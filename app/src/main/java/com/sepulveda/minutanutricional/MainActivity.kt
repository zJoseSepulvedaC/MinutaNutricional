package com.sepulveda.minutanutricional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sepulveda.minutanutricional.ui.screens.ForgotPasswordScreen
import com.sepulveda.minutanutricional.ui.screens.LoginScreen
import com.sepulveda.minutanutricional.ui.screens.RegisterScreen
import com.sepulveda.minutanutricional.ui.screens.WeeklyMenuScreen
import com.sepulveda.minutanutricional.ui.theme.MinutaNutricionalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinutaNutricionalTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen { route -> navController.navigate(route) }
                        }
                        composable("register") {
                            RegisterScreen { navController.popBackStack() }
                        }
                        composable("forgot") {
                            ForgotPasswordScreen { navController.popBackStack() }
                        }
                        composable("weekly") {
                            WeeklyMenuScreen { navController.popBackStack() }
                        }
                    }
                }
            }
        }
    }
}
