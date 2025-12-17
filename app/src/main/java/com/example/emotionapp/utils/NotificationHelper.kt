package com.example.emotionapp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.emotionapp.R

object NotificationHelper {
    private const val CHANNEL_ID = "emotion_notification_channel"
    private const val CHANNEL_NAME = "Emotion Notifications"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                    NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                        description = "Channel for emotion app notifications"
                    }
            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, riskLevel: String, body: String) {
        // Permission check for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Should handle permission request in Activity/UI, but here we just return safe
                return
            }
        }

        val riskText =
                when (riskLevel) {
                    "SAFE" -> "위험도 낮음"
                    "CAUTION" -> "위험도 중간"
                    "DANGER" -> "위험도 높음"
                    else -> "알림"
                }

        val contentText = "[$riskText] $body"

        val builder =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher_round) // Replace with your app icon
                        .setContentTitle("감정 상태 알림") // Fixed title or use API title if needed
                        .setContentText(contentText)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) { notify(NOTIFICATION_ID, builder.build()) }
    }
}
