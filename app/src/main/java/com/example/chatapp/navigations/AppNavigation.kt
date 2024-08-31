package com.example.chatapp.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.models.Database
import com.example.chatapp.screens.AdminPanelScreen
import com.example.chatapp.screens.HomeScreen
import com.example.chatapp.screens.LoginScreen
import com.example.chatapp.screens.WelcomeScreen

@Composable
fun AppNavigation(database : Database){


    val navController = rememberNavController()

    // Uygulamanın navigasyon yapısını tanımlar
    NavHost(navController = navController, startDestination = Routes.screenWelcome, builder = {
        composable(Routes.screenWelcome){
            WelcomeScreen(navController)
        }
        composable(Routes.screenLogin)
        {
            LoginScreen(navController, database)
        }
        composable(Routes.screenHome){
            HomeScreen(navController, database)
        }
        composable(Routes.screenAdminPanel){
            AdminPanelScreen(navController, database)
        }
    })
}