package com.almagribii.goalmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.almagribii.goalmate.core.navigation.Screen
import com.almagribii.goalmate.feature.auth.LoginScreen
import com.almagribii.goalmate.feature.auth.LoginViewModel
import com.almagribii.goalmate.feature.dashboard.DashboardScreen
import com.almagribii.goalmate.ui.theme.GoalmateTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoalmateTheme {
                val navController = rememberNavController()
                val currentUser by loginViewModel.currentUser.collectAsState(initial = null)

                // Listener Real-time State untuk Auto-Login dan Auto-Logout
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        // Jika user terdeteksi sudah masuk, navigasikan ke Dashboard dan bersihkan stack Login
                        navController.navigate(Screen.Dashboard) {
                            popUpTo(Screen.Login) { inclusive = true }
                        }
                    } else {
                        // Jika user logout / session null, kembalikan ke Login Screen
                        navController.navigate(Screen.Login) {
                            popUpTo(Screen.Dashboard) { inclusive = true }
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login
                    ) {
                        // Halaman Login
                        composable<Screen.Login> {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    navController.navigate(Screen.Dashboard) {
                                        popUpTo(Screen.Login) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Halaman Dashboard
                        composable<Screen.Dashboard> {
                            DashboardScreen(
                                onLogoutClick = {
                                    loginViewModel.signOut()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}