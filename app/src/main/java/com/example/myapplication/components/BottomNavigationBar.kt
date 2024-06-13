import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.app.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.HomeScreen,
        Screen.SearchMusicScreen,
        Screen.ShareMusicScreen,
        Screen.RankerMusicScreen,
        Screen.ProfileScreen
    )

    Box(

    ) {
        BottomNavigation(
            backgroundColor = Color(54, 54, 54),
            contentColor = Color.White
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { screen ->
                BottomNavigationItem(
                    icon = {
                        val iconColor = if (currentRoute == screen.route) Color(0, 205, 0) else Color.White
                        screen.icon(iconColor)
                    },
                    label = {
                        val labelColor = if (currentRoute == screen.route) Color(0,205,0) else Color.White
                        Text(
                            screen.label, color = labelColor,
                            fontSize = 12.sp
                        )
                    },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
