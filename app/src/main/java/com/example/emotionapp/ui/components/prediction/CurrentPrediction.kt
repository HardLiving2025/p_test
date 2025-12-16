package com.example.emotionapp.ui.components.prediction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.AccentBlue
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing

@Composable
fun CurrentPrediction(data: com.example.emotionapp.data.PredictionResponse?) {
    if (data != null) {
        PredictionCard {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.L)) {
                // 1. 위험도 감지
                RiskItem(
                        icon = Icons.Filled.Warning,
                        title = data.riskAnalysis.title, // "위험도 높음"
                        description = data.riskAnalysis.message
                )
                // 2. 시간대 예측 (유효할 경우만)
                if (data.usagePrediction.hasPrediction) {
                    RiskItem(
                            icon = Icons.Filled.Schedule,
                            title = "시간대 예측",
                            description = data.usagePrediction.message
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskItem(icon: ImageVector, title: String, description: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.M)) {
        Box(
                modifier =
                        Modifier.clip(RoundedCornerShape(8.dp)).background(AccentBlue).padding(8.dp)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
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
