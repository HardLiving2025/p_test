package com.example.emotionapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.emotionapp.R
import com.example.emotionapp.data.local.TokenManager
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
        onNavigateToHome: () -> Unit // Note: currently unused but kept for interface compatibility
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // 항상 LOGIN 단계부터 시작 (토큰 체크 제거)
    var currentStep by rememberSaveable { mutableStateOf(RequestStep.LOGIN) }

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

        val density = LocalDensity.current

        AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val duration = 1100 // 1.1s duration
                    val offsetPx = with(density) { 20.dp.roundToPx() }

                    if (targetState.ordinal > initialState.ordinal) {
                        // Enter: From Right
                        (fadeIn(tween(duration)) +
                                        slideInHorizontally(tween(duration)) { offsetPx })
                                .togetherWith(
                                        // Exit: To Left
                                        fadeOut(tween(duration)) +
                                                slideOutHorizontally(tween(duration)) { -offsetPx }
                                )
                    } else {
                        // Enter: From Left (for backward navigation)
                        (fadeIn(tween(duration)) +
                                        slideInHorizontally(tween(duration)) { -offsetPx })
                                .togetherWith(
                                        // Exit: To Right
                                        fadeOut(tween(duration)) +
                                                slideOutHorizontally(tween(duration)) { offsetPx }
                                )
                    }
                },
                label = "RequestStepTransition"
        ) { targetStep ->
            when (targetStep) {
                RequestStep.LOGIN -> {
                    LoginSection(onLoginSuccess = { currentStep = RequestStep.USAGE_PERMISSION })
                }
                RequestStep.USAGE_PERMISSION -> {
                    UsagePermissionSection(
                            onPermissionGranted = {
                                currentStep = RequestStep.NOTIFICATION_PERMISSION
                            }
                    )
                }
                RequestStep.NOTIFICATION_PERMISSION -> {
                    NotificationPermissionSection(
                            onPermissionGranted = { currentStep = RequestStep.DATA_PULL },
                            onSkip = { currentStep = RequestStep.DATA_PULL }
                    )
                }
                RequestStep.DATA_PULL -> {
                    DataPullSection(
                            onComplete = {
                                // 데이터 수집 완료 후 세션 체크
                                val currentSlot = TokenManager.calculateCurrentSlotTime()
                                val lastSlot = tokenManager.getLastInputSlot()

                                if (currentSlot == lastSlot) {
                                    // 이미 이번 세션에 입력했으면 Home으로
                                    onNavigateToHome()
                                } else {
                                    // 입력 안 했으면 감정/상태 입력 화면으로
                                    onNavigateToMood()
                                }
                            }
                    )
                }
            }
        }
    }
}
