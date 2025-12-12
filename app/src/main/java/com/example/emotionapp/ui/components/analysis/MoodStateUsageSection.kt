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

private data class MoodStateUsage(val moodLabel: String, val busy: Int, val relaxed: Int)

@Composable
fun MoodStateUsageSection() {
    val moodStateData =
            listOf(
                    MoodStateUsage("😊 좋음", busy = 45, relaxed = 55),
                    MoodStateUsage("🙂 보통", busy = 60, relaxed = 40),
                    MoodStateUsage("😞 나쁨", busy = 30, relaxed = 70)
            )

    val maxTotal = moodStateData.maxOf { it.busy + it.relaxed }

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

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            moodStateData.forEach { m ->
                val total = m.busy + m.relaxed
                val ratio = total.toFloat() / maxTotal.coerceAtLeast(1)

                Column {
                    Text(text = m.moodLabel, fontSize = FontSizes.Normal, color = PrimaryBrown)
                    Spacer(modifier = Modifier.height(Spacing.XS))
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .height(10.dp)
                                            .background(BackgroundBeige, RoundedCornerShape(999.dp))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(ratio).fillMaxHeight()) {
                            Box(
                                    modifier =
                                            Modifier.weight(m.busy.toFloat())
                                                    .fillMaxHeight()
                                                    .background(PrimaryBrown)
                            )
                            Box(
                                    modifier =
                                            Modifier.weight(m.relaxed.toFloat())
                                                    .fillMaxHeight()
                                                    .background(SecondaryBeige)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                            text = "바쁨 ${m.busy}분 · 여유로움 ${m.relaxed}분",
                            fontSize = FontSizes.Small,
                            color = PrimaryBrown.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
