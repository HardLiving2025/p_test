package com.example.emotionapp.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

/** API 응답 데이터 모델 */
data class SlotUsageAverage(
        val slot: Int,
        val startTime: String, // "00:00"
        val endTime: String, // "00:30"
        val sns: Long,
        val game: Long,
        val other: Long,
        val total: Long
)

data class UsageAverageResponse(
        val yesterday: List<SlotUsageAverage>,
        val week1: List<SlotUsageAverage>,
        val week2: List<SlotUsageAverage>,
        val month1: List<SlotUsageAverage>
)

object UsageAnalysisManager {
        // API URL
        private const val URL =
                "http://ceprj2.gachon.ac.kr:65042/api/analysis/usage-by-slot-average"

        // JWT 토큰 (ServerUploadManager와 동일)
        private val AUTH_TOKEN = TokenManager.AUTH_TOKEN

        private val client = OkHttpClient()

        /**
         * 서버에서 시간대별 평균 사용량 데이터를 가져옵니다.
         * @param onResult 콜백 (성공 시 데이터, 실패 시 null)
         */
        fun fetchUsageAverages(onResult: (UsageAverageResponse?) -> Unit) {
                val request =
                        Request.Builder()
                                .url(URL)
                                .addHeader("Authorization", "Bearer $AUTH_TOKEN")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e(
                                                        "UsageAnalysis",
                                                        "❌ Failed to fetch usage averages",
                                                        e
                                                )
                                                postResult(onResult, null)
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "❌ Server error: ${response.code} ${response.message}"
                                                                )
                                                                postResult(onResult, null)
                                                                return
                                                        }

                                                        val body = response.body?.string()
                                                        if (body == null) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "❌ Response body is null"
                                                                )
                                                                postResult(onResult, null)
                                                                return
                                                        }

                                                        // ✅ 원본 JSON 로그
                                                        Log.d(
                                                                "UsageAnalysis",
                                                                "✅ Raw response body:\n$body"
                                                        )

                                                        try {
                                                                val json = JSONObject(body)
                                                                val result =
                                                                        UsageAverageResponse(
                                                                                yesterday =
                                                                                        parseSlots(
                                                                                                json.optJSONArray(
                                                                                                        "yesterday"
                                                                                                )
                                                                                        ),
                                                                                week1 =
                                                                                        parseSlots(
                                                                                                json.optJSONArray(
                                                                                                        "week_1"
                                                                                                )
                                                                                        ),
                                                                                week2 =
                                                                                        parseSlots(
                                                                                                json.optJSONArray(
                                                                                                        "week_2"
                                                                                                )
                                                                                        ),
                                                                                month1 =
                                                                                        parseSlots(
                                                                                                json.optJSONArray(
                                                                                                        "month_1"
                                                                                                )
                                                                                        )
                                                                        )

                                                                Log.d(
                                                                        "UsageAnalysis",
                                                                        "✅ Successfully parsed data"
                                                                )

                                                                // ✅ 파싱된 데이터 상세 로그 출력
                                                                logUsageAverages(
                                                                        "ServerResponse",
                                                                        result
                                                                )

                                                                postResult(onResult, result)
                                                        } catch (e: Exception) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "❌ Parsing error",
                                                                        e
                                                                )
                                                                postResult(onResult, null)
                                                        }
                                                }
                                        }
                                }
                        )
        }

        /** 메인 스레드로 콜백 전달 */
        private fun postResult(
                onResult: (UsageAverageResponse?) -> Unit,
                data: UsageAverageResponse?
        ) {
                Handler(Looper.getMainLooper()).post { onResult(data) }
        }

        /** JSON 배열 -> SlotUsageAverage 리스트로 변환 */
        private fun parseSlots(array: org.json.JSONArray?): List<SlotUsageAverage> {
                val list = mutableListOf<SlotUsageAverage>()
                if (array == null) return list

                for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val slot = obj.optInt("slot")

                        // startTime/endTime을 slot 기반으로 계산하여 "nu" 문제 방지
                        val startTotalMin = slot * 30
                        val startH = startTotalMin / 60
                        val startM = startTotalMin % 60
                        val startTimeStr = "%02d:%02d".format(startH, startM)

                        val endTotalMin = startTotalMin + 30
                        val endH = endTotalMin / 60
                        val endM = endTotalMin % 60
                        val endTimeStr = "%02d:%02d".format(endH, endM)

                        list.add(
                                SlotUsageAverage(
                                        slot = slot,
                                        startTime = startTimeStr,
                                        endTime = endTimeStr,
                                        sns = obj.optLong("sns"),
                                        game = obj.optLong("game"),
                                        other = obj.optLong("other"),
                                        total = obj.optLong("total")
                                )
                        )
                }
                // 슬롯 순서대로 정렬
                return list.sortedBy { it.slot }
        }

        /** ✅ 디버깅용: 파싱된 UsageAverageResponse 내용을 예쁘게 로그로 출력 */
        private fun logUsageAverages(tag: String, data: UsageAverageResponse?) {
                if (data == null) {
                        Log.e("UsageAnalysis", "[$tag] ❌ 서버 응답 NULL (파싱 실패 또는 서버 오류)")
                        return
                }

                fun logSlotList(title: String, list: List<SlotUsageAverage>) {
                        Log.d("UsageAnalysis", "-------------------------------")
                        Log.d("UsageAnalysis", "📌 $title (${list.size} slots)")
                        Log.d("UsageAnalysis", "-------------------------------")

                        list.forEach { slot ->
                                Log.d(
                                        "UsageAnalysis",
                                        "slot=${slot.slot}, ${slot.startTime}~${slot.endTime}, " +
                                                "sns=${slot.sns}, game=${slot.game}, other=${slot.other}, total=${slot.total}"
                                )
                        }
                }

                logSlotList("어제(yesterday)", data.yesterday)
                logSlotList("1주차 평균(week_1)", data.week1)
                logSlotList("2주차 평균(week_2)", data.week2)
                logSlotList("1개월 평균(month_1)", data.month1)
        }
}
