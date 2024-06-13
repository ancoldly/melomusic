package com.example.myapplication.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.Playlist
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AddPlayListScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToProfilePage: () -> Unit,
    categoryViewModel: CategoryViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel(),
    podcastViewModel: PodcastViewModel = viewModel(),
    playlistViewModel: PlaylistViewModel = viewModel()
) {
    val context = LocalContext.current

    val categories by categoryViewModel.categories.collectAsState(initial = emptyList())
    val musics by musicViewModel.musics.collectAsState(initial = emptyList())
    val podcasts by podcastViewModel.podcasts.collectAsState(initial = emptyList())

    var playlistName by remember { mutableStateOf("My playlists #...") }

    var selectedSongs by remember { mutableStateOf<List<String>>(emptyList()) }

    Surface(
        color = Color(28, 28, 28)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = { onNavToProfilePage.invoke() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Home",
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Add playlist",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Divider(color = Color(130,130,130), thickness = 1.dp)

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = "Name your music playlist?",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.size(20.dp))

            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(50, 205, 50),
                    unfocusedBorderColor = Color.White,
                ),
                textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White)
            )

            Spacer(modifier = Modifier.size(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.buttonColors(Color.Transparent),
                    modifier = Modifier.background(Color.Transparent),
                    onClick = {
                        onNavToProfilePage.invoke()
                    }
                ) {
                    Text(text = "Cancel")
                }

                Spacer(modifier = Modifier.size(20.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(Color(50, 205, 50)),
                    modifier = Modifier.background(Color.Transparent),
                    onClick = {
                        val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                        val playlistId = UUID.randomUUID().toString()
                        val createdDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val playlist = Playlist(playlistId, playlistName, userId, createdDate, selectedSongs)
                        playlistViewModel.addPlaylist(playlist)
                        onNavToProfilePage.invoke()
                    }
                ) {
                    Text(
                        text = "Create",
                        color = Color.Black
                    )
                }
            }
        }
    }
}
