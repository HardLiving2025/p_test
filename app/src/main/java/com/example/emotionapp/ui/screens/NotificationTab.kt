package com.example.emotionapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.emotionapp.data.NotificationManager
import com.example.emotionapp.ui.components.notification.NotificationItem
import com.example.emotionapp.ui.components.notification.NotificationItemData
import com.example.emotionapp.ui.components.notification.NotificationList
import com.example.emotionapp.ui.components.notification.NotificationPermissionToggle
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite
import com.example.emotionapp.utils.PermissionUtils

@Composable
fun NotificationTab(period: Period) {
        // 섹션 확장 상태
        var expandedAll by remember { mutableStateOf(false) }
        var expandedChecked by remember { mutableStateOf(false) }
        var expandedIgnored by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        var isPermissionGranted by remember {
                mutableStateOf(PermissionUtils.hasNotificationPermission(context))
        }

        DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                                isPermissionGranted =
                                        PermissionUtils.hasNotificationPermission(context)
                        }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
        }

        var notifications by remember { mutableStateOf<List<NotificationItemData>>(emptyList()) }

        var allNotificationsList by remember {
                mutableStateOf<List<NotificationItemData>>(emptyList())
        }

        // Status lists kept empty for now until API supports status filtering
        val checkedNotificationsList = emptyList<NotificationItemData>()
        val ignoredNotificationsList = emptyList<NotificationItemData>()

        LaunchedEffect(Unit) {
                NotificationManager.fetchRecentNotifications(
                        context,
                        onResult = { fetchedList ->
                                val mappedList =
                                        fetchedList.map { serverItem ->
                                                // Parse "2025-12-17 10:49:39"
                                                val dateTimeParts =
                                                        serverItem.message_type.split(" ")
                                                val datePart =
                                                        if (dateTimeParts.isNotEmpty())
                                                                dateTimeParts[0]
                                                        else serverItem.message_type
                                                // User requested to display ONLY date. Setting time
                                                // to empty string.
                                                val timePart = ""

                                                NotificationItemData(
                                                        id = serverItem.noti_id,
                                                        time = timePart,
                                                        date = datePart,
                                                        message = serverItem.message_body,
                                                        status = "all" // Default status as API
                                                        // doesn't provide it yet
                                                        )
                                        }
                                notifications = mappedList
                                allNotificationsList = mappedList
                        },
                        onError = {
                                // Handle error if needed, for now just log
                                // Log is already done in NotificationManager
                        }
                )

                // Fetch notification message and log it
                NotificationManager.fetchNotificationMessage(
                        context,
                        onResult = {
                                // Log is already handled in NotificationManager
                        },
                        onError = {
                                // Log is already handled in NotificationManager
                        }
                )
        }

        Column(
                modifier = Modifier.fillMaxSize().padding(Spacing.ScreenPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.XXL) // 기본 간격
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
                                text = "알림",
                                fontSize = FontSizes.Title,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBrown
                        )
                        Text(
                                text =
                                        if (isPermissionGranted) "알림 내역입니다."
                                        else "알림 내역입니다. 알림 설정을 허용하시면 알림을 받아볼수 있습니다.",
                                fontSize = FontSizes.Normal,
                                color = PrimaryBrown.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = Spacing.S)
                        )
                }

                NotificationPermissionToggle(
                        isPermissionGranted = isPermissionGranted,
                        onToggle = { PermissionUtils.openNotificationSettings(context) }
                )

                Column(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .alpha(if (isPermissionGranted) 1f else 0.5f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.XXL)
                ) {
                        // 최근 알림
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(Spacing.M))
                                                .background(SurfaceWhite)
                                                .padding(Spacing.CardInner)
                        ) {
                                Text(
                                        text = "최근 알림",
                                        fontSize = FontSizes.SemiBold,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryBrown,
                                        modifier = Modifier.padding(bottom = Spacing.L)
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(Spacing.M)) {
                                        notifications.take(3).forEach { notification ->
                                                NotificationItem(notification)
                                        }
                                }

                                Text(
                                        text = "최근 3개의 알림 목록입니다.",
                                        fontSize = FontSizes.Normal,
                                        color = PrimaryBrown.copy(alpha = 0.7f),
                                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.M),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                        }

                        // 알림 목록 (확장 가능)
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .clip(RoundedCornerShape(Spacing.M))
                                                .background(SurfaceWhite)
                                                .padding(Spacing.CardInner),
                                verticalArrangement = Arrangement.spacedBy(Spacing.L)
                        ) {
                                Text(
                                        text = "알림 목록",
                                        fontSize = FontSizes.SemiBold,
                                        color = PrimaryBrown
                                )

                                NotificationList(
                                        title = "전체 알림",
                                        notifications = allNotificationsList,
                                        isExpanded = expandedAll,
                                        onToggle = { expandedAll = !expandedAll },
                                        description = "받은 모든 알림의 목록입니다."
                                )

                                NotificationList(
                                        title = "확인한 알림",
                                        notifications = checkedNotificationsList,
                                        isExpanded = expandedChecked,
                                        onToggle = { expandedChecked = !expandedChecked },
                                        description = "알림을 확인하고 적절히 대응한 알림 목록입니다."
                                )

                                NotificationList(
                                        title = "무시한 알림",
                                        notifications = ignoredNotificationsList,
                                        isExpanded = expandedIgnored,
                                        onToggle = { expandedIgnored = !expandedIgnored },
                                        description = "알림을 받았는데도 무시해버린 알림 목록입니다."
                                )
                        }
                }
        }
}
