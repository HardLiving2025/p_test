package com.example.emotionapp.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.emotionapp.ui.components.analysis.DailyInsightPopup
import com.example.emotionapp.ui.theme.*
import com.example.emotionapp.utils.PermissionUtils

/** 탭 종류 */
enum class HomeTab {
        ANALYSIS,
        PREDICTION,
        NOTIFICATION,
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
fun HomeScreen(onLogout: () -> Unit = {}) {
        var activeTab by remember { mutableStateOf(HomeTab.ANALYSIS) }
        var selectedPeriod by remember { mutableStateOf(Period.YESTERDAY) }

        val context = LocalContext.current

        val launcher =
                rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                                if (!isGranted) {
                                        android.widget.Toast.makeText(
                                                        context,
                                                        "알림 권한이 거부되었습니다.\n알림 탭에서 변경 가능합니다.",
                                                        android.widget.Toast.LENGTH_LONG
                                                )
                                                .show()
                                }
                        }
                )

        LaunchedEffect(Unit) {
                // 권한이 없으면 요청 (Android 13+) - 알림을 직접 보내진 않음
                if (!PermissionUtils.hasNotificationPermission(context)) {
                        if (android.os.Build.VERSION.SDK_INT >=
                                        android.os.Build.VERSION_CODES.TIRAMISU
                        ) {
                                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                }
        }

        // 초기 팝업 표시 (Daily Insight) - 하루에 한 번만
        val prefs = remember {
                context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        }
        val today = remember {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Date())
        }
        val lastPopupDate = remember { prefs.getString("last_popup_date", "") }
        var showInsightPopup by remember { mutableStateOf(today != lastPopupDate) }

        val tabs =
                listOf(
                        BottomTab(HomeTab.ANALYSIS, "분석", Icons.Filled.BarChart),
                        BottomTab(HomeTab.PREDICTION, "예측", Icons.Filled.ShowChart),
                        BottomTab(HomeTab.NOTIFICATION, "알림", Icons.Filled.Notifications),
                )

        // 데이터 리프레시 트리거 (버전)
        var refreshTrigger by remember { mutableStateOf(0) }

        val currentTab = activeTab
        val onTabSelected: (HomeTab) -> Unit = { activeTab = it }

        Scaffold(
                containerColor = BackgroundBeige,
                bottomBar = {
                        BottomTabBar(
                                tabs = tabs,
                                activeTab = currentTab,
                                onTabSelected = onTabSelected
                        )
                }
        ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Column(modifier = Modifier.fillMaxSize()) {
                                // 상단 기간 선택
                                PeriodSelector(
                                        selectedPeriod = selectedPeriod,
                                        onPeriodSelected = { selectedPeriod = it },
                                        isEnabled =
                                                activeTab != HomeTab.PREDICTION &&
                                                        activeTab != HomeTab.NOTIFICATION,
                                        onLogout = onLogout
                                )

                                // 콘텐츠 영역
                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .verticalScroll(rememberScrollState())
                                                        .padding(bottom = Spacing.L)
                                ) {
                                        when (activeTab) {
                                                HomeTab.ANALYSIS ->
                                                        AnalysisTab(
                                                                period = selectedPeriod,
                                                                refreshTrigger = refreshTrigger
                                                        )
                                                HomeTab.PREDICTION ->
                                                        PredictionTab(period = selectedPeriod)
                                                HomeTab.NOTIFICATION ->
                                                        NotificationTab(period = selectedPeriod)
                                        }
                                }
                        }
                }
                // 팝업 표시
                if (showInsightPopup) {
                        DailyInsightPopup(
                                onClose = {
                                        showInsightPopup = false
                                        // 오늘 날짜 저장 (내일까지 팝업 안 보이게)
                                        prefs.edit().putString("last_popup_date", today).apply()
                                }
                        )
                }
        }
}

/** 상단 기간 선택 컴포넌트 */
@Composable
private fun PeriodSelector(
        selectedPeriod: Period,
        onPeriodSelected: (Period) -> Unit,
        isEnabled: Boolean,
        onLogout: () -> Unit
) {
        val periods =
                listOf(
                        Period.YESTERDAY to "어제",
                        Period.WEEK to "일주일",
                        Period.TWO_WEEKS to "2주일",
                        Period.MONTH to "한달"
                )

        var showMenu by remember { mutableStateOf(false) }

        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(SurfaceWhite)
                                .padding(horizontal = Spacing.ScreenPadding, vertical = Spacing.M)
        ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Row(
                                modifier =
                                        Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.ItemGap)
                        ) {
                                periods.forEach { (id, label) ->
                                        val isSelected = selectedPeriod == id
                                        Button(
                                                onClick = { if (isEnabled) onPeriodSelected(id) },
                                                enabled = isEnabled,
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        if (isSelected) AccentBlue
                                                                        else BackgroundBeige,
                                                                disabledContainerColor =
                                                                        if (isSelected) AccentBlue
                                                                        else BackgroundBeige,
                                                                disabledContentColor = PrimaryBrown
                                                        ),
                                                contentPadding =
                                                        PaddingValues(
                                                                horizontal = Spacing.L,
                                                                vertical = Spacing.S
                                                        ),
                                                modifier =
                                                        Modifier.alpha(if (isEnabled) 1f else 0.5f),
                                                shape = RoundedCornerShape(Spacing.M)
                                        ) {
                                                Text(
                                                        text = label,
                                                        color = PrimaryBrown,
                                                        fontSize = FontSizes.Normal,
                                                )
                                        }
                                }
                        }

                        // 더보기 버튼 및 드롭다운 메뉴
                        Box {
                                androidx.compose.material3.IconButton(
                                        onClick = { showMenu = true }
                                ) {
                                        Icon(
                                                imageVector = Icons.Filled.MoreVert,
                                                contentDescription = "More",
                                                tint = PrimaryBrown
                                        )
                                }
                                androidx.compose.material3.DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier.background(SurfaceWhite)
                                ) {
                                        androidx.compose.material3.DropdownMenuItem(
                                                text = {
                                                        Text(
                                                                text = "🚪 로그아웃",
                                                                fontSize = FontSizes.Normal,
                                                                color = PrimaryBrown
                                                        )
                                                },
                                                onClick = {
                                                        showMenu = false
                                                        onLogout()
                                                }
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
                                .padding(vertical = Spacing.S),
                horizontalArrangement = Arrangement.SpaceAround
        ) {
                tabs.forEach { tab ->
                        val isActive = activeTab == tab.id

                        Box(
                                modifier =
                                        Modifier.weight(1f) // 탭 4개면 4등분
                                                .height(56.dp) // 터치 영역 높이 확보
                                                .clickable { onTabSelected(tab.id) } // 박스 전체 클릭
                                                .padding(vertical = Spacing.XS),
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
