package com.example.emotionapp.ui.components.prediction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.BackgroundBeige
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing

@Composable
fun RecommendedAction() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var predictionData by remember {
        mutableStateOf<com.example.emotionapp.data.PredictionResponse?>(null)
    }

    LaunchedEffect(Unit) {
        com.example.emotionapp.data.PredictionManager.fetchPrediction(context) { result ->
            predictionData = result
        }
    }

    // Local variable for smart cast
    val data = predictionData
    if (data != null) {
        val recommendations = data.recommendations
        if (recommendations.isNotEmpty()) {
            PredictionCard {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.S),
                        modifier = Modifier.padding(bottom = Spacing.L)
                ) {
                    Icon(
                            imageVector = Icons.Filled.Lightbulb,
                            contentDescription = null,
                            tint = PrimaryBrown,
                            modifier = Modifier.size(24.dp)
                    )
                    Text(
                            text = "추천 행동",
                            fontSize = FontSizes.SemiBold,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBrown
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
                    recommendations.forEach { item ->
                        RecommendationItem(title = item.title, description = item.description)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationItem(title: String, description: String) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundBeige)
                            .height(IntrinsicSize.Min)
    ) {
        // 왼쪽 테두리
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(PrimaryBrown))
        Column(modifier = Modifier.padding(Spacing.CardInner)) {
            Text(
                    text = title,
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown
            )
            Text(
                    text = description,
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
