package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.theme.*
import androidx.compose.ui.unit.dp

@Composable
fun RiskCombinationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceWhite, RoundedCornerShape(16.dp))
            .padding(Spacing.CardInner)
    ) {
        Text(
            text = "위험 감정 조합",
            fontSize = FontSizes.SemiBold,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(Spacing.M))

        RiskRow(
            label = "😞 나쁨 + 여유로움",
            levelLabel = "높음",
            levelBackground = PrimaryBrown,
            levelTextColor = SurfaceWhite
        )

        Spacer(modifier = Modifier.height(Spacing.S))

        RiskRow(
            label = "🙂 보통 + 바쁨",
            levelLabel = "중간",
            levelBackground = SecondaryBeige,
            levelTextColor = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(Spacing.S))

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
    levelBackground: Color,
    levelTextColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundBeige, RoundedCornerShape(12.dp))
            .padding(horizontal = Spacing.M, vertical = Spacing.S),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically   // ✅ 이모지 + 텍스트 + 배지 세로 중앙 정렬
    ) {
        Text(
            text = label,
            fontSize = FontSizes.Normal,
            color = PrimaryBrown
        )

        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 28.dp)        // ✅ 배지 최소 높이
                .background(levelBackground, RoundedCornerShape(999.dp))
                .padding(horizontal = Spacing.M, vertical = Spacing.XS),
            contentAlignment = Alignment.Center          // ✅ 텍스트 배지 안에서 중앙 정렬
        ) {
            Text(
                text = levelLabel,
                fontSize = FontSizes.Small,
                color = levelTextColor
            )
        }
    }
}
