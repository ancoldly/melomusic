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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.myapplication.components.MiniPlayer
import com.example.myapplication.components.MiniPodcastPlayer
import com.example.myapplication.components.MusicDialog
import com.example.myapplication.components.PlaylistDialog
import com.example.myapplication.components.PodcastDialog
import com.example.myapplication.components.ShowToastMessage
import com.example.myapplication.data.Music
import com.example.myapplication.data.Podcast
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.ListPodcastViewModel
import com.example.myapplication.viewmodel.MiniPlayerViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.ShareMusicViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun FavoriteScreen(
    musicService: MusicService,
    podcastService: PodcastService,
    loginViewModel: LoginViewModel? = null,
    onNavToProfilePage: () -> Unit,
    musicViewModel: MusicViewModel = viewModel(),
    playlistViewModel: PlaylistViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    miniPlayerViewModel: MiniPlayerViewModel = viewModel(),
    shareMusicViewModel: ShareMusicViewModel = viewModel(),
    podcastViewModel: PodcastViewModel = viewModel(),
    listPodcastViewModel: ListPodcastViewModel = viewModel(),
) {
    val favorites by favoriteViewModel.favorites.collectAsState()
    val musics by musicViewModel.musics.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val podcasts by podcastViewModel.podcasts.collectAsState(initial = emptyList())

    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
    val userFavoriteIds = favorites.filter { it.userId == userId }.map { it.musicId }
    val userFavoriteSongs = musics.filter { it.musicId in userFavoriteIds }

    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf<Music?>(null) }

    var currentPlayingPosition by remember { mutableStateOf(0L) }

    musicService.musics = userFavoriteSongs

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
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            onNavToProfilePage.invoke()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, contentDescription = "Home",
                            tint = Color.White
                        )
                    }

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Favorite \uD83D\uDC95",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))

                Divider(color = Color(130,130,130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(10.dp))

                LazyColumn {
                    items(userFavoriteSongs) { song ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if(podcastService.isPodcastPlaying) {
                                        podcastService.stopPodcast()
                                        podcastService.setMiniPlayerVisibility(false)
                                    }

                                    selectedMusic = song
                                    showMusicDialog = true
                                    musicService.setMiniPlayerVisibility(true)
                                    if (musicService.currentMusic?.musicId != song.musicId) {
                                        musicService.startMusic(context, song)
                                    } else if (!isMusicPlaying) {
                                        musicService.resumeMusic()
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Image(
                                    modifier = Modifier.size(100.dp),
                                    painter = rememberImagePainter(song.imageUrl),
                                    contentDescription = null,
                                )

                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(
                                        text = if (song.songName.length > 15) "${song.songName.take(15)}..." else song.songName,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 16.sp
                                    )

                                    Text(
                                        text = if (song.artist.length > 20) "${song.artist.take(20)}..." else song.artist,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val favoriteId = favorites.find { it.musicId == song.musicId && it.userId == userId }?.favoriteId
                                        favoriteId?.let { favoriteViewModel.removeFavorite(it) }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Favorite,
                                        contentDescription = "",
                                        tint = Color(178, 58, 238)
                                    )
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
}