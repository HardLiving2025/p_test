package com.example.emotionapp.data

import android.util.Log
import java.io.IOException
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

object ServerUploadManager {
    // 포트 번호: 65042
    // 경로: v0.7src/mobile
    private const val DAILY_SUMMARY_URL =
            "http://ceprj2.gachon.ac.kr:65042/api/daily-summary/upload"
    private const val BATCH_URL = "http://ceprj2.gachon.ac.kr:65042/api/usage/batch"

    // Token is retrieved dynamically using Context

    private val client = OkHttpClient()
    private val JSON_MEDIA_type = "application/json; charset=utf-8".toMediaType()
    private const val MOOD_URL = "http://ceprj2.gachon.ac.kr:65042/api/moods"

    fun uploadMoodState(
            context: android.content.Context,
            emotion: String,
            status: String,
            onResult: (Boolean) -> Unit
    ) {
        val jsonString =
                """
            {
                "emotion": "$emotion",
                "status": "$status"
            }
        """.trimIndent()

        uploadToUrl(context, MOOD_URL, jsonString) { success, _ -> onResult(success) }
    }

    fun uploadJson(
            context: android.content.Context,
            jsonString: String,
            onResult: (Boolean, String) -> Unit
    ) {
        // 서버의 두 엔드포인트가 서로 다른 키 이름을 요구함
        // api/usage/batch -> "package_data"
        // api/daily-summary/upload -> "package"

        // 현재 WeeklyUsageExporter는 "package_data"를 생성함.
        // 따라서 daily-summary용으로는 "package"로 치환해야 함.
        val jsonForDaily = jsonString.replace("\"package_data\":", "\"package\":")

        // 1. Daily Summary 업로드
        uploadToUrl(context, DAILY_SUMMARY_URL, jsonForDaily) { success1, msg1 ->
            // 2. Batch 업로드 (Daily Summary 결과와 무관하게 시도)
            uploadToUrl(context, BATCH_URL, jsonString) { success2, msg2 ->
                // 두 결과 종합
                if (success1 && success2) {
                    onResult(true, "모든 서버 전송 성공")
                } else {
                    val errorMsg = buildString {
                        if (!success1) append("Daily Summary 실패: $msg1\n")
                        if (!success2) append("Batch 실패: $msg2")
                    }
                    onResult(false, errorMsg.trim())
                }
            }
        }
    }

    private fun uploadToUrl(
            context: android.content.Context,
            url: String,
            jsonString: String,
            onResult: (Boolean, String) -> Unit
    ) {
        // 전송 데이터 로그 출력
        Log.d("ServerUpload", "Uploading to $url\nPayload: $jsonString")

        val token = com.example.emotionapp.data.local.TokenManager(context).getAccessToken()
        val requestBody = jsonString.toRequestBody(JSON_MEDIA_type)
        val request =
                Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer $token")
                        .post(requestBody)
                        .build()

        client.newCall(request)
                .enqueue(
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                Log.e("ServerUpload", "Upload to $url failed", e)
                                onResult(false, e.message ?: "Unknown error")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                response.use {
                                    if (response.isSuccessful) {
                                        Log.d(
                                                "ServerUpload",
                                                "Upload to $url successful: ${response.code}"
                                        )
                                        onResult(true, "Success")
                                    } else {
                                        val errorDetail = response.body?.string()
                                        Log.e(
                                                "ServerUpload",
                                                "Upload to $url failed: ${response.code} / Body: $errorDetail"
                                        )
                                        onResult(
                                                false,
                                                "Error code: ${response.code}, Msg: $errorDetail"
                                        )
                                    }
                                }
                            }
                        }
                )
    }
}
