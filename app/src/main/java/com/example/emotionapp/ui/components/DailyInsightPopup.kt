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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

@Composable
fun DailyInsightPopup(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),   // 반투명 오버레이
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(SurfaceWhite, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "어제 행동 패턴 분석",
                fontSize = FontSizes.SemiBold,
                color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "최근 3번 연속 경고를 무시하셨어요. 지금은 충동성이 약간 높은 시기로 분석됩니다.",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onClose,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBrown
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "확인",
                    color = SurfaceWhite,
                    fontSize = FontSizes.Normal
                )
            }
        }
    }
}
