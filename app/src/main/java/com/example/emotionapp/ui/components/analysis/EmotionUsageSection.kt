package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

private data class MoodUsage(
    val moodLabel: String,
    val sns: Int,
    val other: Int,
    val game: Int
)

private data class AppDetail(
    val appName: String,
    val icon: String,
    val total: Int,
    val good: Int,
    val normal: Int,
    val bad: Int
)

// React 코드의 #EDBE77
private val HighlightOrange = Color(0xFFEDBE77)

@Composable
fun EmotionUsageSection(
    showDetail: Boolean,
    onToggleDetail: () -> Unit
) {
    // 감정별 평균 사용량 데이터
    val moodData = listOf(
        MoodUsage("😊 좋음", sns = 45, other = 30, game = 25),
        MoodUsage("🙂 보통", sns = 60, other = 50, game = 35),
        MoodUsage("😞 나쁨", sns = 75, other = 95, game = 40)
    )

    // 앱별 감정 상세 데이터
    val appDetailData = listOf(
        AppDetail("Naver Webtoon", "📚", total = 350, good = 80, normal = 120, bad = 150),
        AppDetail("Instagram", "📷", total = 255, good = 50, normal = 80, bad = 125),
        AppDetail("YouTube", "▶️", total = 180, good = 40, normal = 70, bad = 70),
        AppDetail("TikTok", "🎵", total = 150, good = 30, normal = 50, bad = 70),
        AppDetail("Twitter", "🐦", total = 120, good = 40, normal = 50, bad = 30)
    )

    val totalUsage = appDetailData.sumOf { it.total }
    val maxTotal = appDetailData.maxOf { it.total }
    val maxMoodTotal = moodData.maxOf { it.sns + it.other + it.game }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "감정별 평균 사용량 (분)",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 감정별 바 그래프
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            moodData.forEach { item ->
                val total = item.sns + item.other + item.game
                val ratio = total.toFloat() / maxMoodTotal.coerceAtLeast(1)

                Column {
                    Text(
                        text = item.moodLabel,
                        fontSize = 14.sp,
                        color = PrimaryBrown
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .background(BackgroundBeige, RoundedCornerShape(999.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .fillMaxHeight()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(item.sns.toFloat())
                                    .fillMaxHeight()
                                    .background(PrimaryBrown)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(item.other.toFloat())
                                    .fillMaxHeight()
                                    .background(SecondaryBeige)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(item.game.toFloat())
                                    .fillMaxHeight()
                                    .background(HighlightOrange)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SNS ${item.sns}분 · 기타 ${item.other}분 · 게임 ${item.game}분",
                        fontSize = 12.sp,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onToggleDetail,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrown),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (showDetail) "닫기" else "상세 보기",
                color = SurfaceWhite
            )
        }

        if (showDetail) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "앱별 감정 비율 (Top 5)",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                appDetailData.forEach { app ->
                    val percentage = (app.total.toFloat() / totalUsage.coerceAtLeast(1) * 100f)
                    val goodPercent = app.good.toFloat() / app.total * 100f
                    val normalPercent = app.normal.toFloat() / app.total * 100f
                    val badPercent = app.bad.toFloat() / app.total * 100f

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 아이콘
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(BackgroundBeige, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = app.icon,
                                fontSize = 22.sp
                            )
                        }

                        // 이름 + 막대
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.appName,
                                fontSize = 14.sp,
                                color = PrimaryBrown
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(BackgroundBeige, RoundedCornerShape(999.dp))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(app.total.toFloat() / maxTotal.coerceAtLeast(1))
                                        .fillMaxHeight()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(badPercent.coerceAtLeast(0.1f))
                                            .fillMaxHeight()
                                            .background(PrimaryBrown)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(normalPercent.coerceAtLeast(0.1f))
                                            .fillMaxHeight()
                                            .background(HighlightOrange)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(goodPercent.coerceAtLeast(0.1f))
                                            .fillMaxHeight()
                                            .background(SecondaryBeige)
                                    )
                                }
                            }
                        }

                        // 시간 + %
                        Column(horizontalAlignment = Alignment.End) {
                            val minutes = app.total / 60
                            val seconds = app.total % 60
                            Text(
                                text = "${minutes}분 ${seconds}초",
                                fontSize = 12.sp,
                                color = PrimaryBrown
                            )
                            Text(
                                text = "${"%.1f".format(percentage)}%",
                                fontSize = 12.sp,
                                color = PrimaryBrown.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 범례
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendDot(color = PrimaryBrown, label = "😞 나쁨")
                LegendDot(color = HighlightOrange, label = "🙂 보통")
                LegendDot(color = SecondaryBeige, label = "😊 좋음")
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(50))
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = PrimaryBrown.copy(alpha = 0.8f)
        )
    }
}
