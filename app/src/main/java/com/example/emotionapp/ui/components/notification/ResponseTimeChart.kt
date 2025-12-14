package com.example.emotionapp.ui.components.notification

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.SecondaryBeige

@Composable
fun ResponseTimeChart(data: List<Pair<String, Double>>) {
    val animatable = remember(data) { Animatable(0f) }

    LaunchedEffect(key1 = data) {
        animatable.animateTo(
                targetValue = 1f,
                animationSpec =
                        tween(
                                durationMillis = 1000,
                                easing = androidx.compose.animation.core.EaseInOut
                        )
        )
    }

    Box(
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp),
            contentAlignment = Alignment.Center
    ) {
        val textMeasurer = rememberTextMeasurer()

        // 데이터 범위 계산 (Y축)
        val maxVal = data.maxOfOrNull { it.second } ?: 5.0
        val yAxisMax = (kotlin.math.ceil(maxVal)).coerceAtLeast(1.0)

        val progress = animatable.value

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val chartHeight = height * 0.8f

            // Y축 레이블을 위한 공간 확보
            val yAxisLabelWidth = 25.dp.toPx()
            val chartStartPadding = yAxisLabelWidth + 5.dp.toPx()
            val chartWidth = width - chartStartPadding - 10.dp.toPx()

            val xStep = chartWidth / (data.size - 1).coerceAtLeast(1)

            // Draw Y-axis grid lines
            val gridLines = 5
            val stepHeight = chartHeight / gridLines

            for (i in 0..gridLines) {
                val y = height - (i * stepHeight) - 30.dp.toPx()

                // Y-axis Label
                val value = (yAxisMax * i / gridLines).toInt()
                val textResult =
                        textMeasurer.measure(
                                text = value.toString(),
                                style =
                                        androidx.compose.ui.text.TextStyle(
                                                color = PrimaryBrown,
                                                fontSize = FontSizes.Small
                                        )
                        )

                drawText(
                        textLayoutResult = textResult,
                        topLeft = Offset(x = 0f, y = y - (textResult.size.height / 2))
                )

                drawLine(
                        color = SecondaryBeige,
                        start = Offset(chartStartPadding, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Line Path and Points
            if (data.isNotEmpty()) {
                val path = Path()
                val points = mutableListOf<Offset>()

                data.forEachIndexed { index, item ->
                    val targetX =
                            if (data.size == 1) {
                                chartStartPadding + (chartWidth / 2)
                            } else {
                                (index * xStep) + chartStartPadding
                            }
                    val normalizedY = (item.second / yAxisMax).toFloat()
                    val targetY = height - (normalizedY * chartHeight) - 30.dp.toPx()

                    // Animation Start Position (Center Top)
                    val startX = chartStartPadding + (chartWidth / 2)
                    val startY = 0f // Top of the canvas

                    val animX = startX + (targetX - startX) * progress
                    val animY = startY + (targetY - startY) * progress

                    points.add(Offset(animX, animY))

                    if (index == 0) {
                        path.moveTo(animX, animY)
                    } else {
                        // 곡선 효과를 위해 cubicTo 등을 사용할 수도 있지만, 간단히 lineTo 사용
                        path.lineTo(animX, animY)
                    }

                    // X-axis Label
                    val textLayoutResult =
                            textMeasurer.measure(
                                    text = item.first,
                                    style =
                                            androidx.compose.ui.text.TextStyle(
                                                    color =
                                                            PrimaryBrown.copy(
                                                                    alpha = progress
                                                            ), // Fade in label
                                                    fontSize = FontSizes.Small
                                            )
                            )
                    drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft =
                                    Offset(
                                            targetX - (textLayoutResult.size.width / 2),
                                            height - 20.dp.toPx()
                                    )
                    )
                }

                // Draw Line
                drawPath(
                        path = path,
                        color = PrimaryBrown,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Points
                points.forEach { point ->
                    drawCircle(color = PrimaryBrown, radius = 4.dp.toPx(), center = point)
                }
            }
        }
    }
}
