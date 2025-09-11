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
import com.sepulveda.minutanutricional.accessibility.TtsHelper
import com.sepulveda.minutanutricional.ui.screens.ForgotPasswordScreen
import com.sepulveda.minutanutricional.ui.screens.LoginScreen
import com.sepulveda.minutanutricional.ui.screens.RegisterScreen
import com.sepulveda.minutanutricional.ui.screens.WeeklyMenuScreen
import com.sepulveda.minutanutricional.ui.theme.MinutaNutricionalTheme

class MainActivity : ComponentActivity() {

    private lateinit var tts: TtsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        tts = TtsHelper(this)

        setContent {
            MinutaNutricionalTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable(route = "login") {
                            LoginScreen(
                                tts = tts,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable(route = "register") {
                            RegisterScreen(
                                tts = tts,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(route = "forgot") {
                            ForgotPasswordScreen(
                                tts = tts,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable(route = "weekly") {
                            WeeklyMenuScreen(
                                tts = tts,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tts.isInitialized) tts.shutdown()
    }
}
