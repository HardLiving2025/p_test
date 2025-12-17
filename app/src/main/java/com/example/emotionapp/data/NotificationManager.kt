package com.example.emotionapp.data

import android.content.Context
import android.util.Log
import com.example.emotionapp.data.local.TokenManager
import java.io.IOException
import okhttp3.*
import org.json.JSONObject

object NotificationManager {
        private const val RECENT_NOTIFICATIONS_URL =
                "http://ceprj2.gachon.ac.kr:65042/api/notifications/recent"
        private val client = OkHttpClient()

        data class NotificationFromServer(
                val noti_id: Int,
                val message_type: String, // "2025-12-17 10:49:39" (Time)
                val message_body: String, // Content
                val sent_at: String
        )

        fun fetchRecentNotifications(
                context: Context,
                onResult: (List<NotificationFromServer>) -> Unit,
                onError: (String) -> Unit
        ) {
                val token = TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(RECENT_NOTIFICATIONS_URL)
                                .addHeader("Authorization", "Bearer $token")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e(
                                                        "NotificationManager",
                                                        "Failed to fetch notifications",
                                                        e
                                                )
                                                onError(e.message ?: "Unknown fetch error")
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                val errorBody =
                                                                        response.body?.string()
                                                                Log.e(
                                                                        "NotificationManager",
                                                                        "Fetch failed: ${response.code}, body: $errorBody"
                                                                )
                                                                onError("Error: ${response.code}")
                                                                return
                                                        }

                                                        val jsonString = response.body?.string()
                                                        if (jsonString != null) {
                                                                Log.d(
                                                                        "NotificationManager",
                                                                        "Received JSON: $jsonString"
                                                                ) // Log requested by user
                                                                try {
                                                                        val jsonObject =
                                                                                JSONObject(
                                                                                        jsonString
                                                                                )
                                                                        val jsonArray =
                                                                                jsonObject
                                                                                        .getJSONArray(
                                                                                                "recent_notifications"
                                                                                        )
                                                                        val list =
                                                                                mutableListOf<
                                                                                        NotificationFromServer>()

                                                                        for (i in
                                                                                0 until
                                                                                        jsonArray
                                                                                                .length()) {
                                                                                val item =
                                                                                        jsonArray
                                                                                                .getJSONObject(
                                                                                                        i
                                                                                                )
                                                                                list.add(
                                                                                        NotificationFromServer(
                                                                                                noti_id =
                                                                                                        item.getInt(
                                                                                                                "noti_id"
                                                                                                        ),
                                                                                                message_type =
                                                                                                        item.getString(
                                                                                                                "message_type"
                                                                                                        ),
                                                                                                message_body =
                                                                                                        item.getString(
                                                                                                                "message_body"
                                                                                                        ),
                                                                                                sent_at =
                                                                                                        item.getString(
                                                                                                                "sent_at"
                                                                                                        )
                                                                                        )
                                                                                )
                                                                        }
                                                                        onResult(list)
                                                                } catch (e: Exception) {
                                                                        Log.e(
                                                                                "NotificationManager",
                                                                                "Parsing error",
                                                                                e
                                                                        )
                                                                        onError(
                                                                                "Parsing error: ${e.message}"
                                                                        )
                                                                }
                                                        } else {
                                                                onError("Empty response body")
                                                        }
                                                }
                                        }
                                }
                        )
        }
        private const val MESSAGE_URL = "http://ceprj2.gachon.ac.kr:65042/api/notifications/message"

        data class NotificationMessageResponse(
                val title: String,
                val body: String,
                val risk_level: String // SAFE, CAUTION, DANGER
        )

        fun fetchNotificationMessage(
                context: Context,
                onResult: (NotificationMessageResponse) -> Unit,
                onError: (String) -> Unit
        ) {
                val token = TokenManager(context).getAccessToken()
                val request =
                        Request.Builder()
                                .url(MESSAGE_URL)
                                .addHeader("Authorization", "Bearer $token")
                                .get()
                                .build()

                client.newCall(request)
                        .enqueue(
                                object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                                Log.e(
                                                        "NotificationManager",
                                                        "Failed to fetch message",
                                                        e
                                                )
                                                onError(e.message ?: "Unknown fetch error")
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                                response.use {
                                                        if (!response.isSuccessful) {
                                                                val errorBody =
                                                                        response.body?.string()
                                                                Log.e(
                                                                        "NotificationManager",
                                                                        "Message fetch failed: ${response.code}, body: $errorBody"
                                                                )
                                                                onError("Error: ${response.code}")
                                                                return
                                                        }

                                                        val jsonString = response.body?.string()
                                                        if (jsonString != null) {
                                                                Log.d(
                                                                        "NotificationManager",
                                                                        "Message API Response: $jsonString"
                                                                )
                                                                try {
                                                                        val jsonObject =
                                                                                JSONObject(
                                                                                        jsonString
                                                                                )
                                                                        val title =
                                                                                jsonObject
                                                                                        .optString(
                                                                                                "title",
                                                                                                ""
                                                                                        )
                                                                        val body =
                                                                                jsonObject
                                                                                        .optString(
                                                                                                "body",
                                                                                                ""
                                                                                        )
                                                                        val riskLevel =
                                                                                jsonObject
                                                                                        .getString(
                                                                                                "risk_level"
                                                                                        )

                                                                        onResult(
                                                                                NotificationMessageResponse(
                                                                                        title,
                                                                                        body,
                                                                                        riskLevel
                                                                                )
                                                                        )
                                                                } catch (e: Exception) {
                                                                        Log.e(
                                                                                "NotificationManager",
                                                                                "Parsing error",
                                                                                e
                                                                        )
                                                                        onError(
                                                                                "Parsing error: ${e.message}"
                                                                        )
                                                                }
                                                        } else {
                                                                onError("Empty response body")
                                                        }
                                                }
                                        }
                                }
                        )
        }
}
