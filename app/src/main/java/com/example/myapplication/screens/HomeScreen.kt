import androidx.compose.foundation.Image
import MusicService
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CategoryViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.myapplication.components.ImageBackground
import com.example.myapplication.components.LoadingIndicator
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
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.ShareMusicViewModel

@OptIn(ExperimentalCoilApi::class)
@Composable
fun HomeScreen(
    loginViewModel: LoginViewModel? = null,
    musicService: MusicService,
    podcastService: PodcastService,
    onNavToAdminHomePage: () -> Unit,
    onNavToLoginPage: () -> Unit,
    onNavToShareMusicPage: () -> Unit,
    onNavToProfilePage: () -> Unit,
    onNavToDetailsCategoryPage: (String) -> Unit,
    onNavToMusicPage: () -> Unit,
    onNavSearchMusicPage: () -> Unit,
    onNavToPodcastPage: () -> Unit,
    categoryViewModel: CategoryViewModel = viewModel(),
    musicViewModel: MusicViewModel = viewModel(),
    podcastViewModel: PodcastViewModel = viewModel(),
    playlistViewModel: PlaylistViewModel = viewModel(),
    favoriteViewModel: FavoriteViewModel = viewModel(),
    listPodcastViewModel: ListPodcastViewModel = viewModel(),
    onNavToHomePage: () -> Unit,
    onNavToRankerMusicPage: () -> Unit,
    miniPlayerViewModel: MiniPlayerViewModel = viewModel(),
    shareMusicViewModel: ShareMusicViewModel = viewModel()
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.logoutError != null
    val context = LocalContext.current

    val categories by categoryViewModel.categories.collectAsState(initial = emptyList())
    val musics by musicViewModel.musics.collectAsState(initial = emptyList())
    val podcasts by podcastViewModel.podcasts.collectAsState(initial = emptyList())
    val playlists by playlistViewModel.playlists.collectAsState()
    val listpodcasts by listPodcastViewModel.listPodcasts.collectAsState()
    val favorites by favoriteViewModel.favorites.collectAsState()

    val userPlaylists = playlists.filter { it.userId == loginViewModel?.currentUser?.value?.uid }

    var showPlaylistDialog by remember { mutableStateOf(false) }

    var selectedSongId by remember { mutableStateOf<String?>(null) }

    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf<Music?>(null) }

    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"

    var currentPlayingPosition by remember { mutableStateOf(0L) }

    musicService.musics = musics

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

    var currentPlayingPodcastPosition by remember { mutableStateOf(0L) }

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

        if (categories.isEmpty() || musics.isEmpty() || podcasts.isEmpty()) {
            LoadingIndicator()
        } else {
            if (isError) {
                Text(
                    text = loginUiState?.logoutError ?: "Unknown error",
                    color = Color.Red,
                )
            }

            if (loginUiState?.isLoading == true) {
                CircularProgressIndicator()
            }

            LaunchedEffect(key1 = Unit) {
                if (loginViewModel?.hasUser == true) {
                    loginViewModel?.currentUser?.observeForever { user ->
                        user?.let { currentUser ->
                            if (currentUser.email == "adminmelo@gmail.com") {
                                onNavToAdminHomePage.invoke()
                            } else {
                                onNavToHomePage.invoke()
                            }
                        } ?: run {
                            onNavToLoginPage.invoke()
                        }
                    }
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable {
                                    onNavToProfilePage.invoke()
                                }
                                .background(color = Color(0, 205, 102), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = loginViewModel?.currentUser?.value?.email?.firstOrNull()?.uppercase().toString() ?: "N/A",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row {

                            Button(
                                colors = ButtonDefaults.buttonColors(Color(130,130,130)),
                                onClick = {
                                    onNavToMusicPage.invoke()
                                }
                            ) {
                                Text(
                                    text = "Music",
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.size(10.dp))

                            Button(
                                colors = ButtonDefaults.buttonColors(Color(130,130,130)),
                                onClick = {
                                    onNavToPodcastPage.invoke()
                                }
                            ) {
                                Text(
                                    text = "Podcast",
                                    fontSize = 16.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                onNavSearchMusicPage.invoke()
                            }
                        ) {
                            Icon(
                                Icons.Default.Search, contentDescription = "Home",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Divider(color = Color(130,130,130), thickness = 1.dp)

                    Spacer(modifier = Modifier.size(10.dp))

                    LazyColumn(

                    ) {
                        item {
                            Text(
                                text = "Diverse music genres",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        items(categories.chunked(2)) { rowCategories ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (category in rowCategories) {
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onNavToDetailsCategoryPage.invoke(category.categoryId)
                                            },
                                        shape = RoundedCornerShape(0.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.background(color = Color(28,28,28))
                                        ) {
                                            Image(
                                                painter = rememberImagePainter(category.categoryImageUrl),
                                                contentDescription = "Category Image",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(shape = RoundedCornerShape(10.dp))
                                                    .height(150.dp),
                                                contentScale = ContentScale.Crop
                                            )

                                            Spacer(modifier = Modifier.size(10.dp))

                                            Text(
                                                text = category.categoryName,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )

                                            Spacer(modifier = Modifier.size(10.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.size(10.dp))
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        item {
                            Text(
                                text = "The most popular music today",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        item {
                            LazyRow {
                                items(musics.take(5)) { music ->
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

                        item {
                            Spacer(modifier = Modifier.size(30.dp))
                        }

                        item {
                            Text(
                                text = "Song rankings",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        item {
                            val text = "#Melochart"
                            val rainbowColors = listOf(
                                Color(255, 127, 36),
                                Color(255, 140, 0),
                                Color(255, 165, 0),
                                Color(238, 154, 73),
                                Color(255, 106, 106),
                                Color(255, 106, 106),
                                Color(255, 105, 180),
                                Color(255, 130, 171),
                                Color(255, 130, 171),
                                Color(255, 110, 180),
                            )

                            val rainbowText = buildAnnotatedString {
                                text.forEachIndexed { index, char ->
                                    val color = rainbowColors[index % rainbowColors.size]
                                    withStyle(style = SpanStyle(color = color)) {
                                        append(char)
                                    }
                                }
                            }

                            Text(
                                text = rainbowText,
                                fontWeight = FontWeight.Bold,
                                style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                            )

                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        itemsIndexed(musics.sortedByDescending { it.playCount }.take(5)) {
                                index, music ->
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
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painter = rememberImagePainter(music.imageUrl),
                                            contentDescription = "hothitsVn",
                                            modifier = Modifier.size(100.dp)
                                        )

                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(horizontal = 10.dp),
                                                text = "#${index + 1}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(painter = painterResource(id = R.drawable.baseline_headphones_24), contentDescription = "", modifier = Modifier.size(15.dp))
                                                Text(
                                                    text = "${music.playCount}",
                                                    fontWeight = FontWeight.Normal,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }

                                        Column(
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

                        item {
                            Divider(color = Color(130,130,130), thickness = 1.dp)

                            TextButton(
                                onClick = {
                                    onNavToRankerMusicPage.invoke()
                                }
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "See more",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                )
                            }

                            Divider(color = Color(130,130,130), thickness = 1.dp)
                        }

                        item {
                            Spacer(modifier = Modifier.size(30.dp))
                        }

                        item {
                            Text(
                                text = "Popular podcasts",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(10.dp))
                        }

                        item {
                            LazyRow {
                                items(podcasts.take(3)) { podcast ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                if(musicService.isMusicPlaying) {
                                                    musicService.stopMusic()
                                                    musicService.setMiniPlayerVisibility(false)
                                                }

                                                selectedPodcast = podcast
                                                showPodcastDialog = true
                                                podcastService.setMiniPlayerVisibility(true)

                                                if (podcastService.currentPodcast?.podcastId != podcast.podcastId) {
                                                    podcastService.startPodcast(context, podcast)
                                                } else if (!isPlayingPodcast) {
                                                    podcastService.resumePodcast()
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
                                                    painter = rememberImagePainter(podcast.imageUrl),
                                                    contentDescription = null,
                                                )

                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    Text(
                                                        text = if (podcast.podcastName.length > 15) "${podcast.podcastName.take(15)}..." else podcast.podcastName,
                                                        fontWeight = FontWeight.Medium,
                                                        fontSize = 16.sp
                                                    )

                                                    Text(
                                                        text = if (podcast.artist.length > 20) "${podcast.artist.take(20)}..." else podcast.artist,
                                                        fontWeight = FontWeight.Normal,
                                                        fontSize = 12.sp
                                                    )
                                                }
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                                        listPodcastViewModel.addListPodcast(userId, podcast.podcastId)
                                                    }
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.baseline_add_circle_outline_24)
                                                        , contentDescription = ""
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
    }
}