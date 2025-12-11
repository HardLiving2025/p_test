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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.components.notification.NotificationFrequencyChart
import com.example.emotionapp.ui.components.notification.NotificationItem
import com.example.emotionapp.ui.components.notification.NotificationItemData
import com.example.emotionapp.ui.components.notification.NotificationList
import com.example.emotionapp.ui.components.notification.ResponseTimeChart
import com.example.emotionapp.ui.theme.FontSizes
import com.example.emotionapp.ui.theme.PrimaryBrown
import com.example.emotionapp.ui.theme.Spacing
import com.example.emotionapp.ui.theme.SurfaceWhite

@Composable
fun NotificationTab(period: Period) {
    // 섹션 확장 상태
    var expandedAll by remember { mutableStateOf(false) }
    var expandedChecked by remember { mutableStateOf(false) }
    var expandedIgnored by remember { mutableStateOf(false) }

    // 더미 데이터 (React 코드 참조)
    val notifications =
            listOf(
                    NotificationItemData(
                            1,
                            "23:45",
                            "2025-11-26",
                            "위험 시간대에 12분간 사용을 이어가셨어요. 이 시간대의 숏폼 콘텐츠 소비는 수면 리듬을 가장 많이 무너뜨립니다.",
                            "checked"
                    ),
                    NotificationItemData(
                            2,
                            "22:15",
                            "2025-11-26",
                            "기분이 좋지 않은 상태에서 SNS 사용이 감지되었습니다. 잠시 스마트폰을 내려놓고 산책을 해보는 건 어떨까요?",
                            "checked"
                    ),
                    NotificationItemData(
                            3,
                            "21:30",
                            "2025-11-26",
                            "패턴 분석 결과, 지금 시간대부터 과도한 앱 사용이 시작될 가능성이 높습니다. 주의해주세요.",
                            "ignored"
                    ),
                    NotificationItemData(
                            4,
                            "20:10",
                            "2025-11-26",
                            "저녁 시간대 과다 사용이 예측됩니다.",
                            "checked"
                    ),
                    NotificationItemData(
                            5,
                            "18:25",
                            "2025-11-26",
                            "기분이 나쁜 상태에서 기타 앱 사용이 증가하고 있습니다.",
                            "ignored"
                    )
            )

    val notificationFrequency =
            listOf(
                    "00-04" to 2,
                    "04-08" to 0,
                    "08-12" to 3,
                    "12-16" to 5,
                    "16-20" to 7,
                    "20-24" to 12
            )

    val responseTimeData =
            mapOf(
                    Period.YESTERDAY to listOf("어제" to 3.2),
                    Period.WEEK to
                            listOf(
                                    "월" to 2.8,
                                    "화" to 3.1,
                                    "수" to 2.5,
                                    "목" to 2.9,
                                    "금" to 3.5,
                                    "토" to 4.2,
                                    "일" to 3.8
                            ),
                    Period.TWO_WEEKS to listOf("1주차" to 2.9, "2주차" to 3.2),
                    Period.MONTH to listOf("1주" to 2.7, "2주" to 2.9, "3주" to 3.1, "4주" to 3.4)
            )

    val currentResponseData = responseTimeData[period] ?: emptyList()

    val allNotificationsList =
            listOf(
                    NotificationItemData(
                            6,
                            "23:45",
                            "2025-11-26",
                            "위험 시간대에 12분간 사용을 이어가셨어요. 이 시간대의 숏폼 콘텐츠 소비는 수면 리듬을 가장 많이 무너뜨립니다."
                    ),
                    NotificationItemData(
                            7,
                            "22:15",
                            "2025-11-26",
                            "기분이 좋지 않은 상태에서 SNS 사용이 감지되었습니다. 잠시 스마트폰을 내려놓고 산책을 해보는 건 어떨까요?"
                    ),
                    NotificationItemData(
                            8,
                            "21:30",
                            "2025-11-26",
                            "패턴 분석 결과, 지금 시간대부터 과도한 앱 사용이 시작될 가능성이 높습니다. 주의해주세요."
                    )
            )

    val checkedNotificationsList =
            listOf(
                    NotificationItemData(
                            9,
                            "23:45",
                            "2025-11-26",
                            "위험 시간대에 12분간 사용을 이어가셨어요. 이 시간대의 숏폼 콘텐츠 소비는 수면 리듬을 가장 많이 무너뜨립니다."
                    ),
                    NotificationItemData(10, "20:10", "2025-11-26", "저녁 시간대 과다 사용이 예측됩니다.")
            )

    val ignoredNotificationsList =
            listOf(
                    NotificationItemData(
                            11,
                            "21:30",
                            "2025-11-26",
                            "패턴 분석 결과, 지금 시간대부터 과도한 앱 사용이 시작될 가능성이 높습니다. 주의해주세요."
                    ),
                    NotificationItemData(
                            12,
                            "18:25",
                            "2025-11-26",
                            "기분이 나쁜 상태에서 기타 앱 사용이 증가하고 있습니다."
                    )
            )

    Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.XXL) // 기본 간격
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
                    text = "알림",
                    fontSize = FontSizes.Title,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBrown
            )
            Text(
                    text =
                            "${
                    when (period) {
                        Period.YESTERDAY -> "어제"
                        Period.WEEK -> "최근 일주일"
                        Period.TWO_WEEKS -> "최근 2주일"
                        Period.MONTH -> "최근 한달"
                    }
                }의 알림 내역입니다",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Spacing.S)
            )
        }

        // 시간대별 알림 빈도
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Text(
                    text = "시간대별 알림 빈도",
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(bottom = Spacing.L)
            )

            NotificationFrequencyChart(data = notificationFrequency)

            Text(
                    text = "총 ${notificationFrequency.sumOf { it.second }}개의 알림이 전송되었습니다",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.S),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // 알림 통계 (평균 응답 시간)
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner)
        ) {
            Text(
                    text = "알림 통계",
                    fontSize = FontSizes.SemiBold,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(bottom = Spacing.L)
            )
            Text(
                    text = "평균 응답 시간",
                    fontSize = FontSizes.Normal,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryBrown,
                    modifier = Modifier.padding(bottom = Spacing.L)
            )

            ResponseTimeChart(data = currentResponseData)

            Text(
                    text = "알림을 받고 확인하기까지의 평균 시간입니다.",
                    fontSize = FontSizes.Normal,
                    color = PrimaryBrown.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.S),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // 최근 알림
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
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
                notifications.take(3).forEach { notification -> NotificationItem(notification) }
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWhite)
                                .padding(Spacing.CardInner),
                verticalArrangement = Arrangement.spacedBy(Spacing.L)
        ) {
            Text(text = "알림 목록", fontSize = FontSizes.SemiBold, color = PrimaryBrown)

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
