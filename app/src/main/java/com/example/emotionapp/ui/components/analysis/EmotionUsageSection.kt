package com.example.emotionapp.ui.components.analysis

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

private data class MoodUsage(val moodLabel: String, val sns: Int, val other: Int, val game: Int)

private data class AppDetail(
        val appName: String,
        val icon: String,
        val total: Int,
        val good: Int,
        val normal: Int,
        val bad: Int
)

@Composable
fun EmotionUsageSection(showDetail: Boolean, onToggleDetail: () -> Unit) {
        // 감정별 평균 사용량 데이터
        val moodData =
                listOf(
                        MoodUsage("😊 좋음", sns = 45, other = 30, game = 25),
                        MoodUsage("🙂 보통", sns = 60, other = 50, game = 35),
                        MoodUsage("😞 나쁨", sns = 75, other = 95, game = 40)
                )

        // 앱별 감정 상세 데이터
        val appDetailData =
                listOf(
                        AppDetail(
                                "Naver Webtoon",
                                "📚",
                                total = 350,
                                good = 80,
                                normal = 120,
                                bad = 150
                        ),
                        AppDetail(
                                "Instagram",
                                "📷",
                                total = 255,
                                good = 50,
                                normal = 80,
                                bad = 125
                        ),
                        AppDetail("YouTube", "▶️", total = 180, good = 40, normal = 70, bad = 70),
                        AppDetail("TikTok", "🎵", total = 150, good = 30, normal = 50, bad = 70),
                        AppDetail("Twitter", "🐦", total = 120, good = 40, normal = 50, bad = 30)
                )

        val totalUsage = appDetailData.sumOf { it.total }
        val maxTotal = appDetailData.maxOf { it.total }

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "감정별 평균 사용량 (분)",
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
                                                                PathEffect.dashPathEffect(
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
                                        moodData.forEach { item ->
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
                                                                // SNS (PrimaryBrown)
                                                                BarItem(
                                                                        value = item.sns,
                                                                        color = PrimaryBrown
                                                                )
                                                                // 게임 (SecondaryBeige) - 이미지상 가운데
                                                                BarItem(
                                                                        value = item.game,
                                                                        color = SecondaryBeige
                                                                )
                                                                // 기타 (HighlightOrange) - 이미지상 오른쪽
                                                                BarItem(
                                                                        value = item.other,
                                                                        color = HighlightOrange
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
                                moodData.forEach { item ->
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
                        LegendItem(color = PrimaryBrown, label = "SNS")
                        Spacer(modifier = Modifier.width(Spacing.L))
                        LegendItem(color = SecondaryBeige, label = "게임")
                        Spacer(modifier = Modifier.width(Spacing.L))
                        LegendItem(color = HighlightOrange, label = "기타")
                }

                Spacer(modifier = Modifier.height(Spacing.M))

                Button(
                        onClick = onToggleDetail,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrown),
                        shape = RoundedCornerShape(Spacing.L)
                ) { Text(text = if (showDetail) "닫기" else "상세 보기", color = SurfaceWhite) }

                AnimatedVisibility(
                        visible = showDetail,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                ) {
                        Column {
                                Spacer(modifier = Modifier.height(Spacing.SectionGap))

                                Text(
                                        text = "앱별 감정 비율 (Top 5)",
                                        fontSize = FontSizes.SemiBold,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryBrown
                                )

                                Spacer(modifier = Modifier.height(Spacing.M))

                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
                                        appDetailData.forEach { app ->
                                                val percentage =
                                                        (app.total.toFloat() /
                                                                totalUsage.coerceAtLeast(1) * 100f)
                                                val goodPercent =
                                                        app.good.toFloat() / app.total * 100f
                                                val normalPercent =
                                                        app.normal.toFloat() / app.total * 100f
                                                val badPercent =
                                                        app.bad.toFloat() / app.total * 100f

                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(
                                                                        Spacing.ItemGap
                                                                )
                                                ) {
                                                        // 아이콘
                                                        Box(
                                                                modifier =
                                                                        Modifier.size(48.dp)
                                                                                .background(
                                                                                        BackgroundBeige,
                                                                                        RoundedCornerShape(
                                                                                                Spacing.L
                                                                                        )
                                                                                ),
                                                                contentAlignment = Alignment.Center
                                                        ) {
                                                                Text(
                                                                        text = app.icon,
                                                                        fontSize =
                                                                                FontSizes.SemiBold
                                                                )
                                                        }

                                                        // 이름 + 막대
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        text = app.appName,
                                                                        fontSize = FontSizes.Normal,
                                                                        color = PrimaryBrown
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        Spacing.XS
                                                                                )
                                                                )
                                                                Box(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth()
                                                                                        .height(
                                                                                                10.dp
                                                                                        )
                                                                                        .background(
                                                                                                BackgroundBeige,
                                                                                                RoundedCornerShape(
                                                                                                        999.dp
                                                                                                )
                                                                                        )
                                                                ) {
                                                                        Row(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth(
                                                                                                        app.total
                                                                                                                .toFloat() /
                                                                                                                maxTotal.coerceAtLeast(
                                                                                                                        1
                                                                                                                )
                                                                                                )
                                                                                                .fillMaxHeight()
                                                                        ) {
                                                                                Box(
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                                badPercent
                                                                                                                        .coerceAtLeast(
                                                                                                                                0.1f
                                                                                                                        )
                                                                                                        )
                                                                                                        .fillMaxHeight()
                                                                                                        .background(
                                                                                                                PrimaryBrown
                                                                                                        )
                                                                                )
                                                                                Box(
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                                normalPercent
                                                                                                                        .coerceAtLeast(
                                                                                                                                0.1f
                                                                                                                        )
                                                                                                        )
                                                                                                        .fillMaxHeight()
                                                                                                        .background(
                                                                                                                HighlightOrange
                                                                                                        )
                                                                                )
                                                                                Box(
                                                                                        modifier =
                                                                                                Modifier.weight(
                                                                                                                goodPercent
                                                                                                                        .coerceAtLeast(
                                                                                                                                0.1f
                                                                                                                        )
                                                                                                        )
                                                                                                        .fillMaxHeight()
                                                                                                        .background(
                                                                                                                SecondaryBeige
                                                                                                        )
                                                                                )
                                                                        }
                                                                }
                                                        }

                                                        // 시간 + %
                                                        Column(
                                                                horizontalAlignment = Alignment.End
                                                        ) {
                                                                val minutes = app.total / 60
                                                                val seconds = app.total % 60
                                                                Text(
                                                                        text =
                                                                                "${minutes}분 ${seconds}초",
                                                                        fontSize = FontSizes.Small,
                                                                        color = PrimaryBrown
                                                                )
                                                                Text(
                                                                        text =
                                                                                "${"%.1f".format(percentage)}%",
                                                                        fontSize = FontSizes.Small,
                                                                        color =
                                                                                PrimaryBrown.copy(
                                                                                        alpha = 0.6f
                                                                                )
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun BarItem(value: Int, max: Int = 100, color: Color) {
        Box(
                modifier =
                        Modifier.width(18.dp) // 막대 너비
                                .fillMaxHeight(fraction = (value.toFloat() / max).coerceIn(0f, 1f))
                                .background(color)
        )
}

@Composable
private fun LegendItem(color: Color, label: String) {
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
