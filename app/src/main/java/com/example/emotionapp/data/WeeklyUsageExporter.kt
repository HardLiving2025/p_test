package com.example.emotionapp.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

// ============= 데이터 모델 =============

data class AppUsageSession(
        val packageName: String,
        val appName: String, // 패키지명 그대로 사용 (예: com.nhn.android.webtoon)
        val startTime: Long,
        val endTime: Long,
        val startTimeStr: String, // "yyyy-MM-dd HH:mm:ss"
        val endTimeStr: String,
        val duration: Long // ms
)

data class AppUsageSummary(val appName: String, val totalDuration: Long)

data class DailyUsageSummary(
        val date: String, // "yyyy-MM-dd"
        val totalDuration: Long
)

/**
 * 30분 단위 시간 슬롯 데이터 timeSlot: "HH:mm" (예: "14:00", "14:30") categoryUsage: 해당 슬롯에서의 앱별 사용 시간
 * (AppName -> Duration)
 */
data class TimeSlotUsage(
        val date: String,
        val timeSlot: String,
        val categoryUsage: MutableMap<String, Long> = mutableMapOf()
)

data class WeeklyUsageAnalysis(
        val sessions: List<AppUsageSession>,
        val appSummaries: List<AppUsageSummary>,
        val dailySummaries: List<DailyUsageSummary>,
        val timeSlots: List<TimeSlotUsage>
)

private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000L

// ============= 공개 API =============

/**
 * days 일 기준으로 사용량을 수집해서 JSON 문자열로 반환 예) days = 7 -> 최근 7일
 * ```
 *     days = 30 -> 최근 30일(한 달)
 * ```
 */
fun exportUsageJson(context: Context, days: Int): String {
    val sessions = getUsageSessions(context, days)
    val analysis = analyzeUsage(sessions)
    return analysisToJson(analysis)
}

/** 기존 이름 유지용 래퍼 – “주간” */
fun exportWeeklyUsageJson(context: Context): String = exportUsageJson(context, days = 7)

/** 한 달치(30일) JSON */
fun exportMonthlyUsageJson(context: Context): String = exportUsageJson(context, days = 30)

/** 파일 저장 헬퍼 (기간을 명시적으로 전달) */
fun saveUsageJsonToFile(context: Context, days: Int, fileName: String = "usage_${days}d.json") {
    val json = exportUsageJson(context, days)
    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
        output.write(json.toByteArray())
    }
}

/** 편의 함수 – 기존 주간 파일 이름 유지 */
fun saveWeeklyUsageJsonToFile(context: Context, fileName: String = "usage_week.json") =
        saveUsageJsonToFile(context, days = 7, fileName = fileName)

/** 편의 함수 – 한 달치 파일 */
fun saveMonthlyUsageJsonToFile(context: Context, fileName: String = "usage_month.json") =
        saveUsageJsonToFile(context, days = 30, fileName = fileName)

fun readUsageJsonFromFile(context: Context, fileName: String): String? {
    return try {
        context.openFileInput(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        null
    }
}

/** 편의 함수 – 주간 파일 읽기 */
fun readWeeklyUsageJsonFromFile(context: Context): String? =
        readUsageJsonFromFile(context, "usage_week.json")

/** 편의 함수 – 한 달치 파일 읽기 */
fun readMonthlyUsageJsonFromFile(context: Context): String? =
        readUsageJsonFromFile(context, "usage_month.json")

// ============= 내부 구현부 =============

/** days일 동안의 raw session 데이터 수집 */
private fun getUsageSessions(context: Context, days: Int): List<AppUsageSession> {
    val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val endTime = System.currentTimeMillis()
    val startTime = endTime - days * MILLIS_PER_DAY // 최근 days일

    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val sessions = mutableListOf<AppUsageSession>()

    var currentPackage: String? = null
    var currentStart = -1L
    val event = UsageEvents.Event()

    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun addSession(pkg: String, start: Long, end: Long) {
        if (start >= end) return

        val usageKey = pkg // 패키지명을 그대로 사용
        sessions.add(
                AppUsageSession(
                        packageName = pkg,
                        appName = usageKey,
                        startTime = start,
                        endTime = end,
                        startTimeStr = timeFormat.format(Date(start)),
                        endTimeStr = timeFormat.format(Date(end)),
                        duration = end - start
                )
        )
    }

    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)
        when (event.eventType) {
            UsageEvents.Event.ACTIVITY_RESUMED -> {
                if (currentPackage != null && currentStart > 0) {
                    addSession(currentPackage!!, currentStart, event.timeStamp)
                }
                currentPackage = event.packageName
                currentStart = event.timeStamp
            }
            UsageEvents.Event.ACTIVITY_PAUSED -> {
                if (currentPackage != null && currentStart > 0) {
                    addSession(currentPackage!!, currentStart, event.timeStamp)
                }
                currentPackage = null
                currentStart = -1L
            }
        }
    }

    // 마지막에 열려 있던 세션 정리
    if (currentPackage != null && currentStart > 0) {
        addSession(currentPackage!!, currentStart, endTime)
    }

    return sessions
}

