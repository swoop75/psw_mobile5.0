package com.psw.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.psw.mobile.screens.LoginScreen
import com.psw.mobile.screens.DashboardScreen
import com.psw.mobile.screens.MasterlistScreen
import com.psw.mobile.screens.NewCompaniesScreen
import com.psw.mobile.ui.theme.PSWMobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PSWMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PSWApp()
                }
            }
        }
    }
}

@Composable
fun PSWApp() {
    val navController = rememberNavController()
    var isLoggedIn by remember { mutableStateOf(false) }
    
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "dashboard" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    isLoggedIn = true
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                onNavigateToMasterlist = {
                    navController.navigate("masterlist")
                },
                onNavigateToNewCompanies = {
                    navController.navigate("new_companies")
                },
                onLogout = {
                    isLoggedIn = false
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        composable("masterlist") {
            MasterlistScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("new_companies") {
            NewCompaniesScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}