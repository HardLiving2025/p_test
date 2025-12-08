package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emotionapp.ui.theme.*

@Composable
fun RiskCombinationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "위험 감정 조합",
            fontSize = FontSizes.SemiBold,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(12.dp))

        RiskRow(
            label = "😞 나쁨 + 여유로움",
            levelLabel = "높음",
            levelBackground = PrimaryBrown,
            levelTextColor = SurfaceWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        RiskRow(
            label = "🙂 보통 + 바쁨",
            levelLabel = "중간",
            levelBackground = SecondaryBeige,
            levelTextColor = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(8.dp))

        RiskRow(
            label = "😊 좋음 + 여유로움",
            levelLabel = "낮음",
            levelBackground = AccentBlue,
            levelTextColor = PrimaryBrown
        )
    }
}

@Composable
private fun RiskRow(
    label: String,
    levelLabel: String,
    levelBackground: androidx.compose.ui.graphics.Color,
    levelTextColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundBeige, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = FontSizes.Normal,
            color = PrimaryBrown
        )
        Box(
            modifier = Modifier
                .background(levelBackground, RoundedCornerShape(999.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = levelLabel,
                fontSize = FontSizes.Normal,
                color = levelTextColor
            )
        }
    }
}
