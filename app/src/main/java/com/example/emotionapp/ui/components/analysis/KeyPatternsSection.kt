package com.example.emotionapp.ui.components.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.*

@Composable
fun KeyPatternsSection() {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(SurfaceWhite, RoundedCornerShape(Spacing.L))
                            .padding(Spacing.CardInner)
    ) {
        Text(
                text = "주요 패턴",
                fontSize = FontSizes.SemiBold,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryBrown
        )

        Spacer(modifier = Modifier.height(Spacing.M))

        InsightBlock(
                chipText = "기분이 좋지 않을 때 기타 앱 ↑",
                body = "기분이 좋지 않은 날에는 릴스와 같은 숏폼 콘텐츠 사용량이 평균보다 42% 증가했습니다."
        )

        Spacer(modifier = Modifier.height(Spacing.S))

        InsightBlock(chipText = "20-24시 집중 사용", body = "저녁 시간대에 모든 앱 사용량이 급증하는 패턴이 관찰됩니다.")
    }
}

@Composable
private fun InsightBlock(chipText: String, body: String) {
    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(BackgroundBeige, RoundedCornerShape(Spacing.M))
                            .padding(Spacing.M)
    ) {
        Box(
                modifier =
                        Modifier.background(AccentBlue, RoundedCornerShape(999.dp))
                                .padding(horizontal = Spacing.S, vertical = Spacing.XS)
        ) { Text(text = chipText, fontSize = FontSizes.Normal, color = PrimaryBrown) }
        Spacer(modifier = Modifier.height(Spacing.S))
        Text(text = body, fontSize = FontSizes.Normal, color = PrimaryBrown.copy(alpha = 0.8f))
    }
}
