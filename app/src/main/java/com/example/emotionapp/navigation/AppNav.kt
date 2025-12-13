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

    NavHost(navController = navController, startDestination = "mood") {
        composable("mood") {
            MoodSelector(onNext = { mood -> navController.navigate("state/$mood") })
        }

        composable(
                route = "state/{mood}",
        ) { backStackEntry ->
            val mood = backStackEntry.arguments?.getString("mood") ?: "normal"
            StateSelector(mood = mood, onNext = { navController.navigate("home") })
        }

        composable("home") { HomeScreen() }
    }
}
