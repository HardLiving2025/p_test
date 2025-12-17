package com.example.emotionapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.emotionapp.ui.screens.HomeScreen
import com.example.emotionapp.ui.screens.MoodSelector
import com.example.emotionapp.ui.screens.StateSelector

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val tokenManager = com.example.emotionapp.data.local.TokenManager(context)

    // 동적 시작 목적지: 토큰과 세션 체크
    val startDestination = tokenManager.getStartDestination()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            com.example.emotionapp.ui.screens.OnboardingScreen(
                    onComplete = { navController.navigate("login") }
            )
        }

        composable("login") {
            com.example.emotionapp.ui.screens.RequestScreen(
                    onNavigateToMood = { navController.navigate("mood") },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
            )
        }

        composable("mood") {
            MoodSelector(onNext = { mood -> navController.navigate("state/$mood") })
        }

        composable(
                route = "state/{mood}",
        ) { backStackEntry ->
            val mood = backStackEntry.arguments?.getString("mood") ?: "normal"
            StateSelector(
                    mood = mood,
                    onNext = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true } // 앱 종료 시까지 백스택 정리
                        }
                    }
            )
        }

        composable("home") {
            val context = androidx.compose.ui.platform.LocalContext.current
            val tokenManager = com.example.emotionapp.data.local.TokenManager(context)

            HomeScreen(
                    onLogout = {
                        tokenManager.clearTokens()
                        // 로그아웃 시 온보딩부터 다시 시작
                        navController.navigate("onboarding") { popUpTo(0) { inclusive = true } }
                    }
            )
        }
    }
}