/** raw 세션 → 앱별/일별/시간슬롯 요약 */
private fun analyzeUsage(sessions: List<AppUsageSession>): WeeklyUsageAnalysis {
    // 1. 앱별 총 사용량
    val appMap = mutableMapOf<String, Long>()
    sessions.forEach { appMap[it.appName] = appMap.getOrDefault(it.appName, 0L) + it.duration }
    val appSummaries =
            appMap.map { AppUsageSummary(it.key, it.value) }.sortedByDescending { it.totalDuration }

    // 2. 일별 총 사용량
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dailyMap = mutableMapOf<String, Long>()
    sessions.forEach {
        val dateKey = dateFormat.format(Date(it.startTime))
        dailyMap[dateKey] = dailyMap.getOrDefault(dateKey, 0L) + it.duration
    }
    val dailySummaries = dailyMap.map { DailyUsageSummary(it.key, it.value) }.sortedBy { it.date }

    // 3. 30분 단위 시간 슬롯 분석
    val slotMap = mutableMapOf<String, TimeSlotUsage>()
    val slotDurationMs = 30 * 60 * 1000L
    val timeLabelFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    sessions.forEach { session ->
        var current = session.startTime
        val end = session.endTime

        while (current < end) {
            val rawSlotStart = (current / slotDurationMs) * slotDurationMs
            val nextSlotStart = rawSlotStart + slotDurationMs

            val chunkEnd = minOf(end, nextSlotStart)
            val durationInSlot = chunkEnd - current

            val slotDateObj = Date(rawSlotStart)
            val dateStr = dateFormat.format(slotDateObj)
            val timeStr = timeLabelFormat.format(slotDateObj)
            val uniqueKey = "$dateStr $timeStr"

            val slotUsage = slotMap.getOrPut(uniqueKey) { TimeSlotUsage(dateStr, timeStr) }

            slotUsage.categoryUsage[session.appName] =
                    slotUsage.categoryUsage.getOrDefault(session.appName, 0L) + durationInSlot

            current = chunkEnd
        }
    }

    val timeSlots = slotMap.values.sortedWith(compareBy({ it.date }, { it.timeSlot })).toList()

    return WeeklyUsageAnalysis(sessions, appSummaries, dailySummaries, timeSlots)
}

/**
 * 분석 결과 → JSON 배열 문자열 [ {
 * ```
 *     "usage_date": "2025-12-11",
 *     "time_slot": "14:00",
 *     "package": {
 *       "com.nhn.android.webtoon": 123456,
 *       "com.kakao.talk": 78910
 *     }
 * ```
 * }, ... ]
 */
private fun analysisToJson(analysis: WeeklyUsageAnalysis): String {
    val slotsArray = JSONArray()

    analysis.timeSlots.forEach { slot ->
        val obj = JSONObject()
        obj.put("usage_date", slot.date)
        obj.put("time_slot", slot.timeSlot)

        val usageObj = JSONObject()
        slot.categoryUsage.entries.sortedByDescending { it.value }.forEach { (appName, duration) ->
            usageObj.put(appName, duration)
        }

        obj.put("package", usageObj)
        slotsArray.put(obj)
    }

    return slotsArray.toString(2) // pretty print
}
