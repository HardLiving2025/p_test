package com.example.emotionapp.ui.components.prediction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.data.PredictionDescriptionResponse
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing

@Composable
fun TodayMoodState(data: PredictionDescriptionResponse?) {
        PredictionCard {
                if (data != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
                                Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                ) {
                                        Text(
                                                text = data.title,
                                                fontSize = FontSizes.SemiBold,
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryBrown
                                        )
                                }
                                Text(
                                        text = data.description,
                                        fontSize = FontSizes.Normal,
                                        color = PrimaryBrown.copy(alpha = 0.8f)
                                )
                        }
                }
        }
}
