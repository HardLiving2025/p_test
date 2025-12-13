package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

private data class MoodStateUsage(val moodLabel: String, val busy: Int, val relaxed: Int)

@Composable
fun MoodStateUsageSection() {
        val moodStateData =
                listOf(
                        MoodStateUsage("😊 좋음", busy = 45, relaxed = 55),
                        MoodStateUsage("🙂 보통", busy = 60, relaxed = 40),
                        MoodStateUsage("😞 나쁨", busy = 30, relaxed = 70)
                )

        // 최대값 계산 (Y축 스케일링용)
        // 데이터 합인 100을 기준으로 할 수도 있고, 실제 데이터의 최대값을 기준으로 할 수도 있음.
        // EmotionUsageSection과 통일성을 위해 100을 기준으로 하거나,
        // 여기서는 busy/relaxed 합이 100이라고 가정하면 100 스케일이 적절함.
        val yAxisMax = 100

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "감정/상황별 총 사용량 (분)",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                // 감정별 막대 그래프 (Canvas + Layout)
                Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        // Y축 레이블 (0 ~ 100)
                        Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                        ) {
                                listOf("100", "75", "50", "25", "0").forEach { label ->
                                        Text(
                                                text = label,
                                                fontSize = FontSizes.Small,
                                                color = PrimaryBrown.copy(alpha = 0.7f),
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.width(24.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.width(Spacing.S))

                        // 그래프 영역
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                                // 배경 그리드 라인
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                        val stepHeight = size.height / 4

                                        // 가로 점선 그리드
                                        for (i in 0..4) {
                                                val y = stepHeight * i
                                                drawLine(
                                                        color = DisabledGray.copy(alpha = 0.5f),
                                                        start = Offset(0f, y),
                                                        end = Offset(size.width, y),
                                                        pathEffect =
                                                                androidx.compose.ui.graphics
                                                                        .PathEffect.dashPathEffect(
                                                                        floatArrayOf(10f, 10f),
                                                                        0f
                                                                ),
                                                        strokeWidth = 2f
                                                )
                                        }

                                        // Y축 세로선 (왼쪽)
                                        drawLine(
                                                color = PrimaryBrown.copy(alpha = 0.5f),
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, size.height),
                                                strokeWidth = 2f
                                        )
                                }

                                // 막대 그래프 데이터
                                Row(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .padding(horizontal = Spacing.S),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                ) {
                                        moodStateData.forEach { item ->
                                                Box(
                                                        modifier =
                                                                Modifier.weight(1f).fillMaxHeight(),
                                                        contentAlignment = Alignment.BottomCenter
                                                ) {
                                                        // 막대 그룹
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.Bottom,
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(4.dp),
                                                                modifier = Modifier.fillMaxHeight()
                                                        ) {
                                                                // 바쁨 (PrimaryBrown)
                                                                MoodBarItem(
                                                                        value = item.busy,
                                                                        max = yAxisMax,
                                                                        color = PrimaryBrown
                                                                )
                                                                // 여유로움 (SecondaryBeige)
                                                                MoodBarItem(
                                                                        value = item.relaxed,
                                                                        max = yAxisMax,
                                                                        color = SecondaryBeige
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(Spacing.S))

                // X축 레이블 (그래프 아래 위치)
                Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(24.dp))
                        Spacer(modifier = Modifier.width(Spacing.S))

                        Row(modifier = Modifier.weight(1f).padding(horizontal = Spacing.S)) {
                                moodStateData.forEach { item ->
                                        Box(
                                                modifier = Modifier.weight(1f),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = item.moodLabel,
                                                        fontSize = FontSizes.Small,
                                                        color = PrimaryBrown,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(Spacing.M))

                // 범례 (Legend)
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                ) {
                        MoodLegendItem(color = PrimaryBrown, label = "바쁨")
                        Spacer(modifier = Modifier.width(Spacing.L))
                        MoodLegendItem(color = SecondaryBeige, label = "여유로움")
                }
        }
}

@Composable
private fun MoodBarItem(value: Int, max: Int, color: androidx.compose.ui.graphics.Color) {
        Box(
                modifier =
                        Modifier.width(18.dp) // 막대 너비
                                .fillMaxHeight(fraction = (value.toFloat() / max).coerceIn(0f, 1f))
                                .background(
                                        color,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
        )
}

@Composable
private fun MoodLegendItem(color: androidx.compose.ui.graphics.Color, label: String) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.XS)
        ) {
                Box(modifier = Modifier.size(12.dp).background(color))
                Text(
                        text = label,
                        fontSize = FontSizes.Small,
                        color = PrimaryBrown.copy(alpha = 0.8f)
                )
        }
}
