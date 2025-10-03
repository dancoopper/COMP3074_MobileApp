package ca.gbc.comp3074.mobileapp_tmwa

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.gbc.comp3074.mobileapp_tmwa.components.EventForm
import ca.gbc.comp3074.mobileapp_tmwa.screens.HomePage
import ca.gbc.comp3074.mobileapp_tmwa.screens.Login
import ca.gbc.comp3074.mobileapp_tmwa.screens.RegisterScreen
import com.example.compose.AppTheme


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object About : Screen("about")
    object Contact : Screen("contact")
    object Login : Screen("login")
    object Register : Screen("register")
    object EventForm : Screen("eventform")
}

val listNavItems = listOf<Screen>(
    Screen.Home,
    Screen.Contact,
    Screen.About,
    Screen.Login,
    Screen.Register,
    Screen.EventForm
)


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier.padding(top=innerPadding.calculateTopPadding()/2)
                    ) {
//                        Spacer(
//                            modifier = Modifier.height( innerPadding)
//                        )
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
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Home.route) {
            HomePage(
                onBack = {navController.popBackStack()},
                onNewEvent = {navController.navigate(Screen.EventForm.route)}
            )
        }
        composable(Screen.EventForm.route){
            EventForm()
        }
        composable(Screen.About.route) { }
        composable(Screen.Contact.route) { }
        composable(Screen.Login.route) {
            Login(
                onLoginDone = { navController.navigate(Screen.Home.route) },
                onNavigateToRegister = {navController.navigate(Screen.Register.route)}
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterDone = { navController.navigate(Screen.Login.route) },

                )
        }
    }
}
