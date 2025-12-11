package com.example.emotionapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.components.DailyInsightPopup
import com.example.emotionapp.ui.theme.*

/** 탭 종류 */
enum class HomeTab {
    ANALYSIS,
    PREDICTION,
    NOTIFICATION,
    SETTINGS
}

/** 기간 종류 */
enum class Period {
    YESTERDAY,
    WEEK,
    TWO_WEEKS,
    MONTH
}

/** 바텀 탭 정보 */
data class BottomTab(val id: HomeTab, val label: String, val icon: ImageVector)

@Composable
fun HomeScreen() {
    var activeTab by remember { mutableStateOf(HomeTab.ANALYSIS) }
    var selectedPeriod by remember { mutableStateOf(Period.YESTERDAY) }

    // 팝업 순서 제어: InitialPull1MonthDataPopup -> DailyInsightPopup
    var showInitialPopup by remember { mutableStateOf(true) }
    var showInsightPopup by remember { mutableStateOf(false) } // 초기에는 false

    val tabs =
            listOf(
                    BottomTab(HomeTab.ANALYSIS, "분석", Icons.Filled.BarChart),
                    BottomTab(HomeTab.PREDICTION, "예측", Icons.Filled.ShowChart),
                    BottomTab(HomeTab.NOTIFICATION, "알림", Icons.Filled.Notifications),
                    BottomTab(HomeTab.SETTINGS, "설정", Icons.Filled.Settings)
            )

    val showPeriodSelector = activeTab != HomeTab.SETTINGS

    val currentTab = activeTab
    val onTabSelected: (HomeTab) -> Unit = { activeTab = it }

    Scaffold(
            containerColor = BackgroundBeige,
            bottomBar = {
                BottomTabBar(tabs = tabs, activeTab = currentTab, onTabSelected = onTabSelected)
            }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 상단 기간 선택
                if (showPeriodSelector) {
                    PeriodSelector(
                            selectedPeriod = selectedPeriod,
                            onPeriodSelected = { selectedPeriod = it }
                    )
                }

                // 콘텐츠 영역
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(bottom = 16.dp)
                ) {
                    when (activeTab) {
                        HomeTab.ANALYSIS -> AnalysisTab(period = selectedPeriod)
                        HomeTab.PREDICTION -> PredictionTab(period = selectedPeriod)
                        HomeTab.NOTIFICATION -> NotificationTab(period = selectedPeriod)
                        HomeTab.SETTINGS -> SettingsTab()
                    }
                }
            }

            // 팝업 순차 표시
            if (showInitialPopup) {
                com.example.emotionapp.ui.components.InitialPull1MonthDataPopup(
                        onClose = {
                            showInitialPopup = false
                            showInsightPopup = true
                        }
                )
            } else if (showInsightPopup) {
                DailyInsightPopup(onClose = { showInsightPopup = false })
            }
        }
    }
}

/** 상단 기간 선택 컴포넌트 */
@Composable
private fun PeriodSelector(selectedPeriod: Period, onPeriodSelected: (Period) -> Unit) {
    val periods =
            listOf(
                    Period.YESTERDAY to "어제",
                    Period.WEEK to "일주일",
                    Period.TWO_WEEKS to "2주일",
                    Period.MONTH to "한달"
            )

    Box(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(SurfaceWhite)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach { (id, label) ->
                val isSelected = selectedPeriod == id
                Button(
                        onClick = { onPeriodSelected(id) },
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor =
                                                if (isSelected) AccentBlue else BackgroundBeige
                                ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                            text = label,
                            color = PrimaryBrown,
                            fontSize = FontSizes.Normal,
                    )
                }
            }
        }
    }
}

/** 하단 탭 바 */
@Composable
private fun BottomTabBar(
        tabs: List<BottomTab>,
        activeTab: HomeTab,
        onTabSelected: (HomeTab) -> Unit
) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .background(PrimaryBrown)
                            .border(width = 1.dp, color = SecondaryBeige)
                            .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
    ) {
        tabs.forEach { tab ->
            val isActive = activeTab == tab.id

            Box(
                    modifier =
                            Modifier.weight(1f) // 탭 4개면 4등분
                                    .height(56.dp) // 터치 영역 높이 확보
                                    .clickable { onTabSelected(tab.id) } // 박스 전체 클릭
                                    .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (isActive) AccentBlue else SurfaceWhite
                    )
                    Text(
                            text = tab.label,
                            fontSize = FontSizes.Small,
                            color = if (isActive) AccentBlue else SurfaceWhite
                    )
                }
            }
        }
    }
}
