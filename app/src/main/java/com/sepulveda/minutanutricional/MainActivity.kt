package com.sepulveda.minutanutricional

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.lifecycleScope
import com.sepulveda.minutanutricional.accessibility.TtsHelper
import com.sepulveda.minutanutricional.data.UserPrefs
import com.sepulveda.minutanutricional.data.UsersRepository
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

        // Inicializar el repositorio (Room DB) y pre-popular usuarios demo la primera vez.
        // UsersRepository.init requiere un Context y un CoroutineScope (lifecycleScope aquí).
        UsersRepository.init(applicationContext, lifecycleScope)

        setContent {
            MinutaNutricionalTheme {
                val navController = rememberNavController()

                // Observa preferencia "remember session"
                val remember by UserPrefs.rememberFlow(this).collectAsState(initial = false)

                Surface(color = MaterialTheme.colorScheme.background) {
                    // Si hay sesión recordada, navega a weekly al arrancar
                    LaunchedEffect(remember) {
                        if (remember) {
                            // Evita apilar: limpia y navega
                            navController.navigate("weekly") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                tts = tts,
                                repo = UsersRepository,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                tts = tts,
                                repo = UsersRepository,
                                onRegistered = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("forgot") {
                            ForgotPasswordScreen(
                                tts = tts,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("weekly") {
                            WeeklyMenuScreen(
                                tts = tts,
                                onBack = {
                                    // Si quieres hacer "logout" desde aquí,
                                    // podrías usar UserPrefs.clearSession(this) y volver a login.
                                    navController.popBackStack()
                                }
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
