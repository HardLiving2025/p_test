package com.example.emotionapp.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.emotionapp.data.readMonthlyUsageJsonFromFile
import com.example.emotionapp.data.saveMonthlyUsageJsonToFile
import com.example.emotionapp.ui.theme.*

@Composable
fun InitialPull1MonthDataPopup(onClose: () -> Unit, onRefreshNeeded: () -> Unit) {
    val context = LocalContext.current

    Box(
            modifier =
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), // 반투명 오버레이
            contentAlignment = Alignment.Center
    ) {
        Column(
                modifier =
                        Modifier.fillMaxWidth(0.85f)
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.XXL))
                                .padding(Spacing.XL)
        ) {
            Text(text = "버튼을 눌러 정보를 불러와 보세요!", fontSize = FontSizes.SemiBold, color = PrimaryBrown)

            Spacer(modifier = Modifier.height(Spacing.XXL))

            Button(
                    onClick = {
                        // 1) JSON 파일 저장
                        saveMonthlyUsageJsonToFile(context)

                        // 2) 다시 읽어서 내용 확인
                        val json = readMonthlyUsageJsonFromFile(context)

                        if (json != null) {
                            // 3) Logcat에 출력
                            Log.d("UsageJSON", "Saved JSON: $json")

                            // 4) 서버로 전송
                            com.example.emotionapp.data.ServerUploadManager.uploadJson(json!!) {
                                    success,
                                    message ->
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        onRefreshNeeded()
                                    }
                                }
                            }
                        } else {
                            Log.e("UsageJSON", "Failed to read JSON file")
                        }

                        // 5) 사용자에게 토스트 메시지
                        Toast.makeText(context, "10일 사용 기록을 저장하고 서버로 전송합니다.", Toast.LENGTH_SHORT)
                                .show()

                        // 6) 팝업 닫기
                        onClose()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Spacing.L)
            ) { Text(text = "10일 사용 기록 저장", fontSize = FontSizes.Normal) }
        }
    }
}
