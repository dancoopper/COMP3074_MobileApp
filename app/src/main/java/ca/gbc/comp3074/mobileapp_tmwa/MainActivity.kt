package ca.gbc.comp3074.mobileapp_tmwa

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.gbc.comp3074.mobileapp_tmwa.domain.model.Screen
import ca.gbc.comp3074.mobileapp_tmwa.screens.ProfileScreen
import ca.gbc.comp3074.mobileapp_tmwa.screens.HomeScreen
import ca.gbc.comp3074.mobileapp_tmwa.screens.LoginScreen
import ca.gbc.comp3074.mobileapp_tmwa.screens.RegisterScreen
import com.example.compose.AppTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding() / 2)
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.HOME.route) {
        composable(Screen.HOME.route) {
            HomeScreen(
                onProfileClick = {
                    navController.navigate(Screen.PROFILE.route)
                }
            )
        }
        composable(Screen.LOGIN.route) {
            LoginScreen(
                onLoginDone = { navController.navigate(Screen.HOME.route) },
                onNavigateToRegister = { navController.navigate(Screen.REGISTER.route) }
            )
        }
        composable(Screen.REGISTER.route) {
            RegisterScreen(
                onRegisterDone = { navController.navigate(Screen.LOGIN.route) },
            )
        }
        composable(Screen.PROFILE.route) {
            ProfileScreen(
                onNavigateHome = { navController.popBackStack(Screen.HOME.route, inclusive = false) }
            )
        }
    }
}
