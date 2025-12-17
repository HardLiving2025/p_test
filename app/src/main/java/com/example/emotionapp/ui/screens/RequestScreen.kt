package com.example.emotionapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.emotionapp.R
import com.example.emotionapp.ui.components.request.DataPullSection
import com.example.emotionapp.ui.components.request.LoginSection
import com.example.emotionapp.ui.components.request.NotificationPermissionSection
import com.example.emotionapp.ui.components.request.UsagePermissionSection
import com.example.emotionapp.ui.theme.OnboardingBackground
import com.example.emotionapp.ui.theme.PrimaryBrown

enum class RequestStep {
    LOGIN,
    USAGE_PERMISSION,
    NOTIFICATION_PERMISSION,
    DATA_PULL
}

@Composable
fun RequestScreen(
        onNavigateToMood: () -> Unit,
        onNavigateToHome:
                () -> Unit // Used if valid token exists and we skip login? logic handles token
        // check inside sections or LoginSection?
        // Actually LoginScreen checked token manager.
        // If token exists, we might skip Login.
        // The previous LoginScreen logic checked Destination.
        // We should replicate that or rely on AppNav to direct us.
        // For now, let's assume we start at LOGIN step if we land here.
        ) {
    var currentStep by remember { mutableStateOf(RequestStep.LOGIN) }

    Column(
            modifier = Modifier.fillMaxSize().background(OnboardingBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
                modifier =
                        Modifier.size(128.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(PrimaryBrown)
                                .padding(bottom = 0.dp),
                contentAlignment = Alignment.Center
        ) {
            Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "App Icon",
                    modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        when (currentStep) {
            RequestStep.LOGIN -> {
                LoginSection(onLoginSuccess = { currentStep = RequestStep.USAGE_PERMISSION })
            }
            RequestStep.USAGE_PERMISSION -> {
                UsagePermissionSection(
                        onPermissionGranted = { currentStep = RequestStep.NOTIFICATION_PERMISSION }
                )
            }
            RequestStep.NOTIFICATION_PERMISSION -> {
                NotificationPermissionSection(
                        onPermissionGranted = { currentStep = RequestStep.DATA_PULL },
                        onSkip = { currentStep = RequestStep.DATA_PULL }
                )
            }
            RequestStep.DATA_PULL -> {
                DataPullSection(onComplete = { onNavigateToMood() })
            }
        }
    }
}
