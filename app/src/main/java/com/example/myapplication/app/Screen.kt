package com.example.myapplication.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R

sealed class Screen(
    val route: String,
    val label: String,
    val icon: @Composable (Color) -> Unit,
    val selectedColor: Color,
    val unselectedColor: Color
) {
    object LoginScreen : Screen("login_screen", "Login", { color -> Icon(Icons.Default.Person, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object RegisterScreen : Screen("register_screen", "Register", { color -> Icon(Icons.Default.Person, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object HomeScreen : Screen("home_screen", "Home", { color -> Icon(Icons.Default.Home, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)

    object SearchMusicScreen : Screen("searchMusic_screen", "Search", { color -> Icon(Icons.Default.Search, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object ProfileScreen : Screen("profile_screen", "Profile", { color -> Icon(Icons.Default.Person, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object DetailsCategoryScreen : Screen("detailsCategory_screen", "Details", { color -> Icon(Icons.Default.Home, contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object MusicScreen : Screen("music_screen", "Music", { color -> Icon(painter = painterResource(id = R.drawable.baseline_music_note_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object PodcastScreen : Screen("podcast_screen", "Podcast", { color -> Icon(painter = painterResource(id = R.drawable.podcast), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object AddPlayListScreen : Screen("addPlayListScreen_screen", "Add Playlist", { color -> Icon(painterResource(id = R.drawable.baseline_music_note_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object PlayListMusicScreen : Screen("playListMusic_screen", "Playlist Music", { color -> Icon(painterResource(id = R.drawable.baseline_music_note_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object FavoriteScreen : Screen("favorite_screen", "Favorites", { color -> Icon(painterResource(id = R.drawable.baseline_music_note_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object ListPodcastScreen : Screen("listpodcast_screen", "Podcasts", { color -> Icon(painterResource(id = R.drawable.baseline_music_note_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)

    object RankerMusicScreen : Screen("rankerMusic_screen", "#Chart", { color -> Icon(painterResource(id = R.drawable.baseline_multiline_chart_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object ShareMusicScreen : Screen("shareMusic_screen", "#Share", { color -> Icon(painterResource(id = R.drawable.baseline_all_inclusive_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)

    object RandomMusicScreen : Screen("randomMusic_screen", "#Music", { color -> Icon(painterResource(id = R.drawable.baseline_all_inclusive_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
    object NewMusicScreen : Screen("newMusic_screen", "#Music", { color -> Icon(painterResource(id = R.drawable.baseline_all_inclusive_24), contentDescription = null, tint = color) }, selectedColor = Color(150,205,50), unselectedColor = Color.White)
}

