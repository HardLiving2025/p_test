package com.example.emotionapp.ui.components.request

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.emotionapp.ui.theme.*
import com.example.emotionapp.utils.PermissionUtils

@Composable
fun UsagePermissionSection(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    // Check permission on resume (needs lifecycle observer in parent or here, but simplify for now
    // by checking on button click or composition)
    // Actually, user might come back from settings.
    // Parent RequestScreen should probably handle "onResume" checks, but section can also check.

    // Simple UI:
    // Title: 앱 사용 통계 권한이 필요합니다.
    // Description: 사용 시간을 분석하려면 권한 허용이 필요해요.
    // Button: 권한 설정하러 가기

    Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.L),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = "앱 사용 기록 접근 권한",
                fontSize = FontSizes.Title,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
        )
        Spacer(modifier = Modifier.height(Spacing.L))
        Text(
                text = "Screen Comma는 사용자의 앱 사용 패턴을 분석합니다.\n정확한 분석을 위해\n사용 정보 접근 권한을 허용해주세요",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.XXL))
        Button(
                onClick = { PermissionUtils.openUsageAccessSettings(context) },
                shape = RoundedCornerShape(Spacing.M),
                modifier = Modifier.fillMaxWidth(0.8f)
        ) { Text(text = "권한 허용하러 가기", fontSize = FontSizes.Normal) }

        Spacer(modifier = Modifier.height(Spacing.L))

        // Button to check if granted (or auto check in parent)
        // Let's add a "Next" button that is enabled only if granted, or just checks and proceeds.
        Button(
                onClick = {
                    if (PermissionUtils.hasUsageStatsPermission(context)) {
                        onPermissionGranted()
                    } else {
                        // Toast or verify again
                        android.widget.Toast.makeText(
                                        context,
                                        "아직 권한이 허용되지 않았습니다.",
                                        android.widget.Toast.LENGTH_SHORT
                                )
                                .show()
                    }
                },
                shape = RoundedCornerShape(Spacing.M),
                modifier = Modifier.fillMaxWidth(0.8f)
        ) { Text(text = "다음으로", fontSize = FontSizes.Normal) }
    }
}
