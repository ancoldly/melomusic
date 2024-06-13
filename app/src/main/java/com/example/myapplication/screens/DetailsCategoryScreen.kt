package com.example.myapplication.screens

import MusicService
import PodcastService
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.myapplication.R
import com.example.myapplication.components.MiniPlayer
import com.example.myapplication.components.MiniPodcastPlayer
import com.example.myapplication.components.MusicDialog
import com.example.myapplication.components.PlaylistDialog
import com.example.myapplication.components.PodcastDialog
import com.example.myapplication.components.ShowToastMessage
import com.example.myapplication.data.Music
import com.example.myapplication.data.Podcast
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.ListPodcastViewModel
import com.example.myapplication.viewmodel.MiniPlayerViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.ShareMusicViewModel
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DetailSCategoryScreen(
    categoryId: String,
    musicService: MusicService,
    podcastService: PodcastService,
    loginViewModel: LoginViewModel? = null,
    onNavToDetailsCategoryPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    miniPlayerViewModel: MiniPlayerViewModel = viewModel(),
    shareMusicViewModel: ShareMusicViewModel = viewModel(),
    podcastViewModel: PodcastViewModel = viewModel(),
    listPodcastViewModel: ListPodcastViewModel = viewModel(),
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.logoutError != null
    val context = LocalContext.current
    val categories by categoryViewModel.categories.collectAsState(initial = emptyList())
    val musics by musicViewModel.musics.collectAsState(initial = emptyList())
    val podcasts by podcastViewModel.podcasts.collectAsState(initial = emptyList())

    val category = categories.find { it.categoryId == categoryId }
    val filteredMusics = musics.filter { it.genre == category?.categoryId }
    val numberOfSongs = filteredMusics.count()
    val playlists by playlistViewModel.playlists.collectAsState()
    val favorites by favoriteViewModel.favorites.collectAsState()

    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
    val userPlaylists = playlists.filter { it.userId == loginViewModel?.currentUser?.value?.uid }

    var showPlaylistDialog by remember { mutableStateOf(false) }

    var selectedSongId by remember { mutableStateOf<String?>(null) }

    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf<Music?>(null) }

    var currentPlayingPosition by remember { mutableStateOf(0L) }

    musicService.musics = filteredMusics

    val currentPlayingIndex = musicService.currentPlayingIndex
    val isMusicPlaying = musicService.isMusicPlaying
    val totalDuration = musicService.totalDuration

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
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                item {
                    IconButton(
                        onClick = {
                        onNavToHomePage.invoke()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Home",
                            tint = Color.White
                        )
                    }
                }

                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10))
                                .size(150.dp),
                            contentScale = ContentScale.Crop,
                            painter = rememberImagePainter(
                                category?.categoryImageUrl
                            ),
                            contentDescription = ""
                        )

                        Spacer(modifier = Modifier.size(20.dp))

                        category?.categoryName?.let {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = it,
                                fontSize = 18.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = "MeloMusic",
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        Text(
                            text = "$numberOfSongs songs",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.size(20.dp))
                }

                if (categories.isEmpty() || musics.isEmpty()) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(filteredMusics) { music ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
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