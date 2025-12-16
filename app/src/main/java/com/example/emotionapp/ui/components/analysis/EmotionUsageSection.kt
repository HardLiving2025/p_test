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
fun EmotionUsageSection(
        period: com.example.emotionapp.ui.screens.Period,
        showDetail: Boolean,
        onToggleDetail: () -> Unit
) {
        val context = androidx.compose.ui.platform.LocalContext.current

        // 1. 감정별 평균 사용량 (SNS, GAME, OTHER)
        var moodData by remember { mutableStateOf<List<MoodUsage>>(emptyList()) }
        // 2. 앱별 감정 상세 데이터
        var appDetailData by remember { mutableStateOf<List<AppDetail>>(emptyList()) }

        // Period가 바뀌면 다시 Fetch할 수도 있지만, 여기서는 한번 Fetch 후 Period에 따라 필터링하는 방식이 좋을지,
        // 아니면 매번 새로고침할지 결정해야 합니다.
        // UsageAnalysisManager 구조상 한번에 다 받아오므로, LaunchedEffect 내에서 Period에 따라 state를 갱신합니다.

        LaunchedEffect(period) {
                // Fetch Mood Average
                com.example.emotionapp.data.UsageAnalysisManager.fetchUsageByEmotionAverage(
                        context
                ) { result ->
                        if (result != null) {
                                android.util.Log.d("EmotionUsageSection", "Data received: $result")
                                val periodKey =
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

                                val list =
                                        listOf(
                                                        "GOOD" to "😊 좋음",
                                                        "NORMAL" to "🙂 보통",
                                                        "BAD" to "😞 나쁨"
                                                )
                                                .map { (key, label) ->
                                                        val map = periodKey[key]
                                                        // map이 null일 경우를 대비해 기본값 처리
                                                        val safeMap = map ?: emptyMap()

                                                        val sns =
                                                                (safeMap["SNS"] ?: 0L) / (1000 * 60)
                                                        val game =
                                                                (safeMap["GAME"]
                                                                        ?: 0L) / (1000 * 60)
                                                        val other =
                                                                (safeMap["OTHER"]
                                                                        ?: 0L) / (1000 * 60)
                                                        MoodUsage(
                                                                label,
                                                                sns.toInt(),
                                                                other.toInt(),
                                                                game.toInt()
                                                        )
                                                }
                                moodData = list
                        }
                }

                // Fetch App Ratios
                com.example.emotionapp.data.UsageAnalysisManager.fetchAppRatiosByEmotion(context) {
                        result ->
                        if (result != null) {
                                val periodKey =
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

                                // Flatten the list essentially or pick top 5 across all emotions?
                                // The UI shows "App Detail", presumably aggregating all emotions or
                                // showing top apps.
                                // The original logic mapped `result` (list) directly. Now we have
                                // `result` (Map<Emotion, List>).
                                // Let's aggregate all apps from all emotions for the "App Detail"
                                // list.

                                val aggregatedApps = mutableMapOf<String, AppDetail>()

                                periodKey.forEach { (_, apps) ->
                                        apps.forEach { app ->
                                                // We need to merge stats if the same app appears in
                                                // multiple emotions?
                                                // Actually the API structure is "GOOD": [{app
                                                // stats}], "NORMAL": ...
                                                // The previous code expected a flat list.
                                                // Let's assume we want to show a consolidated list.

                                                // Simplified: Just collect all distinct apps and
                                                // sum their usage?
                                                // Or does the API return 'total' which is total for
                                                // that app regardless of emotion?
                                                // Looking at user provided JSON:
                                                // "GOOD": [{ "app":..., "ms": ... }]
                                                // This seems like usage *while* feeling GOOD.

                                                // Let's create or update the AppDetail entry.
                                                val existing = aggregatedApps[app.pkgName]
                                                val totalMin =
                                                        (app.totalTime / (1000 * 60))
                                                                .toInt() // This is time for THIS
                                                // emotion

                                                // Since we don't know which emotion this specific
                                                // entry belongs to without the key,
                                                // we iterate through keys.
                                        }
                                }

                                /**
                                 * Re-implementing aggregation logic correctly: Iterate "GOOD",
                                 * "NORMAL", "BAD". For each app in that list, add to its
                                 * total/good/normal/bad stats.
                                 */
                                val appMap = mutableMapOf<String, AppDetail>()

                                periodKey.forEach { (emotion, apps) ->
                                        apps.forEach { item ->
                                                val pkg = item.pkgName
                                                val timeMin = (item.totalTime / (1000 * 60)).toInt()

                                                val current =
                                                        appMap.getOrPut(pkg) {
                                                                val icon =
                                                                        when {
                                                                                item.appName
                                                                                        .contains(
                                                                                                "kakao",
                                                                                                true
                                                                                        ) -> "💬"
                                                                                item.appName
                                                                                        .contains(
                                                                                                "insta",
                                                                                                true
                                                                                        ) -> "📷"
                                                                                item.appName
                                                                                        .contains(
                                                                                                "tube",
                                                                                                true
                                                                                        ) -> "▶️"
                                                                                item.appName
                                                                                        .contains(
                                                                                                "talk",
                                                                                                true
                                                                                        ) -> "🎵"
                                                                                else -> "📱"
                                                                        }
                                                                AppDetail(
                                                                        item.appName,
                                                                        icon,
                                                                        0,
                                                                        0,
                                                                        0,
                                                                        0
                                                                )
                                                        }

                                                val newTotal = current.total + timeMin
                                                val newGood =
                                                        current.good +
                                                                if (emotion == "GOOD") timeMin
                                                                else 0
                                                val newNormal =
                                                        current.normal +
                                                                if (emotion == "NORMAL") timeMin
                                                                else 0
                                                val newBad =
                                                        current.bad +
                                                                if (emotion == "BAD") timeMin else 0

                                                appMap[pkg] =
                                                        current.copy(
                                                                total = newTotal,
                                                                good = newGood,
                                                                normal = newNormal,
                                                                bad = newBad
                                                        )
                                        }
                                }

                                appDetailData =
                                        appMap.values
                                                .sortedByDescending { it.total }
                                                .take(5)
                                                .toList()
                        }
                }
        }

        val totalUsage = appDetailData.sumOf { it.total }
        val maxTotal = appDetailData.maxOfOrNull { it.total } ?: 1

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

                val maxMoodUsage =
                        remember(moodData) {
                                moodData.maxOfOrNull { maxOf(it.sns, it.game, it.other) } ?: 60
                        }
                val chartMax = if (maxMoodUsage == 0) 60 else maxMoodUsage

                // 감정별 막대 그래프 (Canvas + Layout)
                Row(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        // Y축 레이블 (Dynamic)
                        Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.End
                        ) {
                                // 4등분 (100%, 75%, 50%, 25%, 0%)
                                val steps = 4
                                for (i in steps downTo 0) {
                                        val value = (chartMax * (i.toFloat() / steps)).toInt()
                                        Text(
                                                text = value.toString(),
                                                fontSize = FontSizes.Small,
                                                color = PrimaryBrown.copy(alpha = 0.7f),
                                                textAlign = TextAlign.End,
                                                modifier = Modifier.width(32.dp) // 너비 약간 증가
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
                                                                        max = chartMax,
                                                                        color = PrimaryBrown
                                                                )
                                                                // 게임 (SecondaryBeige) - 이미지상 가운데
                                                                BarItem(
                                                                        value = item.game,
                                                                        max = chartMax,
                                                                        color = SecondaryBeige
                                                                )
                                                                // 기타 (HighlightOrange) - 이미지상 오른쪽
                                                                BarItem(
                                                                        value = item.other,
                                                                        max = chartMax,
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
                                                                // app.total은 '분' 단위입니다.
                                                                val totalMinutesVal = app.total
                                                                val days =
                                                                        totalMinutesVal / (24 * 60)
                                                                val hours =
                                                                        (totalMinutesVal %
                                                                                (24 * 60)) / 60
                                                                val mins = totalMinutesVal % 60

                                                                val timeText =
                                                                        buildString {
                                                                                if (days > 0)
                                                                                        append(
                                                                                                "${days}일 "
                                                                                        )
                                                                                if (hours > 0)
                                                                                        append(
                                                                                                "${hours}시간 "
                                                                                        )
                                                                                if (mins > 0)
                                                                                        append(
                                                                                                "${mins}분"
                                                                                        )
                                                                        }
                                                                                .trim()
                                                                                .ifEmpty { "0분" }

                                                                Text(
                                                                        text = timeText,
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
                                .background(
                                        color,
                                        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
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
