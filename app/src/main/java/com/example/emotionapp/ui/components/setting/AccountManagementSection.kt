package com.example.emotionapp.ui.components.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
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
import com.example.emotionapp.ui.theme.SurfaceWhite

@Composable
fun AccountManagementSection() {
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
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = PrimaryBrown,
                    modifier = Modifier.size(24.dp)
            )
            Text(
                    text = "개인정보 및 계정 관리",
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.S)) {
            // 연결된 계정 정보
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(Spacing.S))
                                    .background(BackgroundBeige)
                                    .padding(Spacing.CardInner)
            ) {
                Column {
                    Text(
                            text = "연결된 계정",
                            fontSize = FontSizes.Normal,
                            color = PrimaryBrown.copy(alpha = 0.7f)
                    )
                    // 하드코딩된 계정 정보
                    Text(
                            text = "example@gmail.com",
                            fontSize = FontSizes.Normal,
                            color = PrimaryBrown,
                            modifier = Modifier.padding(top = Spacing.XS)
                    )
                }
            }

            SettingsItemButton(text = "계정 정보")
            SettingsItemButton(text = "데이터 삭제")

            // 로그아웃 버튼
            Box(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(Spacing.S))
                                    .background(PrimaryBrown)
                                    .clickable { /* 로그아웃 로직 */}
                                    .padding(Spacing.CardInner),
                    contentAlignment = Alignment.Center
            ) { Text(text = "로그아웃", fontSize = FontSizes.Normal, color = SurfaceWhite) }
        }
    }
}

@Composable
private fun SettingsItemButton(text: String, onClick: () -> Unit = {}) {
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
