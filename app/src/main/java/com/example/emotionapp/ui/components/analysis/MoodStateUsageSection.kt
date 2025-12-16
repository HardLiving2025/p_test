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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

@Composable
fun MoodStateUsageSection(period: com.example.emotionapp.ui.screens.Period) {
        val context = androidx.compose.ui.platform.LocalContext.current

        // 감정 -> 상태("BUSY"/"FREE") -> 총 사용량(분)
        var moodStatusData by remember { mutableStateOf<Map<String, Map<String, Long>>?>(null) }

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

                                val dayDivisor =
                                        when (period) {
                                                com.example.emotionapp.ui.screens.Period
                                                        .YESTERDAY -> 1
                                                com.example.emotionapp.ui.screens.Period.WEEK -> 7
                                                com.example.emotionapp.ui.screens.Period
                                                        .TWO_WEEKS -> 14
                                                com.example.emotionapp.ui.screens.Period.MONTH -> 30
                                        }

                                val processed = mutableMapOf<String, Map<String, Long>>()
                                periodKeyMap.forEach { (emotion, statusMap) ->
                                        val newMap = mutableMapOf<String, Long>()
                                        statusMap.forEach { (status, timeMs) ->
                                                newMap[status] =
                                                        timeMs /
                                                                (1000 * 60) /
                                                                3 /
                                                                dayDivisor // 분 단위 변환
                                        }
                                        processed[emotion] = newMap
                                }
                                moodStatusData = processed
                        }
                }
        }

        val chartGroups = listOf("좋음" to "GOOD", "보통" to "NORMAL", "나쁨" to "BAD")

        // 최대값 계산 (Y축 스케일)
        val allValues = moodStatusData?.values?.flatMap { it.values } ?: emptyList()
        val maxValComp = allValues.maxOrNull() ?: 1L
        // 최소 1은 보장 (분모 0 방지), 값이 작으면 그에 맞게, 0이면 기본 60, 등
        // 하지만 사용자 요청은 "유동적". 데이터가 10이면 10이 max.
        val maxVal = if (maxValComp == 0L) 60L else maxValComp

        // 단계별 값 (4등분)
        // 0부터 maxVal까지 4단계로 표시

        // 색상 정의
        val busyColor = Color(0xFF3C2F2F)
        val relaxedColor = Color(0xFFD2BDA8)

        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                                .padding(Spacing.CardInner)
        ) {
                Text(
                        text = "감정/상태별 평균 사용량 (분)",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )

                Spacer(modifier = Modifier.height(Spacing.M))

                // 감정별 그룹 막대 그래프 (Canvas + Layout)
                Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        // Y축 레이블
                        Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                        ) {
                                val steps = 4
                                (steps downTo 0).forEach { i ->
                                        val value = (maxVal * i / steps)
                                        Text(
                                                text = "$value",
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
                                        chartGroups.forEach { (label, key) ->
                                                // 해당 감정의 데이터
                                                val statusData = moodStatusData?.get(key)
                                                val busyVal = statusData?.get("BUSY") ?: 0L
                                                val freeVal = statusData?.get("FREE") ?: 0L

                                                Box(
                                                        modifier =
                                                                Modifier.weight(1f).fillMaxHeight(),
                                                        contentAlignment = Alignment.BottomCenter
                                                ) {
                                                        // 두 개의 막대 (Busy, Free)
                                                        Row(
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(4.dp),
                                                                verticalAlignment = Alignment.Bottom
                                                        ) {
                                                                // Busy Bar
                                                                MoodBarItem(
                                                                        busyVal.toInt(),
                                                                        maxVal.toInt(),
                                                                        busyColor
                                                                )
                                                                // Relaxed Bar
                                                                MoodBarItem(
                                                                        freeVal.toInt(),
                                                                        maxVal.toInt(),
                                                                        relaxedColor
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(Spacing.S))

                // X축 레이블
                Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.width(32.dp))
                        Spacer(modifier = Modifier.width(Spacing.S))

                        Row(modifier = Modifier.weight(1f).padding(horizontal = Spacing.S)) {
                                chartGroups.forEach { (label, _) ->
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

                Spacer(modifier = Modifier.height(Spacing.M))

                // 범례 (Legend)
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                ) {
                        MoodLegendItem(color = busyColor, label = "바쁨")
                        Spacer(modifier = Modifier.width(Spacing.L))
                        MoodLegendItem(color = relaxedColor, label = "여유로움")
                }
        }
}

@Composable
private fun MoodBarItem(value: Int, max: Int, color: Color) {
        Box(
                modifier =
                        Modifier.width(16.dp) // 그룹 내 막대라 조금 얇게
                                .fillMaxHeight(fraction = (value.toFloat() / max).coerceIn(0f, 1f))
                                .background(
                                        color,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
        )
}

@Composable
private fun MoodLegendItem(color: Color, label: String) {
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
