package com.example.myapplication.screens

import MusicService
import PodcastService
import android.widget.ImageButton
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.myapplication.components.DividerTextComponent
import com.example.myapplication.components.MiniPlayer
import com.example.myapplication.components.MiniPodcastPlayer
import com.example.myapplication.components.MusicDialog
import com.example.myapplication.components.PlaylistDialog
import com.example.myapplication.components.PodcastDialog
import com.example.myapplication.components.ShowToastMessage
import com.example.myapplication.data.Music
import com.example.myapplication.data.Podcast
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.ListPodcastViewModel
import com.example.myapplication.viewmodel.MiniPlayerViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.ShareMusicViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToProfilePage: () -> Unit,
    onNavToLoginPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onNavToAddPlayListPage: () -> Unit,
    onNavToPlayListMusicPage: (String) -> Unit,
    onNavToFavoritePage: () -> Unit,
    onNavToListPodcastPage: () -> Unit,
    musicViewModel: MusicViewModel = viewModel(),
    playlistViewModel: PlaylistViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    listPodcastViewModel: ListPodcastViewModel = viewModel(),
    musicService: MusicService,
    podcastService: PodcastService,
    shareMusicViewModel: ShareMusicViewModel = viewModel(),
    podcastViewModel: PodcastViewModel = viewModel(),
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.logoutError != null
    val context = LocalContext.current

    val musics by musicViewModel.musics.collectAsState()
    val playlists by playlistViewModel.playlists.collectAsState()
    val favorites by favoriteViewModel.favorites.collectAsState()
    val listpodcasts by listPodcastViewModel.listPodcasts.collectAsState()
    val podcasts by podcastViewModel.podcasts.collectAsState(initial = emptyList())

    val userPlaylists = playlists.filter { it.userId == loginViewModel?.currentUser?.value?.uid }

    var showPlaylistDialog by remember { mutableStateOf(false) }

    var selectedSongId by remember { mutableStateOf<String?>(null) }

    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf<Music?>(null) }

    var currentPlayingPosition by remember { mutableLongStateOf(0L) }

    val currentPlayingIndex = musicService.currentPlayingIndex
    val isMusicPlaying = musicService.isMusicPlaying
    val totalDuration = musicService.totalDuration

    val musicsSuggest = musics.shuffled().take(5)

    musicService.musics = musicsSuggest

    val onSeek: (Long) -> Unit = { newPosition ->
        currentPlayingPosition = newPosition
        musicService.seekTo(newPosition)
    }

    val onNextSong: () -> Unit = {
        musicService.playNextMusic(context)
        selectedMusic = musicService.currentMusic
    }

    val onPreviousSong: () -> Unit = {
        musicService.playPreviousMusic(context)
        selectedMusic = musicService.currentMusic
    }

    LaunchedEffect(currentPlayingIndex) {
        musicService.currentPlayingIndex = currentPlayingIndex
    }

    LaunchedEffect(musicService.currentMusic) {
        selectedMusic = musicService.currentMusic
    }

    var showPodcastDialog by remember { mutableStateOf(false) }
    var selectedPodcast by remember { mutableStateOf<Podcast?>(null) }

    var currentPlayingPodcastPosition by remember { mutableLongStateOf(0L) }

    podcastService.podcasts = podcasts

    val currentPlayingIndexPodcast = podcastService.currentPlayingIndex
    val isPlayingPodcast = podcastService.isPodcastPlaying
    val totalDurationPodcast = podcastService.totalDuration

    val onSeekPodcast: (Long) -> Unit = { newPosition ->
        currentPlayingPodcastPosition = newPosition
        podcastService.seekTo(newPosition)
    }

    val onNextSongPodcast: () -> Unit = {
        podcastService.playNextPodcast(context)
        selectedPodcast = podcastService.currentPodcast
    }

    val onPreviousSongPodcast: () -> Unit = {
        podcastService.playPreviousPodcast(context)
        selectedPodcast = podcastService.currentPodcast
    }

    LaunchedEffect(currentPlayingIndexPodcast) {
        podcastService.currentPlayingIndex = currentPlayingIndexPodcast
    }

    LaunchedEffect(podcastService.currentPodcast) {
        selectedPodcast = podcastService.currentPodcast
    }

    if (playlistViewModel.showToast) {
        ShowToastMessage(message = playlistViewModel.toastMessage)
        playlistViewModel.showToast = false
    }

    if (favoriteViewModel.showToast) {
        ShowToastMessage(message = favoriteViewModel.toastMessage)
        favoriteViewModel.showToast = false
    }

    if (listPodcastViewModel.showToast) {
        ShowToastMessage(message = listPodcastViewModel.toastMessage)
        listPodcastViewModel.showToast = false
    }

    if (shareMusicViewModel.showToast) {
        ShowToastMessage(message = shareMusicViewModel.toastMessage)
        shareMusicViewModel.showToast = false
    }

    Surface(
        color = Color(28, 28, 28)
    ) {
        if (isError) {
            Text(
                text = loginUiState?.logoutError ?: "Unknown error",
                color = Color.Red,
            )
        }

        if (loginUiState?.isLoading == true) {
            CircularProgressIndicator()
        }

        LaunchedEffect(key1 = loginViewModel?.hasUser) {
            if (loginViewModel?.hasUser == true) {
                onNavToProfilePage.invoke()
            } else {
                onNavToLoginPage.invoke()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { onNavToHomePage.invoke() }) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Home",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Profile",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            loginViewModel?.logout(context)
                            musicService.stopMusic()
                            musicService.setMiniPlayerVisibility(false)
                        },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.size(5.dp))

                            Text(
                                text = "Logout",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Divider(color = Color(130,130,130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hip_hop),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(shape = CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    loginViewModel?.currentUser?.let { currentUser ->
                        Column {
                            Text(
                                text = "${currentUser.value?.email} ♫",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            loginViewModel?.currentUser.value?.uid?.let {
                                Text(
                                    text = it,
                                    fontSize = 12.sp,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Divider(color = Color(130,130,130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(10.dp))

                LazyColumn {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                                    .clickable {
                                        val userId =
                                            loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                        onNavToFavoritePage.invoke()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.height(300.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.FavoriteBorder,
                                        contentDescription = "",
                                        tint = Color(0, 245, 255)
                                    )

                                    Spacer(modifier = Modifier.size(10.dp))

                                    Column(

                                    ) {
                                        Text(
                                            text = "Favorite",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.White
                                        )

                                        val favoriteCount = favoriteViewModel.getFavoriteCountForUser(loginViewModel?.currentUser?.value?.uid ?: "")

                                        Text(
                                            text = favoriteCount.toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.size(10.dp))

                            val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                            val numberOfPodcastList = listPodcastViewModel.getListPodcastCountForUser(userId)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                                    .clickable {
                                        onNavToListPodcastPage.invoke()
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(50.dp),
                                    painter = painterResource(id = R.drawable.podcast),
                                    contentDescription = "",
                                    tint = Color.White,
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Column {
                                    Text(
                                        text = "List podcast",
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )

                                    Text(
                                        text = "$numberOfPodcastList",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Playlist",
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier
                                    .clickable {
                                        onNavToAddPlayListPage.invoke()
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Music with @hongan",
                                    tint = Color.White
                                )

                                Text(
                                    text = "Create playlist",
                                    fontSize = 16.sp,
                                    color = Color.White,
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    items(userPlaylists) { playlist ->
                        val numberOfSongs = playlistViewModel.getNumberOfSongsInPlaylist(playlist.playlistId)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onNavToPlayListMusicPage.invoke(playlist.playlistId)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.audio),
                                    contentDescription = "AddPlaylist",
                                    tint = Color.White,
                                    modifier = Modifier.size(70.dp)
                                )

                                Column {
                                    Text(
                                        text = playlist.playlistName,
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )

                                    val date = LocalDate.parse(playlist.createdDate)
                                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                    val dateNew =  date.format(formatter)

                                    Text(
                                        text = "$numberOfSongs songs - $dateNew",
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    playlistViewModel.deletePlaylist(playlist.playlistId)
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "",
                                    tint = Color(238, 99, 99)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    item {
                        Text(
                            text = "Suggestions for you",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.size(10.dp))
                    }

                    item {
                        LazyRow {
                            items(musicsSuggest) {
                                    music ->
                                Card(
                                    modifier = Modifier
                                        .width(300.dp)
                                        .clickable {
                                            if(podcastService.isPodcastPlaying) {
                                                podcastService.stopPodcast()
                                                podcastService.setMiniPlayerVisibility(false)
                                            }

                                            selectedMusic = music
                                            showMusicDialog = true
                                            musicService.setMiniPlayerVisibility(true)
                                            if (musicService.currentMusic?.musicId != music.musicId) {
                                                musicService.startMusic(context, music)
                                            } else if (!isMusicPlaying) {
                                                musicService.resumeMusic()
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                modifier = Modifier.size(100.dp),
                                                painter = rememberImagePainter(music.imageUrl),
                                                contentDescription = null,
                                            )

                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(5.dp)
                                            ) {
                                                Text(
                                                    text = if (music.songName.length > 15) "${music.songName.take(15)}..." else music.songName,
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 16.sp
                                                )

                                                Text(
                                                    text = if (music.artist.length > 20) "${music.artist.take(20)}..." else music.artist,
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {

                                            IconButton(
                                                onClick = {
                                                    selectedSongId = music.musicId
                                                    showPlaylistDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.baseline_queue_music_24)
                                                    , contentDescription = ""
                                                )
                                            }

                                            val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                            val favoriteId = favorites.find { it.musicId == music?.musicId && it.userId == userId }?.favoriteId

                                            IconButton(
                                                onClick = {
                                                    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                                    music?.musicId?.let { musicId ->
                                                        if (favoriteViewModel.isFavorite(userId, musicId)) {
                                                            if (favoriteId != null) {
                                                                favoriteViewModel.removeFavorite(favoriteId)
                                                            }
                                                        } else {
                                                            favoriteViewModel.addFavorite(userId, musicId)
                                                        }
                                                    }
                                                }
                                            ) {
                                                val isFavorite = music?.musicId?.let { musicId ->
                                                    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                                    favoriteViewModel.isFavorite(userId, musicId)
                                                } ?: false

                                                Icon(
                                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "",
                                                    tint = if (isFavorite) Color.Red else Color.Black,
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                }
            }

            if (musicService.isMiniPlayerVisible) {
                Column(
                    modifier = Modifier
                        .background(Color(54, 54, 54))
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                ) {
                    MiniPlayer(
                        musicService = musicService,
                        showMusicDialog = { showMusicDialog = true }
                    )

                    Divider(color = Color(130,130,130), thickness = 1.dp)
                }
            }

            if (podcastService.isMiniPodcastPlayerVisible) {
                Column(
                    modifier = Modifier
                        .background(Color(54, 54, 54))
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                ) {
                    MiniPodcastPlayer(
                        podcastService = podcastService,
                        showPodcastDialog = { showPodcastDialog = true }
                    )

                    Divider(color = Color(130,130,130), thickness = 1.dp)
                }
            }
        }

        MusicDialog(
            loginViewModel = loginViewModel,
            favoriteViewModel = favoriteViewModel,
            musicService = musicService,
            visible = showMusicDialog,
            music = musicService.currentMusic,
            isMusicPlaying = isMusicPlaying,
            onNextSong = onNextSong,
            onPreviousSong = onPreviousSong,
            onPlayOrPause = { musicService.togglePlayPause() },
            onToggleRepeatMode = { musicService.toggleRepeatMode() },
            onClose = { showMusicDialog = false },
            musics = musics,
            currentPlayingIndex = currentPlayingIndex,
            currentPlayingPosition = currentPlayingPosition,
            totalDuration = totalDuration,
            onSeek = onSeek
        )

        PlaylistDialog(
            visible = showPlaylistDialog,
            playlists = userPlaylists,
            selectedSongId = selectedSongId,
            onClose = { showPlaylistDialog = false },
            onSelectPlaylist = { playlistId ->
                if (selectedSongId != null) {
                    playlistViewModel.addSongToPlaylist(selectedSongId!!, playlistId)
                }
            }
        )

        PodcastDialog(
            loginViewModel = loginViewModel,
            listPodcastViewModel = listPodcastViewModel,
            podcastService = podcastService,
            visible = showPodcastDialog,
            podcast = podcastService.currentPodcast,
            isPodcastPlaying = isPlayingPodcast,
            onNextSong = onNextSongPodcast,
            onPreviousSong = onPreviousSongPodcast,
            onPlayOrPause = { podcastService.togglePlayPause() },
            onToggleRepeatMode = { podcastService.toggleRepeatMode() },
            onClose = { showPodcastDialog = false },
            podcasts = podcasts,
            currentPlayingIndex = currentPlayingIndex,
            currentPlayingPodcastPosition = currentPlayingPodcastPosition,
            totalDurationPodcast = totalDurationPodcast,
            onSeek = onSeekPodcast
        )
    }
}