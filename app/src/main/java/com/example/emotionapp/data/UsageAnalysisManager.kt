package com.example.emotionapp.data

import android.content.Context
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
        private const val BASE_URL = "http://ceprj2.gachon.ac.kr:65042/api/analysis"

        // 1. TimeUsageSection
        private const val SLOT_USAGE_URL = "$BASE_URL/usage-by-slot-average"
        // 2. MoodStateUsageSection
        private const val EMOTION_STATUS_URL = "$BASE_URL/usage-by-emotion-status"
        // 3. EmotionUsageSection (Bar Chart)
        private const val EMOTION_AVERAGE_URL = "$BASE_URL/usage-by-emotion-average"
        // 4. EmotionUsageSection (App List)
        private const val APP_RATIOS_URL = "$BASE_URL/app-ratios-by-emotion"
        // 5. KeyPatternsSection
        private const val MAJOR_PATTERNS_URL = "$BASE_URL/major-patterns"

        private val client = OkHttpClient()

        // --- 1. usage-by-slot-average ---
        fun fetchUsageAverages(context: Context, onResult: (UsageAverageResponse?) -> Unit) {
                fetch(context, SLOT_USAGE_URL) { body ->
                        if (body == null) {
                                onResult(null)
                                return@fetch
                        }
                        try {
                                val json = JSONObject(body)
                                val result =
                                        UsageAverageResponse(
                                                yesterday =
                                                        parseSlots(json.optJSONArray("yesterday")),
                                                week1 = parseSlots(json.optJSONArray("week_1")),
                                                week2 = parseSlots(json.optJSONArray("week_2")),
                                                month1 = parseSlots(json.optJSONArray("month_1"))
                                        )
                                postResult(onResult, result)
                        } catch (e: Exception) {
                                Log.e("UsageAnalysis", "Error parsing slot usage", e)
                                postResult(onResult, null)
                        }
                }
        }

        private fun parseSlots(array: org.json.JSONArray?): List<SlotUsageAverage> {
                val list = mutableListOf<SlotUsageAverage>()
                if (array == null) return list
                for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val slot = obj.optInt("slot")

                        // 서버에서 start_time, end_time을 준다면 그것을 우선 사용
                        // (User requested schema: start_time, end_time)
                        val serverStartTime = obj.optString("start_time", "")
                        val serverEndTime = obj.optString("end_time", "")

                        val finalStartTime: String
                        val finalEndTime: String

                        if (serverStartTime.isNotEmpty() && serverEndTime.isNotEmpty()) {
                                finalStartTime = serverStartTime
                                finalEndTime = serverEndTime
                        } else {
                                // Fallback: calculate from slot
                                val startTotalMin = slot * 30
                                finalStartTime =
                                        "%02d:%02d".format(startTotalMin / 60, startTotalMin % 60)
                                val endTotalMin = startTotalMin + 30
                                finalEndTime =
                                        "%02d:%02d".format(endTotalMin / 60, endTotalMin % 60)
                        }

                        list.add(
                                SlotUsageAverage(
                                        slot = slot,
                                        startTime = finalStartTime,
                                        endTime = finalEndTime,
                                        sns = obj.optLong("sns"),
                                        game = obj.optLong("game"),
                                        other = obj.optLong("other"),
                                        total = obj.optLong("total")
                                )
                        )
                }
                return list.sortedBy { it.slot }
        }

        // --- 2. usage-by-emotion-status (MoodStateUsageSection) ---
        // Response: Map<PeriodKey, Map<Emotion, Map<Status, Long>>>
        data class EmotionStatusResponse(
                val yesterday: Map<String, Map<String, Long>>,
                val week1: Map<String, Map<String, Long>>,
                val week2: Map<String, Map<String, Long>>,
                val month1: Map<String, Map<String, Long>>
        )

        fun fetchUsageByEmotionStatus(
                context: Context,
                onResult: (EmotionStatusResponse?) -> Unit
        ) {
                fetch(context, EMOTION_STATUS_URL) { body ->
                        if (body == null) {
                                onResult(null)
                                return@fetch
                        }
                        try {
                                val json = JSONObject(body)
                                val result =
                                        EmotionStatusResponse(
                                                yesterday =
                                                        parseEmotionStatusMap(
                                                                json.optJSONObject("yesterday")
                                                        ),
                                                week1 =
                                                        parseEmotionStatusMap(
                                                                json.optJSONObject("week_1")
                                                        ),
                                                week2 =
                                                        parseEmotionStatusMap(
                                                                json.optJSONObject("week_2")
                                                        ),
                                                month1 =
                                                        parseEmotionStatusMap(
                                                                json.optJSONObject("month_1")
                                                        )
                                        )
                                postResult(onResult, result)
                        } catch (e: Exception) {
                                Log.e("UsageAnalysis", "Error parsing emotion status", e)
                                postResult(onResult, null)
                        }
                }
        }

        private fun parseEmotionStatusMap(periodObj: JSONObject?): Map<String, Map<String, Long>> {
                val result = mutableMapOf<String, Map<String, Long>>()
                if (periodObj == null) return result

                val keys = periodObj.keys()
                while (keys.hasNext()) {
                        val emotion = keys.next() // GOOD, NORMAL, BAD
                        val dataObj = periodObj.optJSONObject(emotion) ?: JSONObject()
                        // Data structure: "BUSY": number, "FREE": number
                        result[emotion] =
                                mapOf(
                                        "BUSY" to dataObj.optLong("BUSY", 0L),
                                        "FREE" to dataObj.optLong("FREE", 0L),
                                        "NORMAL" to dataObj.optLong("NORMAL", 0L)
                                )
                }
                return result
        }

        // --- 3. usage-by-emotion-average (EmotionUsageSection - Bar Chart) ---
        // Response: Map<PeriodKey, Map<Emotion, Map<Category, Long>>>
        data class EmotionAverageResponse(
                val yesterday: Map<String, Map<String, Long>>,
                val week1: Map<String, Map<String, Long>>,
                val week2: Map<String, Map<String, Long>>,
                val month1: Map<String, Map<String, Long>>
        )

        fun fetchUsageByEmotionAverage(
                context: Context,
                onResult: (EmotionAverageResponse?) -> Unit
        ) {
                fetch(context, EMOTION_AVERAGE_URL) { body ->
                        if (body == null) {
                                onResult(null)
                                return@fetch
                        }
                        try {
                                val json = JSONObject(body)
                                val result =
                                        EmotionAverageResponse(
                                                yesterday =
                                                        parseEmotionAverageMap(
                                                                json.optJSONObject("yesterday")
                                                        ),
                                                week1 =
                                                        parseEmotionAverageMap(
                                                                json.optJSONObject("week_1")
                                                        ),
                                                week2 =
                                                        parseEmotionAverageMap(
                                                                json.optJSONObject("week_2")
                                                        ),
                                                month1 =
                                                        parseEmotionAverageMap(
                                                                json.optJSONObject("month_1")
                                                        )
                                        )
                                postResult(onResult, result)
                        } catch (e: Exception) {
                                Log.e("UsageAnalysis", "Error parsing emotion average", e)
                                postResult(onResult, null)
                        }
                }
        }

        private fun parseEmotionAverageMap(periodObj: JSONObject?): Map<String, Map<String, Long>> {
                val result = mutableMapOf<String, Map<String, Long>>()
                if (periodObj == null) return result

                val keys = periodObj.keys()
                while (keys.hasNext()) {
                        val emotion = keys.next()
                        val dataObj = periodObj.optJSONObject(emotion) ?: JSONObject()
                        // JSON 키가 대소문자 섞여 있을 수 있으므로 둘 다 확인
                        val sns =
                                if (dataObj.has("SNS")) dataObj.optLong("SNS")
                                else dataObj.optLong("sns", 0L)
                        val game =
                                if (dataObj.has("GAME")) dataObj.optLong("GAME")
                                else dataObj.optLong("game", 0L)
                        val other =
                                if (dataObj.has("OTHER")) dataObj.optLong("OTHER")
                                else dataObj.optLong("other", 0L)

                        result[emotion] = mapOf("SNS" to sns, "GAME" to game, "OTHER" to other)
                }
                return result
        }

        // --- 4. app-ratios-by-emotion (EmotionUsageSection - App List) ---
        data class AppUsageDetail(
                val appName: String, // "app_name" in JSON
                val pkgName: String, // "app" in JSON
                val totalTime: Long // "ms" in JSON
        )

        // Response: Map<PeriodKey, Map<Emotion, List<AppUsageDetail>>>
        data class AppRatiosResponse(
                val yesterday: Map<String, List<AppUsageDetail>>,
                val week1: Map<String, List<AppUsageDetail>>,
                val week2: Map<String, List<AppUsageDetail>>,
                val month1: Map<String, List<AppUsageDetail>>
        )

        fun fetchAppRatiosByEmotion(context: Context, onResult: (AppRatiosResponse?) -> Unit) {
                fetch(context, APP_RATIOS_URL) { body ->
                        if (body == null) {
                                onResult(null)
                                return@fetch
                        }
                        try {
                                val json = JSONObject(body)
                                val result =
                                        AppRatiosResponse(
                                                yesterday =
                                                        parseAppRatiosMap(
                                                                json.optJSONObject("yesterday")
                                                        ),
                                                week1 =
                                                        parseAppRatiosMap(
                                                                json.optJSONObject("week_1")
                                                        ),
                                                week2 =
                                                        parseAppRatiosMap(
                                                                json.optJSONObject("week_2")
                                                        ),
                                                month1 =
                                                        parseAppRatiosMap(
                                                                json.optJSONObject("month_1")
                                                        )
                                        )
                                postResult(onResult, result)
                        } catch (e: Exception) {
                                Log.e("UsageAnalysis", "Error parsing app ratios", e)
                                postResult(onResult, null)
                        }
                }
        }

        private fun parseAppRatiosMap(periodObj: JSONObject?): Map<String, List<AppUsageDetail>> {
                val result = mutableMapOf<String, List<AppUsageDetail>>()
                if (periodObj == null) return result

                val keys = periodObj.keys() // GOOD, NORMAL, BAD
                while (keys.hasNext()) {
                        val emotion = keys.next()
                        val list = mutableListOf<AppUsageDetail>()
                        val arr = periodObj.optJSONArray(emotion) ?: org.json.JSONArray()
                        for (i in 0 until arr.length()) {
                                val item = arr.getJSONObject(i)
                                list.add(
                                        AppUsageDetail(
                                                appName = item.optString("app_name", "Unknown"),
                                                pkgName = item.optString("app", ""),
                                                totalTime = item.optLong("ms", 0L)
                                        )
                                )
                        }
                        result[emotion] = list
                }
                return result
        }

        // --- 5. major-patterns (KeyPatternsSection) ---
        data class PatternInsight(val title: String, val description: String)

        // Response: Map<PeriodKey, List<PatternInsight>>
        data class MajorPatternsResponse(
                val yesterday: List<PatternInsight>,
                val week1: List<PatternInsight>,
                val week2: List<PatternInsight>,
                val month1: List<PatternInsight>
        )

        fun fetchMajorPatterns(context: Context, onResult: (MajorPatternsResponse?) -> Unit) {
                fetch(context, MAJOR_PATTERNS_URL) { body ->
                        if (body == null) {
                                onResult(null)
                                return@fetch
                        }
                        try {
                                val json = JSONObject(body)
                                val result =
                                        MajorPatternsResponse(
                                                yesterday =
                                                        parsePatternsList(
                                                                json.optJSONArray("yesterday")
                                                        ),
                                                week1 =
                                                        parsePatternsList(
                                                                json.optJSONArray("week_1")
                                                        ),
                                                week2 =
                                                        parsePatternsList(
                                                                json.optJSONArray("week_2")
                                                        ),
                                                month1 =
                                                        parsePatternsList(
                                                                json.optJSONArray("month_1")
                                                        )
                                        )
                                postResult(onResult, result)
                        } catch (e: Exception) {
                                Log.e("UsageAnalysis", "Error parsing major patterns", e)
                                postResult(onResult, null)
                        }
                }
        }

        private fun parsePatternsList(arr: org.json.JSONArray?): List<PatternInsight> {
                val list = mutableListOf<PatternInsight>()
                if (arr == null) return list
                for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        list.add(
                                PatternInsight(
                                        title = item.optString("title"),
                                        description = item.optString("content") // "content" in JSON
                                )
                        )
                }
                return list
        }

        // --- Helpers ---
        private fun fetch(context: Context, url: String, onBody: (String?) -> Unit) {
                val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(url)
                                .addHeader("Authorization", "Bearer $token")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e("UsageAnalysis", "Failed fetch: $url", e)
                                                onBody(null)
                                        }
                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                Log.e(
                                                                        "UsageAnalysis",
                                                                        "Server Error ${response.code} for $url"
                                                                )
                                                                onBody(null)
                                                                return
                                                        }
                                                        val bodyStr = response.body?.string()
                                                        Log.d(
                                                                "UsageAnalysis",
                                                                "[$url] Response: $bodyStr"
                                                        )
                                                        onBody(bodyStr)
                                                }
                                        }
                                }
                        )
        }

        private fun <T> postResult(onResult: (T?) -> Unit, data: T?) {
                Handler(Looper.getMainLooper()).post { onResult(data) }
        }
}
