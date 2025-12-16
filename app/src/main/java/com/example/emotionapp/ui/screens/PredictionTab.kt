package com.example.emotionapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.components.prediction.CurrentPrediction
import com.example.emotionapp.ui.components.prediction.PredictionCard
import com.example.emotionapp.ui.components.prediction.RecommendedAction
import com.example.emotionapp.ui.components.prediction.TodayMoodState
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing

@Composable
fun PredictionTab() {
    // 오늘의 기분/상태 (실제로는 앱에서 전달받아야 함)
    val todayMood = "😞 나쁨"
    val todayState = "여유로움"

    Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.M)
    ) {
        // 헤더
        PredictionCard {
            Text(
                    text = "예측",
                    fontSize = FontSizes.Title,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
            )
            Text(
                    text = "패턴을 기반으로 예측한 오늘의 위험 요소와 추천사항입니다",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Spacing.S)
            )
        }

        // 오늘의 기분/상태
        TodayMoodState(mood = todayMood, state = todayState)

        // 위험 예측
        CurrentPrediction()

        // 추천 행동
        RecommendedAction()
    }
}
