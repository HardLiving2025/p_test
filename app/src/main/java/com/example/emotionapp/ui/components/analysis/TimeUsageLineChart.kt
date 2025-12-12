package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.SlotUsageAverage
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.SecondaryBeige

// 색상 정의
private val ColorSNS = PrimaryBrown          // 검갈색
private val ColorGame = SecondaryBeige       // 베이지
private val ColorOther = Color(0xFFF4BE6C)   // 노란색 (추정)

@Composable
fun TimeUsageLineChart(data: List<SlotUsageAverage>) {
    // 빈 데이터 처리
    if (data.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("데이터가 없습니다.", color = PrimaryBrown)
        }
        return
    }

    // 30분 슬롯 → 2시간 단위(4개씩)로 압축
    val aggregatedData = remember(data) {
        data
            .chunked(4) // 30분 × 4 = 2시간
            .map { group ->
                val avgSns = group.map { it.sns }.average().toLong()
                val avgGame = group.map { it.game }.average().toLong()
                val avgOther = group.map { it.other }.average().toLong()
                val avgTotal = group.map { it.total }.average().toLong()

                SlotUsageAverage(
                    slot = group.first().slot,                // 그냥 첫 슬롯 값 사용
                    startTime = group.first().startTime,      // 블록 시작 시간
                    endTime = group.last().endTime,           // 블록 끝 시간
                    sns = avgSns,                             // ms 기준 평균
                    game = avgGame,
                    other = avgOther,
                    total = avgTotal                          // total도 평균으로
                )
            }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 여백 설정
            val chartPaddingBottom = 50.dp.toPx() // X축 및 범례 공간
            val chartPaddingLeft = 30.dp.toPx()   // Y축 레이블 공간

            val chartWidth = width - chartPaddingLeft
            val chartHeight = height - chartPaddingBottom

            // Y축 (0 ~ 30분)
            val maxY = 30f
            val yStep = 10f

            // ───────────── Y축 그리드 + 레이블 ─────────────
            for (i in 0..3) { // 0, 10, 20, 30
                val value = i * yStep
                val y = chartHeight - (value / maxY * chartHeight)

                // 점선 그리드
                drawLine(
                    color = SecondaryBeige.copy(alpha = 0.5f),
                    start = Offset(chartPaddingLeft, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect
                        .dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                // Y축 레이블
                val textLayoutResult =
                    textMeasurer.measure(
                        text = "${value.toInt()}",
                        style = androidx.compose.ui.text.TextStyle(
                            color = PrimaryBrown.copy(alpha = 0.7f),
                            fontSize = FontSizes.Small
                        )
                    )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = chartPaddingLeft -
                                textLayoutResult.size.width -
                                5.dp.toPx(),
                        y = y - textLayoutResult.size.height / 2
                    )
                )
            }

            // X축: 한 점이 2시간 → 4시간마다 라벨 = 2칸마다
            val labelInterval = 2
            val xStep = chartWidth / (aggregatedData.size - 1).coerceAtLeast(1)

            aggregatedData.forEachIndexed { index, item ->
                if (index % labelInterval != 0) return@forEachIndexed

                val x = chartPaddingLeft + (index * xStep)
                val label = item.startTime.substring(0, 2) // "00:00" -> "00"

                val textLayoutResult =
                    textMeasurer.measure(
                        text = label,
                        style = androidx.compose.ui.text.TextStyle(
                            color = PrimaryBrown,
                            fontSize = FontSizes.Small
                        )
                    )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        x = x - textLayoutResult.size.width / 2,
                        y = chartHeight + 10.dp.toPx()
                    )
                )
            }

            // ───────────── 라인 그리기 함수 ─────────────
            fun drawLineChart(values: List<Long>, color: Color) {
                if (values.isEmpty()) return

                val path = Path()
                val points = mutableListOf<Offset>()

                values.forEachIndexed { index, valueMs ->
                    val valueMin =
                        (valueMs / 1000f / 60f).coerceAtMost(30f) // ms → 분, 최대 30분
                    val x = chartPaddingLeft + (index * xStep)
                    val y = chartHeight - (valueMin / maxY * chartHeight)

                    points.add(Offset(x, y))

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        val prevX = points[index - 1].x
                        val prevY = points[index - 1].y
                        val controlX1 = prevX + (x - prevX) / 2
                        val controlY1 = prevY
                        val controlX2 = prevX + (x - prevX) / 2
                        val controlY2 = y
                        path.cubicTo(
                            controlX1,
                            controlY1,
                            controlX2,
                            controlY2,
                            x,
                            y
                        )
                    }
                }

                // 선
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                )

                // 포인트 (외곽색 + 흰색)
                points.forEach { point ->
                    drawCircle(
                        color = color,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }

            // 3가지 라인
            drawLineChart(aggregatedData.map { it.sns }, ColorSNS)
            drawLineChart(aggregatedData.map { it.game }, ColorGame)
            drawLineChart(aggregatedData.map { it.other }, ColorOther)
        }
    }

    // 범례
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem("SNS", ColorSNS)
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem("게임", ColorGame)
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem("기타", ColorOther)
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    Color.White,
                    androidx.compose.foundation.shape.CircleShape
                )
                .border(
                    2.dp,
                    color,
                    androidx.compose.foundation.shape.CircleShape
                )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = FontSizes.Small, color = PrimaryBrown)
    }
}
