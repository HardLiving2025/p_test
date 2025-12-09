// WeeklyUsageExporter.kt
package com.example.emotionapp.data

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * 앱 사용 세션 데이터 모델
 * packageName: 어떤 앱인지
 * startTime: 세션 시작 (ms)
 * endTime: 세션 종료 (ms)
 */
data class AppUsageSession(
    val packageName: String,
    val startTime: Long,
    val endTime: Long
)

/**
 * 최근 7일간 앱 사용 세션을 (RESUMED → PAUSED) 구조로 수집
 */
fun getWeeklyUsageSessions(context: Context): List<AppUsageSession> {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val endTime = System.currentTimeMillis()
    val startTime = endTime - 7 * 24 * 60 * 60 * 1000L     // 최근 7일

    val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
    val sessions = mutableListOf<AppUsageSession>()

    var currentPackage: String? = null
    var currentStart = -1L

    val event = UsageEvents.Event()

    while (usageEvents.hasNextEvent()) {
        usageEvents.getNextEvent(event)

        when (event.eventType) {

            UsageEvents.Event.ACTIVITY_RESUMED -> {
                // 이전 앱 세션이 열린 상태면 먼저 종료 등록
                if (currentPackage != null && currentStart > 0) {
                    sessions.add(
                        AppUsageSession(
                            packageName = currentPackage!!,
                            startTime = currentStart,
                            endTime = event.timeStamp
                        )
                    )
                }
                // 새 앱 세션 시작
                currentPackage = event.packageName
                currentStart = event.timeStamp
            }

            UsageEvents.Event.ACTIVITY_PAUSED -> {
                // 세션 닫기
                if (currentPackage != null && currentStart > 0) {
                    sessions.add(
                        AppUsageSession(
                            packageName = currentPackage!!,
                            startTime = currentStart,
                            endTime = event.timeStamp
                        )
                    )
                }
                currentPackage = null
                currentStart = -1L
            }
        }
    }

    // 아직 열린 세션이 있으면 endTime 기준으로 종료
    if (currentPackage != null && currentStart > 0) {
        sessions.add(
            AppUsageSession(
                packageName = currentPackage!!,
                startTime = currentStart,
                endTime = endTime
            )
        )
    }

    return sessions
}

/**
 * 세션 리스트를 JSON 문자열로 직렬화
 */
fun sessionsToJson(sessions: List<AppUsageSession>): String {
    val jsonArray = JSONArray()

    sessions.forEach { session ->
        val obj = JSONObject()
        obj.put("package_name", session.packageName)
        obj.put("start_time", session.startTime)
        obj.put("end_time", session.endTime)
        jsonArray.put(obj)
    }

    return jsonArray.toString()
}

/**
 * "일주일 사용 기록 전체"를 JSON으로 반환하는 통합 함수
 */
fun exportWeeklyUsageJson(context: Context): String {
    val sessions = getWeeklyUsageSessions(context)
    return sessionsToJson(sessions)
}

/**
 * JSON을 내부 저장소 파일로 저장
 * 기본 파일 이름: usage_week.json
 *
 * /data/data/<패키지명>/files/usage_week.json 에 자동 저장됨
 */
fun saveWeeklyUsageJsonToFile(
    context: Context,
    fileName: String = "usage_week.json"
) {
    val json = exportWeeklyUsageJson(context)

    context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
        output.write(json.toByteArray())
    }
}

/**
 * 저장된 JSON 파일 읽기
 */
fun readWeeklyUsageJsonFromFile(
    context: Context,
    fileName: String = "usage_week.json"
): String? {
    return try {
        context.openFileInput(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        null
    }
}
