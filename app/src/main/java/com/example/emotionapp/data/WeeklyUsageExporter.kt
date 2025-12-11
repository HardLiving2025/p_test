package com.example.emotionapp.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 확장된 데이터 모델
 * Enhanced data models for usage analysis
 */
data class AppUsageSession(
    val packageName: String,
    val appName: String, // 패키지명 뒷부분만 추출 (예: "youtube")
    val startTime: Long,
    val endTime: Long,
    val startTimeStr: String, // 가독성 있는 시간 "yyyy-MM-dd HH:mm:ss"
    val endTimeStr: String,
    val duration: Long // 밀리초 단위
)

data class AppUsageSummary(
    val appName: String,
    val totalDuration: Long
)

data class DailyUsageSummary(
    val date: String, // "yyyy-MM-dd"
    val totalDuration: Long
)

/**
 * 30분 단위 시간 슬롯 데이터
 * timeSlot: "HH:mm" (예: "14:00", "14:30")
 * categoryUsage: 해당 슬롯에서의 앱별 사용 시간 (AppName -> Duration)
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

/**
 * 메인 함수: 데이터 수집 및 분석 통합
 * Main function to collect and analyze usage data
 */
fun exportWeeklyUsageJson(context: Context): String {
    // 1. 원본 세션 데이터 수집
    val sessions = getWeeklyUsageSessions(context)
    
    // 2. 데이터 분석 (요약 및 슬롯 집계)
    val analysis = analyzeUsage(sessions)
    
    // 3. JSON 변환
    return analysisToJson(analysis)
}

/**
 * 헬퍼: 사용 기록 수집
 * Collect raw usage sessions
 */
private fun getWeeklyUsageSessions(context: Context): List<AppUsageSession> {
    val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val pm = context.packageManager
    val endTime = System.currentTimeMillis()
    val startTime = endTime - 7 * 24 * 60 * 60 * 1000L // 최근 7일

    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val sessions = mutableListOf<AppUsageSession>()

    var currentPackage: String? = null
    var currentStart = -1L
    val event = UsageEvents.Event()

    // 시간 포맷터
    val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // 앱 이름 캐시 제거 (패키지명 사용으로 변경)
    // val appNameCache = mutableMapOf<String, String>()

    fun addSession(pkg: String, start: Long, end: Long) {
        if (start >= end) return
        
        // 사용자의 요청: "webtoon" 등 읽기 쉬운 이름 대신 "com.nhn.android.webtoon" 같은 패키지명 사용
        // 따라서 별도의 라벨 조회(getAppLabel) 없이 패키지명을 그대로 사용합니다.
        val usageKey = pkg 

        sessions.add(
            AppUsageSession(
                packageName = pkg,
                appName = usageKey, // Grouping key is now the package name
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

    if (currentPackage != null && currentStart > 0) {
        addSession(currentPackage!!, currentStart, endTime)
    }

    return sessions
}

/**
 * 헬퍼: 데이터 분석 로직
 * Analyze raw sessions into summaries and time slots
 */
private fun analyzeUsage(sessions: List<AppUsageSession>): WeeklyUsageAnalysis {
    // 1. 앱별 총 사용량 요약
    val appMap = mutableMapOf<String, Long>()
    sessions.forEach { 
        appMap[it.appName] = appMap.getOrDefault(it.appName, 0L) + it.duration 
    }
    val appSummaries = appMap.map { AppUsageSummary(it.key, it.value) }.sortedByDescending { it.totalDuration }

    // 2. 일별 총 사용량 요약
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dailyMap = mutableMapOf<String, Long>()
    sessions.forEach {
        val dateKey = dateFormat.format(Date(it.startTime))
        dailyMap[dateKey] = dailyMap.getOrDefault(dateKey, 0L) + it.duration
    }
    val dailySummaries = dailyMap.map { DailyUsageSummary(it.key, it.value) }.sortedBy { it.date }

    // 3. 시간 슬롯 분석 (30분 단위)
    // 맵 키: "yyyy-MM-dd HH:mm" -> TimeSlotUsage
    
    val slotMap = mutableMapOf<String, TimeSlotUsage>()
    // slotFormat is used to create unique keys for slots
    val slotFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    sessions.forEach { session ->
        var current = session.startTime
        val end = session.endTime
        
        while (current < end) {
            // 현재 시간이 속한 30분 슬롯의 시작 시간 계산
            // 예: 14:15 -> 14:00, 다음 슬롯은 14:30
            val slotDurationMs = 30 * 60 * 1000L
            val rawSlotStart = (current / slotDurationMs) * slotDurationMs
            val nextSlotStart = rawSlotStart + slotDurationMs
            
            // 이 슬롯에 걸친 세션 길이 계산
            val chunkEnd = minOf(end, nextSlotStart)
            val durationInSlot = chunkEnd - current
            
            // 키 생성
            val slotDateObj = Date(rawSlotStart)
            val outputDateStr = dateFormat.format(slotDateObj)
            val outputTimeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(slotDateObj)
            val uniqueKey = "$outputDateStr $outputTimeStr"

            val slotUsage = slotMap.getOrPut(uniqueKey) {
                TimeSlotUsage(outputDateStr, outputTimeStr)
            }
            
            // 해당 슬롯에 앱 사용량 누적
            slotUsage.categoryUsage[session.appName] = 
                slotUsage.categoryUsage.getOrDefault(session.appName, 0L) + durationInSlot
            
            current = chunkEnd
        }
    }
    
    val timeSlots = slotMap.values.sortedWith(compareBy({ it.date }, { it.timeSlot })).toList()

    return WeeklyUsageAnalysis(sessions, appSummaries, dailySummaries, timeSlots)
}

/**
 * 헬퍼: JSON 변환
 * Convert analysis result to JSON string (Time Slots)
 */
private fun analysisToJson(analysis: WeeklyUsageAnalysis): String {
    // 사용자가 요청한 포맷: 날짜, 시간 슬롯, 사용량 내역만 포함된 배열
    val slotsArray = JSONArray()
    analysis.timeSlots.forEach { slot ->
        val obj = JSONObject()
        obj.put("usage_date", slot.date)
        obj.put("time_slot", slot.timeSlot)
        
        val usageObj = JSONObject()
        // 사용량 높은 순으로 정렬하여 추가
        slot.categoryUsage.entries.sortedByDescending { it.value }.forEach { (appName, duration) ->
             usageObj.put(appName, duration)
        }
        obj.put("package", usageObj)
        slotsArray.put(obj)
    }

    return slotsArray.toString(2) // 들여쓰기 2칸으로 Pretty Print
}


// 기존 파일 저장 함수 유지
fun saveWeeklyUsageJsonToFile(context: Context, fileName: String = "usage_week.json") {
    val json = exportWeeklyUsageJson(context)
    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
        output.write(json.toByteArray())
    }
}

fun readWeeklyUsageJsonFromFile(context: Context, fileName: String = "usage_week.json"): String? {
    return try {
        context.openFileInput(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        null
    }
}
