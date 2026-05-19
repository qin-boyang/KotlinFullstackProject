package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.viewmodel.LoginViewModel
import androidx.compose.ui.tooling.preview.Preview
import org.example.project.ui.LoginUI
import org.example.project.ui.ProfileUI

@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = viewModel { LoginViewModel() }

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginUI(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("profile") {
                    ProfileUI(
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("profile") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
