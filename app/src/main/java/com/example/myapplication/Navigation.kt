import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.app.Screen
import com.example.myapplication.login.LoginViewModel
import androidx.compose.ui.Modifier
import com.example.myapplication.app.ScreenAdmin
import com.example.myapplication.screens.DetailSCategoryScreen
import com.example.myapplication.screens.LoginScreen
import com.example.myapplication.screens.ProfileScreen
import com.example.myapplication.screens.RegisterScreen
import com.example.myapplication.admin.AdminHomeScreen
import com.example.myapplication.admin.AdminCategoryScreen
import com.example.myapplication.admin.AdminAddCategoryScreen
import com.example.myapplication.admin.AdminAddMusicScreen
import com.example.myapplication.admin.AdminAddPodcastScreen
import com.example.myapplication.admin.AdminEditCategoryScreen
import com.example.myapplication.admin.AdminEditMusicScreen
import com.example.myapplication.admin.AdminEditPodcastScreen
import com.example.myapplication.admin.AdminMusicScreen
import com.example.myapplication.admin.AdminPodcastScreen
import com.example.myapplication.admin.AdminUserScreen
import com.example.myapplication.data.Music
import com.example.myapplication.screens.AddPlayListScreen
import com.example.myapplication.screens.FavoriteScreen
import com.example.myapplication.screens.ListPodcast
import com.example.myapplication.screens.MusicScreen
import com.example.myapplication.screens.PlayListMusicScreen
import com.example.myapplication.screens.PodcastScreen
import com.example.myapplication.screens.RankerMusicScreen
import com.example.myapplication.screens.SearchMusicScreen
import com.example.myapplication.screens.ShareMusicScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    service: MusicService,
    servicePodcast: PodcastService,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(Screen.HomeScreen.route) {
                        launchSingleTop = true
                        popUpTo(Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                onNavToAdminHomePage = {
                     navController.navigate(ScreenAdmin.AdminHomeScreen.route) {
                         launchSingleTop = true
                         popUpTo(Screen.LoginScreen.route) {
                             inclusive = true
                         }
                     }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(Screen.RegisterScreen.route) {
                    launchSingleTop = true
                    popUpTo(Screen.RegisterScreen.route) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(
                onNavToHomePage = {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.RegisterScreen.route) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(Screen.LoginScreen.route)
            }
        }

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                onNavToProfilePage = {
                    navController.navigate(Screen.ProfileScreen.route)
                },
                onNavToLoginPage = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                onNavToDetailsCategoryPage = { categoryId ->
                    navController.navigate(Screen.DetailsCategoryScreen.route + "/$categoryId")
                },
                onNavToAdminHomePage =  {
                    navController.navigate(ScreenAdmin.AdminHomeScreen.route)
                },
                onNavToMusicPage = {
                    navController.navigate(Screen.MusicScreen.route)
                },
                onNavToPodcastPage = {
                    navController.navigate(Screen.PodcastScreen.route)
                },
                loginViewModel = loginViewModel,
                onNavSearchMusicPage = {
                   navController.navigate(Screen.SearchMusicScreen.route)
                },
                onNavToRankerMusicPage = {
                    navController.navigate(Screen.RankerMusicScreen.route)
                },
                onNavToShareMusicPage = {
                    navController.navigate(Screen.ShareMusicScreen.route)
                },
                onNavToHomePage = {
                    navController.navigate(Screen.HomeScreen.route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                podcastService = servicePodcast,
                musicService = service
            )
        }

        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen(
                onNavToProfilePage = {

                },
                onNavToLoginPage = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                onNavToHomePage = {
                    navController.navigateUp()
                },
                onNavToAddPlayListPage = {
                     navController.navigate(Screen.AddPlayListScreen.route)
                },
                onNavToPlayListMusicPage = { playlistId ->
                    navController.navigate(Screen.PlayListMusicScreen.route + "/$playlistId")
                },
                onNavToFavoritePage = {
                    navController.navigate(Screen.FavoriteScreen.route)
                },
                onNavToListPodcastPage = {
                    navController.navigate(Screen.ListPodcastScreen.route)
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.DetailsCategoryScreen.route + "/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            DetailSCategoryScreen(
                categoryId = categoryId,
                onNavToHomePage = {
                    navController.navigateUp()
                },
                onNavToDetailsCategoryPage = {

                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = ScreenAdmin.AdminHomeScreen.route) {
            AdminHomeScreen(
                onNavToHomePage = {
                    navController.navigate(Screen.HomeScreen.route)
                },
                onNavToLoginPage = {
                    navController.navigate(Screen.LoginScreen.route)
                },
                onNavToAdminHomePage = {

                },
                onNavToAdminCategoryPage = {
                    navController.navigate(ScreenAdmin.AdminCategoryScreen.route)
                },
                onNavToAdminUserPage = {
                    navController.navigate(ScreenAdmin.AdminUserScreen.route)        
                },
                onNavToAdminMusicPage = {
                    navController.navigate(ScreenAdmin.AdminMusicScreen.route)
                },
                onNavToAdminPodcastPage = {
                    navController.navigate(ScreenAdmin.AdminPodcastScreen.route)
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminCategoryScreen.route) {
            AdminCategoryScreen(
                onNavToAdminHomePage = {
                    navController.navigateUp()
                },
                onNavToAddCategoryHomePage = {
                    navController.navigate(ScreenAdmin.AdminAddCategoryScreen.route)
                },
                onNavToEditCategoryHomePage = { categoryId ->
                    navController.navigate(ScreenAdmin.AdminEditCategoryScreen.route + "/$categoryId")
                },
                    loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminAddCategoryScreen.route) {
            AdminAddCategoryScreen(
                onNavToAdminHomePage = {
                    navController.navigate(ScreenAdmin.AdminHomeScreen.route)
                },
                onNavToAdminCategoryPage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminAddMusicScreen.route) {
            AdminAddMusicScreen(
                onNavToAdminHomePage = {
                    navController.navigate(ScreenAdmin.AdminHomeScreen.route)
                },
                onNavToAdminMusicPage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminAddPodcastScreen.route) {
            AdminAddPodcastScreen(
                onNavToAdminHomePage = {
                    navController.navigate(ScreenAdmin.AdminHomeScreen.route)
                },
                onNavToAdminPodcastPage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminEditCategoryScreen.route + "/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            AdminEditCategoryScreen(
                onNavToAdminCategoryPage = {
                    navController.navigateUp()
                },
                categoryId = categoryId,
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminUserScreen.route) {
            AdminUserScreen(
                onNavToAdminHomePage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminMusicScreen.route) {
            AdminMusicScreen(
                onNavToAdminHomePage = {
                    navController.navigateUp()
                },
                onNavToEditMusicHomePage = { musicId ->
                    navController.navigate(ScreenAdmin.AdminEditMusicScreen.route + "/$musicId")
                },
                onNavToAddMusicHomePage = {
                    navController.navigate(ScreenAdmin.AdminAddMusicScreen.route)
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminEditMusicScreen.route + "/{musicId}") { backStackEntry ->
            val musicId = backStackEntry.arguments?.getString("musicId") ?: ""
            AdminEditMusicScreen(
                onNavToAdminMusicPage = {
                    navController.navigateUp()
                },
                musicId = musicId,
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminPodcastScreen.route) {
            AdminPodcastScreen(
                onNavToAdminHomePage = {
                    navController.navigateUp()
                },
                onNavToEditPodcastHomePage = { podcastId ->
                    navController.navigate(ScreenAdmin.AdminEditPodcastScreen.route + "/$podcastId")
                },
                onNavToAddPodcastHomePage = {
                    navController.navigate(ScreenAdmin.AdminAddPodcastScreen.route)
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = ScreenAdmin.AdminEditPodcastScreen.route + "/{podcastId}") { backStackEntry ->
            val podcastId = backStackEntry.arguments?.getString("podcastId") ?: ""
            AdminEditPodcastScreen(
                onNavToAdminPodcastPage = {
                    navController.navigateUp()
                },
                podcastId = podcastId,
                loginViewModel = loginViewModel
            )
        }

        composable(route = Screen.MusicScreen.route) {
            MusicScreen(
                onNavToHomePage = {
                   navController.navigateUp()
                },
                onNavSearchMusicPage = {
                   navController.navigate(Screen.SearchMusicScreen.route)
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.PodcastScreen.route) {
            PodcastScreen(
                onNavToHomePage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.AddPlayListScreen.route) {
            AddPlayListScreen(
                onNavToProfilePage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel
            )
        }

        composable(route = Screen.PlayListMusicScreen.route + "/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
            PlayListMusicScreen(
                onNavToProfilePage = {
                    navController.navigateUp()
                },
                playlistId = playlistId,
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.FavoriteScreen.route) {
            FavoriteScreen(
                onNavToProfilePage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.ListPodcastScreen.route) {
            ListPodcast(
                onNavToProfilePage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.SearchMusicScreen.route) {
            SearchMusicScreen(
                onNavToMusicPage = {
                    navController.navigateUp()
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.RankerMusicScreen.route) {
            RankerMusicScreen(
                onNavToHomePage = {
                    navController.navigateUp()
                },
                onNavSearchMusicPage = {
                    navController.navigate(Screen.SearchMusicScreen.route)
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }

        composable(route = Screen.ShareMusicScreen.route) {
            ShareMusicScreen(
                onNavToHomePage = {
                    navController.navigateUp()
                },
                onNavSearchMusicPage = {
                    navController.navigate(Screen.SearchMusicScreen.route)
                },
                loginViewModel = loginViewModel,
                musicService = service,
                podcastService = servicePodcast
            )
        }
    }
}
