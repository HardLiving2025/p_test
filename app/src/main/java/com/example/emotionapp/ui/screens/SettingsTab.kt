package com.example.emotionapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.emotionapp.ui.components.setting.AccountManagementSection
import com.example.emotionapp.ui.components.setting.NotificationFrequency
import com.example.emotionapp.ui.components.setting.NotificationSettingsSection
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite

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
                                        .clip(RoundedCornerShape(Spacing.M))
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
                NotificationSettingsSection(
                        frequency = notificationFrequency,
                        onFrequencyChanged = { notificationFrequency = it }
                )

                // 개인정보 및 계정 관리
                AccountManagementSection()

                // 앱 정보
                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .clip(RoundedCornerShape(Spacing.M))
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
                                        text = "마지막 업데이트: 2025.12.16",
                                        fontSize = FontSizes.Normal,
                                        color = PrimaryBrown.copy(alpha = 0.7f)
                                )
                        }
                }
        }
}
