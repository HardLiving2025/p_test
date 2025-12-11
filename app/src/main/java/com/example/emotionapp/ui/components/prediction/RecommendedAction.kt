
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
            RecommendationItem(
                title = "🚶‍♂️ 산책하기",
                description = "기분이 좋지 않은 날에는 15분 정도 산책을 하면 숏폼 콘텐츠 사용 충동이 감소합니다."
            )
            RecommendationItem(
                title = "😌 휴식 취하기",
                description = "저녁 시간대 전에 충분한 휴식을 취하면 과도한 앱 사용을 예방할 수 있습니다."
            )
            RecommendationItem(
                title = "📱 디지털 디톡스",
                description = "20시 이후 스마트폰을 멀리 두고 다른 활동을 해보세요."
            )
            RecommendationItem(
                title = "📖 독서하기",
                description = "숏폼 콘텐츠 대신 책을 읽으면 수면의 질이 개선됩니다."
            )
        }
    }
}

@Composable
private fun RecommendationItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(BackgroundBeige)
            .height(IntrinsicSize.Min)
    ) {
        // 왼쪽 테두리
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(PrimaryBrown)
        )
        Column(
            modifier = Modifier
                .padding(Spacing.CardInner)
        ) {
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
