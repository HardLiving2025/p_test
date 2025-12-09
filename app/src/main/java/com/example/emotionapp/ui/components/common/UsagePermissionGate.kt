package com.example.emotionapp.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.emotionapp.data.hasUsageStatsPermission
import com.example.emotionapp.data.openUsageAccessSettings

@Composable
fun UsagePermissionGate(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(hasUsageStatsPermission(context)) }

    if (hasPermission) {
        // ✅ 권한 있으면 원래 앱 콘텐츠 보여주기
        content()
    } else {
        // ⚠️ 권한 없으면 안내 화면
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("스크린 Comma가 사용 기록에 접근해야\n정확한 사용 패턴 분석이 가능해요.")
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    openUsageAccessSettings(context)
                    // 사용자가 돌아와서 다시 컴포즈될 때 직접 다시 체크해도 됨
                    hasPermission = hasUsageStatsPermission(context)
                }) {
                    Text("사용량 접근 권한 설정하러 가기")
                }
            }
        }
    }
}
