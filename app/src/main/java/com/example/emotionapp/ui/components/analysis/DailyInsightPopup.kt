package com.example.emotionapp.ui.components.analysis

import android.content.pm.PackageManager
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
import androidx.compose.ui.platform.LocalContext
import com.example.emotionapp.data.UsageAnalysisManager
import com.example.emotionapp.ui.theme.*

@Composable
fun DailyInsightPopup(onClose: () -> Unit) {
        val context = LocalContext.current
        var insightMessage by remember { mutableStateOf("분석 중...") }

        LaunchedEffect(Unit) {
                UsageAnalysisManager.fetchAppRatiosByEmotion(context) { result ->
                        if (result != null) {
                                // "어제" 데이터
                                val periodMap =
                                        result.yesterday // Map<Emotion, List<AppUsageDetail>>

                                // 1. 앱별 사용량 합계 계산 (패키지명 기준)
                                val appUsageMap = mutableMapOf<String, Long>() // pkg -> totalMs

                                periodMap.values.forEach { appList ->
                                        appList.forEach { app ->
                                                val current =
                                                        appUsageMap.getOrDefault(app.pkgName, 0L)
                                                appUsageMap[app.pkgName] = current + app.totalTime
                                        }
                                }

                                // 2. 총 핸드폰 사용량 계산
                                val totalMs = appUsageMap.values.sum()
                                val totalHours = totalMs / (1000 * 60 * 60)
                                val totalMinutes = (totalMs % (1000 * 60 * 60)) / (1000 * 60)

                                val timeString =
                                        buildString {
                                                if (totalHours > 0) append("${totalHours}시간 ")
                                                append("${totalMinutes}분")
                                        }
                                                .ifEmpty { "0분" }

                                // 3. Top 3 앱 추출 및 이름 변환
                                val topApps =
                                        appUsageMap
                                                .entries
                                                .sortedByDescending { it.value }
                                                .take(3)
                                                .map { entry ->
                                                        val pkg = entry.key
                                                        try {
                                                                val info =
                                                                        context.packageManager
                                                                                .getApplicationInfo(
                                                                                        pkg,
                                                                                        0
                                                                                )
                                                                context.packageManager
                                                                        .getApplicationLabel(info)
                                                                        .toString()
                                                        } catch (
                                                                e:
                                                                        PackageManager.NameNotFoundException) {
                                                                var foundName = pkg
                                                                run search@{
                                                                        periodMap.values.forEach {
                                                                                list ->
                                                                                val match =
                                                                                        list.find {
                                                                                                it.pkgName ==
                                                                                                        pkg
                                                                                        }
                                                                                if (match != null) {
                                                                                        foundName =
                                                                                                match.appName
                                                                                        return@search
                                                                                }
                                                                        }
                                                                }
                                                                foundName
                                                        }
                                                }

                                val topAppsString = topApps.joinToString(", ")

                                // 4. 메시지 구성
                                insightMessage =
                                        "어제 총 핸드폰 사용량은 ${timeString}이에요. ${topAppsString} 등을 가장 많이 사용하셨는데, 오늘은 이에 유의해서 사용량을 줄여나가 봐요!"
                        } else {
                                insightMessage = "데이터를 불러오지 못했습니다."
                        }
                }
        }

        Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxWidth(0.85f)
                                        .background(SurfaceWhite, RoundedCornerShape(Spacing.XXL))
                                        .padding(Spacing.XL)
                ) {
                        Text(
                                text = "어제 행동 패턴 분석",
                                fontSize = FontSizes.SemiBold,
                                color = PrimaryBrown
                        )

                        Spacer(modifier = Modifier.height(Spacing.M))

                        Text(
                                text = insightMessage,
                                fontSize = FontSizes.Normal,
                                color = PrimaryBrown,
                                lineHeight = FontSizes.Normal * 1.5 // 가독성을 위해 줄간격 추가
                        )

                        Spacer(modifier = Modifier.height(Spacing.L))

                        Button(
                                onClick = onClose,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBrown),
                                shape = RoundedCornerShape(Spacing.L)
                        ) { Text(text = "확인", color = SurfaceWhite, fontSize = FontSizes.Normal) }
                }
        }
}
