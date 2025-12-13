package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.data.SlotUsageAverage
import com.example.emotionapp.data.UsageAnalysisManager
import com.example.emotionapp.data.UsageAverageResponse
import com.example.emotionapp.ui.screens.Period
import com.example.emotionapp.ui.theme.*

@Composable
fun TimeUsageSection(period: Period, refreshTrigger: Int = 0) {
        var usageResponse by remember { mutableStateOf<UsageAverageResponse?>(null) }

        // 데이터 로드
        LaunchedEffect(Unit, refreshTrigger) {
                UsageAnalysisManager.fetchUsageAverages { response -> usageResponse = response }
        }

        // 선택된 탭에 따른 데이터 추출
        val currentData: List<SlotUsageAverage> =
                usageResponse?.let {
                        when (period) {
                                Period.YESTERDAY -> it.yesterday
                                Period.WEEK -> it.week1
                                Period.TWO_WEEKS -> it.week2
                                Period.MONTH -> it.month1
                        }
                }
                        ?: emptyList()

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = "시간대별 평균 사용량 (분)",
                                fontSize = FontSizes.SemiBold,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBrown
                        )
                }

                Spacer(modifier = Modifier.height(Spacing.M))

                // 꺾은선 그래프
                TimeUsageLineChart(currentData)
        }
}
