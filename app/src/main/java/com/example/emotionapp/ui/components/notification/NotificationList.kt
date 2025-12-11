package com.example.emotionapp.ui.components.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.theme.AccentBlue
import com.example.emotionapp.ui.theme.BackgroundBeige
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight


data class NotificationItemData(
        val id: Int,
        val time: String,
        val date: String,
        val message: String,
        val status: String = "all"
)

@Composable
fun NotificationList(
        title: String,
        notifications: List<NotificationItemData>,
        isExpanded: Boolean,
        onToggle: () -> Unit,
        description: String
) {
    Column {
        // 헤더
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isExpanded) AccentBlue else BackgroundBeige)
                                .clickable { onToggle() }
        ) {
            // 왼쪽 테두리
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(PrimaryBrown))

            Row(
                    modifier =
                            Modifier.fillMaxWidth()
                                    .padding(horizontal = Spacing.L, vertical = Spacing.L),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = title,
                        fontSize = FontSizes.Normal,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBrown
                )
                Text(
                        text = "${notifications.size}개",
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown
                )
            }
        }

        Text(
                text = description,
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.7f),
                modifier =
                        Modifier.padding(
                                start = Spacing.L,
                                top = Spacing.S,
                                bottom = if (isExpanded) Spacing.S else 0.dp
                        )
        )

        AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                    verticalArrangement = Arrangement.spacedBy(Spacing.M),
                    modifier = Modifier.padding(top = Spacing.S)
            ) { notifications.forEach { notification -> NotificationItem(notification) } }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationItemData) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BackgroundBeige)
    ) {
        // 왼쪽 테두리
        Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(PrimaryBrown))

        Column(
                modifier = Modifier.padding(Spacing.CardInner).weight(1f) // 남은 공간 채우기
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.XS),
                    modifier = Modifier.padding(bottom = Spacing.XS)
            ) {
                Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = PrimaryBrown.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                )
                Text(
                        text = "${notification.date} ${notification.time}",
                        fontSize = FontSizes.Small,
                        color = PrimaryBrown.copy(alpha = 0.7f)
                )
            }
            Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.S),
                    modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = PrimaryBrown,
                        modifier = Modifier.size(20.dp),
                )
                Text(
                        text = notification.message,
                        fontSize = FontSizes.Normal,
                        color = PrimaryBrown,
                        modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
