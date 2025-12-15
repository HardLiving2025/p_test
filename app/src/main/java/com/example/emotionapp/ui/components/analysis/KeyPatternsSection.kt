package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.UsageAnalysisManager
import com.example.emotionapp.ui.theme.*

@Composable
fun KeyPatternsSection(period: com.example.emotionapp.ui.screens.Period) {
        val context = androidx.compose.ui.platform.LocalContext.current
        var insights by remember {
                mutableStateOf<List<UsageAnalysisManager.PatternInsight>>(emptyList())
        }

        LaunchedEffect(period) {
                UsageAnalysisManager.fetchMajorPatterns(context) { result ->
                        if (result != null) {
                                insights =
                                        when (period) {
                                                com.example.emotionapp.ui.screens.Period
                                                        .YESTERDAY -> result.yesterday
                                                com.example.emotionapp.ui.screens.Period.WEEK ->
                                                        result.week1
                                                com.example.emotionapp.ui.screens.Period
                                                        .TWO_WEEKS -> result.week2
                                                com.example.emotionapp.ui.screens.Period.MONTH ->
                                                        result.month1
                                        }
                        }
                }
        }

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "주요 패턴",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                if (insights.isEmpty()) {
                        Text(
                                text = "아직 분석된 패턴이 없습니다.",
                                fontSize = FontSizes.Normal,
                                color = DisabledGray,
                                modifier = Modifier.padding(vertical = Spacing.S)
                        )
                } else {
                        insights.forEach { insight ->
                                InsightBlock(chipText = insight.title, body = insight.description)
                                Spacer(modifier = Modifier.height(Spacing.S))
                        }
                }
        }
}

@Composable
private fun InsightBlock(chipText: String, body: String) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundBeige, RoundedCornerShape(Spacing.M))
                                .padding(Spacing.M)
        ) {
                Box(
                        modifier =
                                Modifier.background(AccentBlue, RoundedCornerShape(999.dp))
                                        .padding(horizontal = Spacing.S, vertical = Spacing.XS)
                ) { Text(text = chipText, fontSize = FontSizes.Normal, color = PrimaryBrown) }
                Spacer(modifier = Modifier.height(Spacing.S))
                Text(
                        text = body,
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.8f)
                )
        }
}
