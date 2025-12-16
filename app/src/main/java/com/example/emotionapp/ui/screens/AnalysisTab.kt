/**
 * 감정별 평균 사용량 → EmotionUsageSection.kt 시간대별 평균 사용량 → TimeUsageSection.kt 감정/상황별 총 사용량 →
 * MoodStateUsageSection.kt 위험 감정 조합 → RiskCombinationSection.kt 주요 패턴(인사이트) → KeyPatternsSection.kt
 */
package com.example.emotionapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.components.analysis.EmotionUsageSection
import com.example.emotionapp.ui.components.analysis.KeyPatternsSection
import com.example.emotionapp.ui.components.analysis.MoodStateUsageSection
import com.example.emotionapp.ui.components.analysis.RiskCombinationSection
import com.example.emotionapp.ui.components.analysis.TimeUsageSection
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite

@Composable
fun AnalysisTab(period: Period, refreshTrigger: Int = 0) {
        var showDetail by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxSize().padding(Spacing.ScreenPadding)) {
                // 헤더 카드
                AnalysisHeaderCard(period = period)

                Spacer(modifier = Modifier.height(Spacing.M))

                // 감정별 평균 사용량 + 앱 상세
                EmotionUsageSection(
                        period = period,
                        showDetail = showDetail,
                        onToggleDetail = { showDetail = !showDetail }
                )
                Spacer(modifier = Modifier.height(Spacing.M))

                // 시간대별 평균 사용량
                TimeUsageSection(period = period, refreshTrigger = refreshTrigger)
                Spacer(modifier = Modifier.height(Spacing.M))

                // 감정/상황별 총 사용량
                MoodStateUsageSection(period = period)
                Spacer(modifier = Modifier.height(Spacing.M))

                // 위험 감정 조합
                RiskCombinationSection(period = period)
                Spacer(modifier = Modifier.height(Spacing.M))

                // 주요 패턴(인사이트)
                KeyPatternsSection(period = period)
        }
}

@Composable
private fun AnalysisHeaderCard(period: Period) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "분석",
                        fontSize = FontSizes.Title,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBrown
                )
                Spacer(modifier = Modifier.height(Spacing.S))
                Text(
                        text =
                                when (period) {
                                        Period.YESTERDAY -> "어제의 패턴을 분석했어요"
                                        Period.WEEK -> "최근 일주일의 패턴을 분석했어요"
                                        Period.TWO_WEEKS -> "최근 2주일의 패턴을 분석했어요"
                                        Period.MONTH -> "최근 한달의 패턴을 분석했어요"
                                },
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                )
        }
}
