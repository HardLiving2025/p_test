package com.example.emotionapp.ui.components.request

import android.os.Build
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
fun NotificationPermissionSection(onPermissionGranted: () -> Unit, onSkip: () -> Unit) {
    val context = LocalContext.current

    Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.L),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
    ) {
        Text(
                text = "알림 권한",
                fontSize = FontSizes.Title,
                fontWeight = FontWeight.Bold,
                color = PrimaryBrown
        )
        Spacer(modifier = Modifier.height(Spacing.L))
        Text(
                text = "중요한 알림과 상태 분석 리포트를 받으려면\n알림 권한이 필요합니다.\n\n알림을 허용해 주시겠습니까?",
                fontSize = FontSizes.Normal,
                color = PrimaryBrown.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Spacing.XXL))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Button(
                    onClick = {
                        // Logic to request runtime permission usually requires RequestPermission
                        // contract
                        // which must be registered in Activity/Fragment or Composable using
                        // rememberLauncherForActivityResult.
                        // Ideally, we pass a callback or handling to the parent, OR we open
                        // settings if denied.
                        // Since runtime permission request is best handled in the parent screen
                        // with `rememberLauncherForActivityResult`,
                        // we might assume the parent passes a "requestPermission" lambda.
                        // But here we can simpler open settings if needed, or rely on user to grant
                        // it.

                        // However, we want the system dialog if possible.
                        // Let's assume the user goes to settings if they click "Allow" here, OR the
                        // parent handles the actual request.
                        // For simplicity in this section component: Open Settings.
                        PermissionUtils.openNotificationSettings(context)
                    },
                    shape = RoundedCornerShape(Spacing.M),
                    modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text(text = "권한 설정하러 가기", fontSize = FontSizes.Normal) }
        } else {
            // Pre-Android 13, notification permission is granted at install, but user can turn off.
            Button(
                    onClick = { PermissionUtils.openNotificationSettings(context) },
                    shape = RoundedCornerShape(Spacing.M),
                    modifier = Modifier.fillMaxWidth(0.8f)
            ) { Text(text = "알림 설정 확인하기", fontSize = FontSizes.Normal) }
        }

        Spacer(modifier = Modifier.height(Spacing.L))

        // "Next" or "Skip"
        // Even if denied, user might proceed.
        Button(
                onClick = {
                    // If we want to enforce it: check permission.
                    if (PermissionUtils.hasNotificationPermission(context)) {
                        onPermissionGranted()
                    } else {
                        // Decide if we allow skipping. User said "알림 권한을 허용해달라는 화면" implication is
                        // it is a step.
                        // Usually notification is optional. Let's ask if they want to skip.
                        // Or just proceed.
                        // I will check status and if true call onPermissionGranted.
                        // If false, maybe show a toast "권한이 없습니다" or just proceed?
                        // I'll assume it's optional but recommended.
                        onPermissionGranted()
                    }
                },
                shape = RoundedCornerShape(Spacing.M),
                modifier = Modifier.fillMaxWidth(0.8f)
        ) { Text(text = "확인 / 다음으로", fontSize = FontSizes.Normal) }
    }
}
