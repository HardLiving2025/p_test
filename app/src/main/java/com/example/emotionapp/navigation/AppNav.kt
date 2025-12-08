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

    NavHost(
        navController = navController,
        startDestination = "mood"
    ) {
        composable("mood") {
            MoodSelector(onNext = { navController.navigate("state") })
        }

        composable("state") {
            StateSelector(onNext = { navController.navigate("home") })
        }

        composable("home") {
            HomeScreen()
        }
    }
}
