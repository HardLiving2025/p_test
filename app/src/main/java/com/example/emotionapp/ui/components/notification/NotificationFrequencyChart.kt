package com.example.emotionapp.ui.components.notification

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.SecondaryBeige

@Composable
fun NotificationFrequencyChart(data: List<Pair<String, Int>>) {
    Box(
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp),
            contentAlignment = Alignment.Center
    ) {
        val textMeasurer = rememberTextMeasurer()
        val maxValue = data.maxOfOrNull { it.second } ?: 1
        // Y축 최대값을 5의 배수로 올림 처리
        val yAxisMax = ((maxValue / 5) + 1) * 5

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val chartHeight = height * 0.8f
            val chartWidth = width * 0.9f
            val barWidth = chartWidth / (data.size * 2f)
            val barSpacing = chartWidth / data.size

            // Draw Y-axis grid lines
            val gridLines = 5
            val stepHeight = chartHeight / gridLines

            for (i in 0..gridLines) {
                val y = height - (i * stepHeight) - 30.dp.toPx() // X축 라벨용 여백

                // Grid line
                drawLine(
                        color = SecondaryBeige,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                )
            }

            // Draw Bars and X-axis labels
            data.forEachIndexed { index, item ->
                val x = (index * barSpacing) + (barSpacing / 2) - (barWidth / 2) + 20.dp.toPx()
                val barHeight = (item.second.toFloat() / yAxisMax) * chartHeight
                val y = height - barHeight - 30.dp.toPx() // X축 라벨용 여백

                // Bar
                drawRect(
                        color = PrimaryBrown,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight)
                )

                // Label
                val textLayoutResult =
                        textMeasurer.measure(
                                text = item.first,
                                style =
                                        androidx.compose.ui.text.TextStyle(
                                                color = PrimaryBrown,
                                                fontSize = FontSizes.Small
                                        )
                        )

                drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft =
                                Offset(
                                        x + (barWidth / 2) - (textLayoutResult.size.width / 2),
                                        height - 20.dp.toPx()
                                )
                )
            }
        }
    }
}
