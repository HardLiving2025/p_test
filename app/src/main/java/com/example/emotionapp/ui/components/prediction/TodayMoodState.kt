package com.example.emotionapp.ui.components.prediction

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing

@Composable
fun TodayMoodState(mood: String, state: String) {
    PredictionCard {
        Text(
                text = "오늘의 기분 / 상태",
                fontSize = FontSizes.SemiBold,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
        )
        Text(
                text = "$mood · $state",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = Spacing.S)
        )
        Text(
                text = "오늘은 기분이 좋지 않고 여유로운 날이에요. 잠깐 산책을 해보는 것이 도움이 될 수 있어요.",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = Spacing.S)
        )
    }
}
