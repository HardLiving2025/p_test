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
         * 현재 시간 기준 유효 슬롯(타임스탬프) 계산 06:00 ~ 17:59 -> 오늘 06:00 18:00 ~ 05:59 -> (오늘 18:00) 또는 (어제
         * 18:00) 예: 오늘 19시 -> 오늘 18시 예: 내일 04시 -> 오늘 18시
         */
        fun calculateCurrentSlotTime(): Long {
            val cal = java.util.Calendar.getInstance()
            val hour = cal.get(java.util.Calendar.HOUR_OF_DAY)

            // 분/초/밀리초 0으로 초기화 (시작 시간 기준)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)

            if (hour in 6..17) {
                // 06:00 ~ 17:59 -> 06:00
                cal.set(java.util.Calendar.HOUR_OF_DAY, 6)
            } else {
                // 18:00 ~ 05:59 -> 18:00 (전날일 수도 있음)
                if (hour < 6) {
                    // 새벽 00시 ~ 05시 -> 전날 18시
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
     * 앱 시작 시 이동할 목적지 결정 토큰 없음 -> onboarding 토큰 있음 + 이번 슬롯 입력 완료 -> home 토큰 있음 + 입력 필요 -> mood
     * (LoginScreen은 자동 로그인 처리 가정 - 여기서는 'mood'로 바로 가거나 'login' 거쳐감)
     * * LoginScreen에서 토큰 있으면 바로 mood로 가는 로직이 있다면 'login'으로 보내도 됨.
     * * 요구사항: "입력 된 상태일때는 ... 다음 단계로 바로 넘어가게"
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

        // 로그인 되어 있고 입력 안했으면 Login (자동 로그인 체크 후 Mood로 이동될 것임)
        // 또는 바로 'mood'로 보낼 수도 있지만, User 정보를 로드해야 할 수도 있으니
        // 안전하게 'login'으로 보내고 LoginScreen에서 토큰 체크 후 skip 하게 하는 게 일반적.
        // 하지만 요구사항상 "입력창이 바로 안뜨고"라고 했으므로,
        // 입력 해야하면 'mood' (로그인 상태 가정), 안해도 되면 'home'.
        // LoginScreen이 "이미 로그인됨 -> navigate" 로직이 있다면 'login'이 맞음.
        // 여기서는 안전하게 'login'을 리턴하고, LoginScreen에서 분기하도록 하거나,
        // AppNav에서 처리. 여기서는 단순 판단 로직만 제공.
        return "login_gate"
    }
}
