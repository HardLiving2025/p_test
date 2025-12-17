package com.example.emotionapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.emotionapp.data.NotificationManager

class DailyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("DailyNotification", "Alarm received. Fetching notification...")

        // 권한 체크는 NotificationHelper.showNotification 내부에서 처리됨 (Android 13+)
        // 알림 메시지 서버에서 가져오기
        NotificationManager.fetchNotificationMessage(
                context,
                onResult = { response ->
                    NotificationHelper.showNotification(context, response.risk_level, response.body)
                },
                onError = { Log.e("DailyNotification", "Failed to fetch notification") }
        )
    }
}
