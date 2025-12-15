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

        // Token is retrieved dynamically

        private val client = OkHttpClient()

        /**
         * 서버에서 시간대별 평균 사용량 데이터를 가져옵니다.
         * @param context 컨텍스트 (토큰 조회용)
         * @param onResult 콜백 (성공 시 데이터, 실패 시 null)
         */
        fun fetchUsageAverages(
                context: android.content.Context,
                onResult: (UsageAverageResponse?) -> Unit
        ) {
                val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(URL)
                                .addHeader("Authorization", "Bearer $token")
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

        /** 감정별 평균 사용량 API 호출 */
        private const val EMOTION_URL =
                "http://ceprj2.gachon.ac.kr:65042/api/analysis/usage-by-emotion-average"

        fun fetchUsageByEmotionAverage(
                context: android.content.Context,
                onResult: (Map<String, Map<String, Long>>?) -> Unit
        ) {
                val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(EMOTION_URL)
                                .addHeader("Authorization", "Bearer $token")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e(
                                                        "UsageAnalysis",
                                                        "❌ Failed to fetch emotion usage",
                                                        e
                                                )
                                                postResultRaw(onResult, null)
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "❌ Server error: ${response.code}"
                                                                )
                                                                postResultRaw(onResult, null)
                                                                return
                                                        }

                                                        val body = response.body?.string()
                                                        if (body == null) {
                                                                postResultRaw(onResult, null)
                                                                return
                                                        }

                                                        try {
                                                                // JSON 구조:
                                                                // {
                                                                //   "yesterday": { "GOOD": {"SNS":
                                                                // 10, ...}, ... },
                                                                //   ...
                                                                // }
                                                                // 여기서는 "yesterday" 데이터만 사용하거나, 필요에
                                                                // 따라 선택
                                                                val json = JSONObject(body)
                                                                val yesterday =
                                                                        json.optJSONObject(
                                                                                "yesterday"
                                                                        )
                                                                val result =
                                                                        mutableMapOf<
                                                                                String,
                                                                                Map<String, Long>>()

                                                                if (yesterday != null) {
                                                                        val keys = yesterday.keys()
                                                                        while (keys.hasNext()) {
                                                                                val emotion =
                                                                                        keys.next() // GOOD, NORMAL, BAD
                                                                                val cats =
                                                                                        yesterday
                                                                                                .getJSONObject(
                                                                                                        emotion
                                                                                                )

                                                                                val catMap =
                                                                                        mutableMapOf<
                                                                                                String,
                                                                                                Long>()
                                                                                catMap["SNS"] =
                                                                                        cats.optLong(
                                                                                                "SNS"
                                                                                        )
                                                                                catMap["GAME"] =
                                                                                        cats.optLong(
                                                                                                "GAME"
                                                                                        )
                                                                                catMap["OTHER"] =
                                                                                        cats.optLong(
                                                                                                "OTHER"
                                                                                        )

                                                                                result[emotion] =
                                                                                        catMap
                                                                        }
                                                                }
                                                                postResultRaw(onResult, result)
                                                        } catch (e: Exception) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "❌ Parsing error",
                                                                        e
                                                                )
                                                                postResultRaw(onResult, null)
                                                        }
                                                }
                                        }
                                }
                        )
        }

        private fun postResultRaw(
                onResult: (Map<String, Map<String, Long>>?) -> Unit,
                data: Map<String, Map<String, Long>>?
        ) {
                Handler(Looper.getMainLooper()).post { onResult(data) }
        }

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
