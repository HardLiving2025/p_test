package com.example.emotionapp.ui.components.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.AccentBlue
import com.example.emotionapp.ui.theme.BackgroundBeige
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite

enum class NotificationFrequency {
    NORMAL,
    LESS
}

@Composable
fun NotificationFrequencySelection() {
    var selectedFrequency by remember { mutableStateOf(NotificationFrequency.NORMAL) }

    Column(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(Spacing.M))
                            .background(SurfaceWhite)
                            .padding(Spacing.CardInner)
    ) {
        Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.M),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = Spacing.L)
        ) {
            Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = null,
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
            )
            Text(
                    text = "알림 단계 설정",
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown
            )
        }

        Text(
                text = "알림 빈도를 선택해주세요",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = Spacing.L)
        )

        // 빈도 선택 버튼들
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            FrequencyOption(
                    label = "보통",
                    isSelected = selectedFrequency == NotificationFrequency.NORMAL,
                    onClick = { selectedFrequency = NotificationFrequency.NORMAL }
            )
            FrequencyOption(
                    label = "알림 적게",
                    isSelected = selectedFrequency == NotificationFrequency.LESS,
                    onClick = { selectedFrequency = NotificationFrequency.LESS }
            )
        }

        // 설명 박스
        Box(
                modifier =
                        Modifier.padding(top = Spacing.L)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Spacing.S))
                                .background(BackgroundBeige)
                                .padding(Spacing.M)
        ) {
            Text(
                    text =
                            if (selectedFrequency == NotificationFrequency.NORMAL)
                                    "주의 패턴일 때부터 알림을 보냅니다."
                            else "위험 패턴이 감지될 때만 알림을 받습니다.",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FrequencyOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AccentBlue else BackgroundBeige)
                            .border(
                                    width = 2.dp,
                                    color = if (isSelected) AccentBlue else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onClick() }
                            .padding(Spacing.CardInner),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = FontSizes.Normal, color = PrimaryBrown)
        if (isSelected) {
            Box(
                    modifier = Modifier.size(20.dp).clip(CircleShape).background(PrimaryBrown),
                    contentAlignment = Alignment.Center
            ) { Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SurfaceWhite)) }
        }
    }
}
