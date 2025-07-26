package com.psw.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.psw.mobile.ui.dashboard.DashboardScreen
import com.psw.mobile.ui.login.LoginScreen

@Composable
fun PSWNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onCompanyClick = { companyId ->
                    navController.navigate("company_detail/$companyId")
                }
            )
        }
        
        composable("company_detail/{companyId}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId")?.toIntOrNull() ?: 0
            // CompanyDetailScreen would go here
        }
    }
}