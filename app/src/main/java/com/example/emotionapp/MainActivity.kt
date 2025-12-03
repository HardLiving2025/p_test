package com.example.emotionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EmotionScreen()
        }
    }

    @Composable
    fun EmotionScreen() {
        var resultText by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("감정 입력 테스트", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                sendEmotion("good", "free") { response ->
                    resultText = response
                }
            }) {
                Text("감정 보내기 (good / free)")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(text = "서버 응답:")
            Text(text = resultText)
        }
    }

    private fun sendEmotion(emotion: String, state: String, callback: (String) -> Unit) {
        val json = """
            {
                "emotion": "$emotion",
                "state": "$state"
            }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://210.102.178.151:65042/emotion")   // ★ 너의 서버 주소
            .post(requestBody)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body?.string() ?: "응답 없음"
                    withContext(Dispatchers.Main) {
                        callback(body)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback("에러: ${e.message}")
                }
            }
        }
    }
}
