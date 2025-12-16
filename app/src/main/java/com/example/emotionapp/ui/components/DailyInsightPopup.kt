package com.example.emotionapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.emotionapp.ui.theme.*

@Composable
fun DailyInsightPopup(onClose: () -> Unit) {
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
            Text(text = "어제 행동 패턴 분석", fontSize = FontSizes.SemiBold, color = PrimaryBrown)

            Spacer(modifier = Modifier.height(Spacing.M))

            Text(
                    text = "최근 3번 연속 경고를 무시하셨어요. 지금은 충동성이 약간 높은 시기로 분석됩니다.",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(Spacing.XL))

            Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrown),
                    shape = RoundedCornerShape(Spacing.L)
            ) { Text(text = "확인", color = SurfaceWhite, fontSize = FontSizes.Normal) }
        }
    }
}
