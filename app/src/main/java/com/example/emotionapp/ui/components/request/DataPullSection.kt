package com.example.emotionapp.ui.components.request

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.emotionapp.data.ServerUploadManager
import com.example.emotionapp.data.readUsageJsonFromFile
import com.example.emotionapp.data.save10DaysUsageJsonToFile
import com.example.emotionapp.ui.theme.*

@Composable
fun DataPullSection(onComplete: () -> Unit) {
    val context = LocalContext.current

    Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.L),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = "초기 데이터 설정",
                fontSize = FontSizes.Title,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
        )
        Spacer(modifier = Modifier.height(Spacing.L))
        Text(
                text = "초기 분석을 위해 최근 10일간의\n앱 사용 기록을 불러옵니다.",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.XXL))

        Button(
                onClick = {
                    // 1. 10일치 저장
                    save10DaysUsageJsonToFile(context)

                    // 2. 파일 읽기
                    val json = readUsageJsonFromFile(context, "usage_10_days.json")

                    if (json != null) {
                        Log.d("RequestScreen", "Saved 10-day JSON: $json")

                        // 3. 서버 전송
                        ServerUploadManager.uploadJson(context, json) { success, message ->
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onComplete() // 완료 시 다음 화면으로 (MoodSelector)
                                }
                            }
                        }
                    } else {
                        Log.e("RequestScreen", "Failed to read 10-day JSON file")
                        Toast.makeText(context, "데이터 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                },
                shape = RoundedCornerShape(Spacing.M),
                modifier = Modifier.fillMaxWidth(0.8f)
        ) { Text(text = "10일 사용 기록 불러오기", fontSize = FontSizes.Normal) }
    }
}
