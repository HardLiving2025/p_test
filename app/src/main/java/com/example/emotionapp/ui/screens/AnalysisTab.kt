/**
감정별 평균 사용량 → EmotionUsageSection.kt
시간대별 평균 사용량 → TimeUsageSection.kt
감정/상황별 총 사용량 → MoodStateUsageSection.kt
위험 감정 조합 → RiskCombinationSection.kt
주요 패턴(인사이트) → KeyPatternsSection.kt
 */
package com.example.emotionapp.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.readWeeklyUsageJsonFromFile
import com.example.emotionapp.data.saveWeeklyUsageJsonToFile
import com.example.emotionapp.ui.components.analysis.EmotionUsageSection
import com.example.emotionapp.ui.components.analysis.KeyPatternsSection
import com.example.emotionapp.ui.components.analysis.MoodStateUsageSection
import com.example.emotionapp.ui.components.analysis.RiskCombinationSection
import com.example.emotionapp.ui.components.analysis.TimeUsageSection
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.SurfaceWhite

@Composable
fun AnalysisTab(period: Period) {
    val context = LocalContext.current
    var showDetail by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헤더 카드
        AnalysisHeaderCard(period = period)

        Spacer(modifier = Modifier.height(12.dp))

        // 감정별 평균 사용량 + 앱 상세
        EmotionUsageSection(
            showDetail = showDetail,
            onToggleDetail = { showDetail = !showDetail }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 시간대별 평균 사용량
        TimeUsageSection()
        Spacer(modifier = Modifier.height(12.dp))

        // 감정/상황별 총 사용량
        MoodStateUsageSection()
        Spacer(modifier = Modifier.height(12.dp))

        // 위험 감정 조합
        RiskCombinationSection()
        Spacer(modifier = Modifier.height(12.dp))

        // 주요 패턴(인사이트)
        KeyPatternsSection()

        Spacer(modifier = Modifier.height(24.dp))

        // 📌 JSON 저장 + 로그 출력 버튼
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                // 1) JSON 파일 저장
                saveWeeklyUsageJsonToFile(context)

                // 2) 다시 읽어서 내용 확인
                val json = readWeeklyUsageJsonFromFile(context)

                // 3) Logcat에 출력
                Log.d("UsageJSON", "Saved JSON: $json")

                // 4) 사용자에게 토스트 메시지
                Toast.makeText(
                    context,
                    "일주일 사용 기록을 저장했어요. (Logcat에서 UsageJSON 태그 확인)",
                    Toast.LENGTH_SHORT
                ).show()
            }
        ) {
            Text("일주일 사용 기록 저장")
        }
    }
}

@Composable
private fun AnalysisHeaderCard(period: Period) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "분석",
            fontSize = FontSizes.Title,
            fontWeight = FontWeight.Bold,
            color = PrimaryBrown
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when (period) {
                Period.YESTERDAY -> "어제의 패턴을 분석했어요"
                Period.WEEK      -> "최근 일주일의 패턴을 분석했어요"
                Period.TWO_WEEKS -> "최근 2주일의 패턴을 분석했어요"
                Period.MONTH     -> "최근 한달의 패턴을 분석했어요"
            },
            fontSize = FontSizes.Normal,
            color = PrimaryBrown.copy(alpha = 0.7f)
        )
    }
}
