package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

@Composable
fun MoodStateUsageSection() {
        val context = androidx.compose.ui.platform.LocalContext.current
        // 감정: GOOD, NORMAL, BAD -> 총 사용량(분) 매핑
        var moodData by remember { mutableStateOf<Map<String, Long>?>(null) } // "GOOD" -> 120L

        LaunchedEffect(Unit) {
                com.example.emotionapp.data.UsageAnalysisManager.fetchUsageByEmotionAverage(
                        context
                ) { result ->
                        if (result != null) {
                                // result: "GOOD" -> {"SNS": 10, ...}
                                // 총합 계산
                                val calculated = mutableMapOf<String, Long>()
                                result.forEach { (emotion, cats) ->
                                        val totalMs = cats.values.sum()
                                        calculated[emotion] = totalMs / (1000 * 60) // ms -> min
                                }
                                moodData = calculated
                        }
                }
        }

        val chartData =
                listOf(
                        "좋음" to (moodData?.get("GOOD") ?: 0L),
                        "보통" to (moodData?.get("NORMAL") ?: 0L),
                        "나쁨" to (moodData?.get("BAD") ?: 0L)
                )

        // 최대값 (Y축) - 데이터가 없으면 기본 100
        // maxOfOrNull ambiguity 해결을 위해 compareBy를 쓰거나 map 사용
        val maxVal = chartData.maxOfOrNull { it.second } ?: 100L
        if (maxVal < 100L) {
                // 최소 100
        }
        val finalMax = maxVal.coerceAtLeast(100L)

        // Y축 눈금 (5등분)
        val step = finalMax / 4

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "감정별 평균 총 사용량 (분)",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                // 감정별 막대 그래프 (Canvas + Layout)
                Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        // Y축 레이블
                        Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                        ) {
                                (4 downTo 0).forEach { i ->
                                        Text(
                                                text = "${step * i}",
                                                fontSize = FontSizes.Small,
                                                color = PrimaryBrown.copy(alpha = 0.7f),
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.width(32.dp)
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
                                        chartData.forEach { (label, value) ->
                                                // 색상 매핑
                                                val barColor =
                                                        when (label) {
                                                                "좋음" -> Color(0xFFD4E157) // 연두색 계열
                                                                "보통" -> Color(0xFFFFCC80) // 주황색 계열
                                                                "나쁨" -> Color(0xFFEF9A9A) // 빨간색 계열
                                                                else -> SecondaryBeige
                                                        }

                                                Box(
                                                        modifier =
                                                                Modifier.weight(1f).fillMaxHeight(),
                                                        contentAlignment = Alignment.BottomCenter
                                                ) {
                                                        Column(
                                                                horizontalAlignment =
                                                                        Alignment
                                                                                .CenterHorizontally,
                                                                verticalArrangement =
                                                                        Arrangement.Bottom
                                                        ) {
                                                                // 값 텍스트 (막대 위)
                                                                if (value > 0) {
                                                                        Text(
                                                                                text = "$value",
                                                                                fontSize = 10.sp,
                                                                                color =
                                                                                        PrimaryBrown
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.8f
                                                                                                ),
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                bottom =
                                                                                                        2.dp
                                                                                        )
                                                                        )
                                                                }
                                                                // 막대
                                                                MoodBarItem(
                                                                        value = value.toInt(),
                                                                        max = finalMax.toInt(),
                                                                        color = barColor
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
                        Spacer(modifier = Modifier.width(32.dp)) // Y축 너비만큼 공백
                        Spacer(modifier = Modifier.width(Spacing.S))

                        Row(modifier = Modifier.weight(1f).padding(horizontal = Spacing.S)) {
                                chartData.forEach { (label, _) ->
                                        Box(
                                                modifier = Modifier.weight(1f),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = label,
                                                        fontSize = FontSizes.Small,
                                                        color = PrimaryBrown,
                                                        textAlign = TextAlign.Center
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun MoodBarItem(value: Int, max: Int, color: androidx.compose.ui.graphics.Color) {
        Box(
                modifier =
                        Modifier.width(30.dp) // 막대 너비
                                .fillMaxHeight(fraction = (value.toFloat() / max).coerceIn(0f, 1f))
                                .background(
                                        color,
                                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
        )
}
