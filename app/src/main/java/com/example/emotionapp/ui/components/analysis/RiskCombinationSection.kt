package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

@Composable
fun RiskCombinationSection(period: com.example.emotionapp.ui.screens.Period) {
        val context = androidx.compose.ui.platform.LocalContext.current
        var riskItems by remember { mutableStateOf<List<RiskItem>>(emptyList()) }

        LaunchedEffect(period) {
                com.example.emotionapp.data.UsageAnalysisManager.fetchUsageByEmotionStatus(
                        context
                ) { result ->
                        if (result != null) {
                                val periodKeyMap =
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

                                // 6가지 조합 생성 (순서 중요: GOOD->NORMAL->BAD, BUSY->FREE)
                                // 값이 같을 경우 이 순서를 따름
                                val rawList = mutableListOf<RiskItem>()
                                val emotions =
                                        listOf(
                                                "GOOD" to "😊 좋음",
                                                "NORMAL" to "🙂 보통",
                                                "BAD" to "😞 나쁨"
                                        )
                                val statuses = listOf("BUSY" to "바쁨", "FREE" to "여유로움")

                                emotions.forEach { (emotionKey, emotionLabel) ->
                                        val statusMap = periodKeyMap[emotionKey] ?: emptyMap()
                                        statuses.forEach { (statusKey, statusLabel) ->
                                                val value = statusMap[statusKey] ?: 0L
                                                rawList.add(
                                                        RiskItem(
                                                                "$emotionLabel + $statusLabel",
                                                                value
                                                        )
                                                )
                                        }
                                }

                                // 값 내림차순 정렬 (값이 같으면 원래 순서 유지 - stable sort)
                                var sorted = rawList.sortedByDescending { it.value }

                                // 레벨 부여
                                // 1위 (index 0) -> 높음
                                // 3위 (index 2) -> 중간
                                // 6위 (index 5) -> 낮음
                                val finalItems = mutableListOf<RiskItem>()

                                if (sorted.isNotEmpty()) {
                                        // High
                                        finalItems.add(
                                                sorted[0].copy(
                                                        levelLabel = "높음",
                                                        levelColor = PrimaryBrown,
                                                        textColor = SurfaceWhite
                                                )
                                        )
                                }
                                if (sorted.size >= 3) {
                                        // Medium
                                        finalItems.add(
                                                sorted[2].copy(
                                                        levelLabel = "중간",
                                                        levelColor = SecondaryBeige,
                                                        textColor = PrimaryBrown
                                                )
                                        )
                                }
                                if (sorted.size >= 6) {
                                        // Low
                                        finalItems.add(
                                                sorted[5].copy(
                                                        levelLabel = "낮음",
                                                        levelColor = AccentBlue,
                                                        textColor = PrimaryBrown
                                                )
                                        )
                                } else if (sorted.isNotEmpty() && sorted.size < 6) {
                                        // 데이터가 6개가 안될 경우(그럴일은 없겠지만), 마지막 아이템을 낮음으로?
                                        // data class 구조상 6개는 무조건 생성됨 (값 0 포함)
                                        finalItems.add(
                                                sorted.last()
                                                        .copy(
                                                                levelLabel = "낮음",
                                                                levelColor = AccentBlue,
                                                                textColor = PrimaryBrown
                                                        )
                                        )
                                }

                                riskItems = finalItems
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
                        text = "위험 감정 조합",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                if (riskItems.isEmpty()) {
                        // 로딩 중이거나 데이터 없음 (기본 UI 유지)
                        // 기존 하드코딩된 내용을 플레이스홀더로 보여줄 수도 있음
                        Text(
                                text = "데이터를 불러오는 중입니다...",
                                fontSize = FontSizes.Normal,
                                color = PrimaryBrown.copy(alpha = 0.5f)
                        )
                } else {
                        riskItems.forEachIndexed { index, item ->
                                RiskRow(
                                        label = item.label,
                                        levelLabel = item.levelLabel,
                                        levelBackground = item.levelColor,
                                        levelTextColor = item.textColor
                                )
                                if (index < riskItems.lastIndex) {
                                        Spacer(modifier = Modifier.height(Spacing.S))
                                }
                        }
                }
        }
}

private data class RiskItem(
        val label: String,
        val value: Long,
        val levelLabel: String = "",
        val levelColor: Color = Color.Transparent,
        val textColor: Color = Color.Black
)

@Composable
private fun RiskRow(
        label: String,
        levelLabel: String,
        levelBackground: Color,
        levelTextColor: Color
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(BackgroundBeige, RoundedCornerShape(Spacing.M))
                                .padding(horizontal = Spacing.M, vertical = Spacing.S),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(text = label, fontSize = FontSizes.Normal, color = PrimaryBrown)

                Box(
                        modifier =
                                Modifier.defaultMinSize(minHeight = 28.dp)
                                        .background(levelBackground, RoundedCornerShape(999.dp))
                                        .padding(horizontal = Spacing.M, vertical = Spacing.XS),
                        contentAlignment = Alignment.Center
                ) { Text(text = levelLabel, fontSize = FontSizes.Small, color = levelTextColor) }
        }
}
