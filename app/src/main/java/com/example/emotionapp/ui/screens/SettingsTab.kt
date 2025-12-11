package com.example.emotionapp.ui.screens

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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

// 알림 빈도 타입 정의
enum class NotificationFrequency {
    NORMAL,
    LESS
}

@Composable
fun SettingsTab() {
    var notificationFrequency by remember { mutableStateOf(NotificationFrequency.NORMAL) }

    Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.XXL)
    ) {
        // 헤더
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Text(
                    text = "설정",
                    fontSize = FontSizes.Title,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
            )
            Text(
                    text = "앱 사용 환경을 설정할 수 있습니다",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Spacing.S)
            )
        }

        // 알림 설정
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
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
                        isSelected = notificationFrequency == NotificationFrequency.NORMAL,
                        onClick = { notificationFrequency = NotificationFrequency.NORMAL }
                )
                FrequencyOption(
                        label = "알림 적게",
                        isSelected = notificationFrequency == NotificationFrequency.LESS,
                        onClick = { notificationFrequency = NotificationFrequency.LESS }
                )
            }

            // 설명 박스
            Box(
                    modifier =
                            Modifier.padding(top = Spacing.L)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BackgroundBeige)
                                    .padding(Spacing.M)
            ) {
                Text(
                        text =
                                if (notificationFrequency == NotificationFrequency.NORMAL)
                                        "주의 패턴일 때부터 알림을 보냅니다."
                                else "위험 패턴이 감지될 때만 알림을 받습니다.",
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.8f)
                )
            }
        }

        // 개인정보 및 데이터 관리
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.M),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Spacing.L)
            ) {
                Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = PrimaryBrown,
                        modifier = Modifier.size(24.dp)
                )
                Text(
                        text = "개인정보 및 데이터 관리",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                SettingsItemButton(text = "데이터 삭제")
                SettingsItemButton(text = "개인정보 처리방침")
                SettingsItemButton(text = "서비스 이용약관")
            }
        }

        // 구글 계정
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.M),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = Spacing.L)
            ) {
                Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = PrimaryBrown,
                        modifier = Modifier.size(24.dp)
                )
                Text(
                        text = "구글 계정",
                        fontSize = FontSizes.SemiBold,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BackgroundBeige)
                                        .padding(Spacing.CardInner)
                ) {
                    Column {
                        Text(
                                text = "연결된 계정",
                                fontSize = FontSizes.Normal,
                                color = PrimaryBrown.copy(alpha = 0.7f)
                        )
                        Text(
                                text = "example@gmail.com",
                                fontSize = FontSizes.Normal,
                                color = PrimaryBrown,
                                modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                SettingsItemButton(text = "계정 정보")

                // 로그아웃 버튼
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(PrimaryBrown)
                                        .clickable { /* 로그아웃 로직 */}
                                        .padding(Spacing.CardInner),
                        contentAlignment = Alignment.Center
                ) { Text(text = "로그아웃", fontSize = FontSizes.Normal, color = SurfaceWhite) }
            }
        }

        // 앱 정보
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Text(
                    text = "앱 정보",
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(bottom = Spacing.M)
            )
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
                Text(
                        text = "버전: 1.0.0",
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                )
                Text(
                        text = "마지막 업데이트: 2025.11.27",
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                )
            }
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

@Composable
fun SettingsItemButton(text: String, onClick: () -> Unit = {}) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundBeige)
                            .clickable { onClick() }
                            .padding(Spacing.CardInner),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = FontSizes.Normal, color = PrimaryBrown)
        Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = PrimaryBrown,
                modifier = Modifier.size(20.dp)
        )
    }
}
