package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

private data class TimeUsage(
    val timeLabel: String,
    val sns: Int,
    val other: Int,
    val game: Int
)

@Composable
fun TimeUsageSection() {
    val timeData = listOf(
        TimeUsage("00-04", sns = 15, other = 20, game = 10),
        TimeUsage("04-08", sns = 5, other = 3, game = 2),
        TimeUsage("08-12", sns = 30, other = 25, game = 15),
        TimeUsage("12-16", sns = 40, other = 35, game = 30),
        TimeUsage("16-20", sns = 55, other = 45, game = 35),
        TimeUsage("20-24", sns = 70, other = 85, game = 50)
    )

    val maxTotal = timeData.maxOf { it.sns + it.other + it.game }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "시간대별 평균 사용량 (분)",
            fontSize = FontSizes.SemiBold,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            timeData.forEach { t ->
                val total = t.sns + t.other + t.game
                val ratio = total.toFloat() / maxTotal.coerceAtLeast(1)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = t.timeLabel,
                        fontSize = FontSizes.Small,
                        color = PrimaryBrown,
                        modifier = Modifier.width(48.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(BackgroundBeige, RoundedCornerShape(999.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(ratio)
                                .fillMaxHeight()
                                .background(PrimaryBrown, RoundedCornerShape(999.dp))
                        )
                    }
                    Text(
                        text = "${total}분",
                        fontSize = FontSizes.Small,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
