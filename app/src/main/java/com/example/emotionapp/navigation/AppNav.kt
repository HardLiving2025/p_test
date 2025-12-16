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

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            com.example.emotionapp.ui.screens.OnboardingScreen(
                    onComplete = { navController.navigate("login") }
            )
        }

        composable("login") {
            com.example.emotionapp.ui.screens.LoginScreen(
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

        composable("home") { HomeScreen() }
    }
}
