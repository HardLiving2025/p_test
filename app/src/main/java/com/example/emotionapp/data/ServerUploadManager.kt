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
    // TODO: 사용자가 제공한 서버 주소로 변경 필요. 현재는 이미지의 주소를 바탕으로 추정한 예시입니다.
    // 포트 번호: 65042
    // 경로: v0.7src/mobile
    private const val DAILY_SUMMARY_URL =
            "http://ceprj2.gachon.ac.kr:65042/api/daily-summary/upload"
    private const val BATCH_URL = "http://ceprj2.gachon.ac.kr:65042/api/usage/batch"

    // ✅ JWT 토큰
    private const val AUTH_TOKEN =
            "eyJhbGciOiAiSFMyNTYiLCAidHlwIjogIkpXVCJ9.eyJzdWIiOiAiMSIsICJleHAiOiAxNzY2MDQ4OTA0fQ.nuMeRx09mGAVuMICqXfYcH1MwFixpEenVZDNXb_36rA"

    private val client = OkHttpClient()
    private val JSON_MEDIA_type = "application/json; charset=utf-8".toMediaType()

    fun uploadJson(jsonString: String, onResult: (Boolean, String) -> Unit) {
        // 1. Daily Summary 업로드
        uploadToUrl(DAILY_SUMMARY_URL, jsonString) { success1, msg1 ->
            // 2. Batch 업로드 (Daily Summary 결과와 무관하게 시도)
            uploadToUrl(BATCH_URL, jsonString) { success2, msg2 ->
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

    private fun uploadToUrl(url: String, jsonString: String, onResult: (Boolean, String) -> Unit) {
        val requestBody = jsonString.toRequestBody(JSON_MEDIA_type)
        val request =
                Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer $AUTH_TOKEN")
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
                                        Log.e(
                                                "ServerUpload",
                                                "Upload to $url failed: ${response.code}"
                                        )
                                        onResult(false, "Error code: ${response.code}")
                                    }
                                }
                            }
                        }
                )
    }
}
