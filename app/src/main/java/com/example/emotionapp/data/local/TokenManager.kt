package com.example.emotionapp.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class TokenManager(context: Context) {
    private val prefs: SharedPreferences =
            context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_LAST_INPUT_SLOT = "last_input_slot_time"

        /**
         * 현재 시간 기준 유효 슬롯(타임스탬프) 계산 세션 1: 08:00 ~ 17:59 -> 오늘 08:00 세션 2: 18:00 ~ 다음날 07:59 -> 오늘/어제
         * 18:00 예: 오늘 19시 -> 오늘 18시 예: 내일 04시 -> 오늘 18시
         */
        fun calculateCurrentSlotTime(): Long {
            val cal = java.util.Calendar.getInstance()
            val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)

            // 분/초/밀리초 0으로 초기화 (시작 시간 기준)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)

            if (hour in 8..17) {
                // 08:00 ~ 17:59 -> 08:00 (세션 1)
                cal.set(java.util.Calendar.HOUR_OF_DAY, 8)
            } else {
                // 18:00 ~ 07:59 -> 18:00 (세션 2, 전날일 수도 있음)
                if (hour < 8) {
                    // 새벽 00시 ~ 07:59 -> 전날 18시
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                }
                // 18시로 설정
                cal.set(java.util.Calendar.HOUR_OF_DAY, 18)
            }
            return cal.timeInMillis
        }
    }

    fun saveAccessToken(token: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, token).apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun clearTokens() {
        prefs.edit().clear().apply()
    }

    fun saveLastInputSlot(slotTime: Long) {
        Log.d("TokenManager", "Saving last input slot: $slotTime")
        prefs.edit().putLong(KEY_LAST_INPUT_SLOT, slotTime).apply()
    }

    fun getLastInputSlot(): Long {
        val value = prefs.getLong(KEY_LAST_INPUT_SLOT, 0L)
        Log.d("TokenManager", "Retrieved last input slot: $value")
        return value
    }

    /**
     * 앱 시작 시 이동할 목적지 결정
     * - 토큰 없음 -> onboarding (첫 로그인)
     * - 토큰 있음 + 이번 세션 입력 완료 -> home
     * - 토큰 있음 + 입력 필요 -> mood (감정/상태 입력)
     */
    fun getStartDestination(): String {
        val token = getAccessToken()
        if (token.isNullOrEmpty()) {
            return "onboarding"
        }

        val currentSlot = calculateCurrentSlotTime()
        val lastSlot = getLastInputSlot()

        // 이미 이번 슬롯을 입력했다면 Home
        Log.d("TokenManager", "Slot Check: Current=$currentSlot, Last=$lastSlot")
        if (currentSlot == lastSlot) {
            return "home"
        }

        // 토큰 있고 입력 안했으면 감정 입력 화면으로 직행
        return "mood"
    }
}
