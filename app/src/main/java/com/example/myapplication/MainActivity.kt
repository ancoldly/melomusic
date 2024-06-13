package com.example.myapplication

import BottomNavigationBar
import MusicService
import Navigation
import PodcastService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.app.Screen
import com.example.myapplication.app.ScreenAdmin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            val service = MusicService()
            val servicePodcast = PodcastService()
            MainScreen(loginViewModel, service, servicePodcast)
        }
    }
}

@Composable
fun MainScreen(loginViewModel: LoginViewModel, service: MusicService, servicePodcast: PodcastService) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithoutBottomBar = listOf(
        Screen.LoginScreen.route,
        Screen.RegisterScreen.route,
        ScreenAdmin.AdminHomeScreen.route,
        ScreenAdmin.AdminAddMusicScreen.route,
        ScreenAdmin.AdminAddCategoryScreen.route,
        ScreenAdmin.AdminAddPodcastScreen.route,
        ScreenAdmin.AdminMusicScreen.route,
        ScreenAdmin.AdminCategoryScreen.route,
        ScreenAdmin.AdminPodcastScreen.route,
        ScreenAdmin.AdminUserScreen.route,
        ScreenAdmin.AdminEditCategoryScreen.route,
        ScreenAdmin.AdminEditMusicScreen.route,
        ScreenAdmin.AdminEditPodcastScreen.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute !in screensWithoutBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        Navigation(navController = navController, loginViewModel = loginViewModel, modifier = Modifier.padding(innerPadding), service = service, servicePodcast = servicePodcast)
    }
}
