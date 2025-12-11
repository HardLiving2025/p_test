package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

@Composable
fun TimeUsageSection() {
        val timeData =
                listOf(
                        TimeUsageData("00-04", sns = 15, other = 20, game = 10),
                        TimeUsageData("04-08", sns = 5, other = 3, game = 2),
                        TimeUsageData("08-12", sns = 30, other = 25, game = 15),
                        TimeUsageData("12-16", sns = 40, other = 35, game = 30),
                        TimeUsageData("16-20", sns = 55, other = 45, game = 35),
                        TimeUsageData("20-24", sns = 70, other = 85, game = 50)
                )

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(16.dp))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "시간대별 평균 사용량 (분)",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                // 꺾은선 그래프
                TimeUsageLineChart(timeData)
        }
}
