package com.example.myapplication.components
import MusicService
import PodcastService
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.CommentPodcast
import com.example.myapplication.data.Music
import com.example.myapplication.data.Playlist
import com.example.myapplication.data.Podcast
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CommentPodcastViewModel
import com.example.myapplication.viewmodel.FavoriteViewModel
import com.example.myapplication.viewmodel.ListPodcastViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PlaylistViewModel
import com.example.myapplication.viewmodel.PodcastViewModel
import com.example.myapplication.viewmodel.ShareMusicViewModel
import com.example.myapplication.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ImageBackground(
    imageResId: Int,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldComponent(
    onTextChanged: (text: String) -> Unit,
    placeholderValue: String,
    leadingIcon: Int,
    contentDescription: String,
    value: String,
    isError: Boolean
) {
    var currentValue by remember { mutableStateOf("") }

    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = currentValue,
        onValueChange = {
            currentValue = it
            onTextChanged(it)
        },
        label = {
            Text(
                text = placeholderValue,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
        ),
        leadingIcon = {
            Icon(painter = painterResource(id = leadingIcon), contentDescription = contentDescription, tint = Color.White)
        },
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(50,205,50),
            unfocusedBorderColor = Color.White,
        )
    )
}

@Composable
fun PasswordFieldComponent(
    onTextChanged: (text: String) -> Unit,
    placeholderValue: String,
    value: String,
    isError: Boolean,
) {
    var currentValue by remember { mutableStateOf("") }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    val localFocusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = currentValue,
        onValueChange = {
            currentValue = it
            onTextChanged(it)
        },
        label = {
            Text(
                text = placeholderValue,
                fontSize = 18.sp,
                color = Color.White
            )
        },
        textStyle = TextStyle.Default.copy(fontSize = 18.sp, color = Color.White),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color.White)
        },
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardActions = KeyboardActions {
            localFocusManager.clearFocus()
        },
        trailingIcon = {
            val iconImage = if (passwordVisible.value) {
                painterResource(id = R.drawable.eye_off)
            } else {
                painterResource(id = R.drawable.eye)
            }

            val description = if (passwordVisible.value) {
                "Hide Password"
            } else {
                "Show Password"
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(painter = iconImage, contentDescription = description, tint = Color.White)
            }
        }
        ,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(50,205,50),
            unfocusedBorderColor = Color.White,
        )
    )
}

@Composable
fun HeadingText(value:String, color: Color) {
    Text(
        text = value,
        fontSize = 26.sp,
        color = color,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun NormalText(value:String) {
    Text(
        text = value,
        fontSize = 18.sp,
        color = Color.Black,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun MinText(value: String, fontSize: Int, color: Color) {
    Text(
        text = value,
        fontSize = fontSize.sp,
        color = color,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
fun DividerTextComponent() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f),
            color = Color.Gray,
            thickness = 1.dp
        )

        Text(
            modifier = Modifier.padding(8.dp),
            text = "Or",
            fontSize = 18.sp,
            color = Color.White)

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}

@Composable
fun Button(
    onClick : () -> Unit,
    value:String,
    fontSize: Int,
    color: Color,
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(Color(255, 130, 71)),
        contentPadding = PaddingValues(12.dp)
    ) {
        Text(
            text = value,
            fontSize = fontSize.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun LoadingIndicator() {
    ImageBackground(imageResId = R.drawable.bgrlogin) {
        ImageBackground(imageResId = R.drawable.bgrlogin) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "MeloMusic loading...",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MusicDialog(
        musicViewModel: MusicViewModel = viewModel(),
        loginViewModel: LoginViewModel? = null,
        favoriteViewModel: FavoriteViewModel = viewModel(),
        musicService: MusicService,
        visible: Boolean,
        music: Music?,
        isMusicPlaying: Boolean,
        onNextSong: () -> Unit,
        onPreviousSong: () -> Unit,
        onPlayOrPause: () -> Unit,
        onClose: () -> Unit,
        musics: List<Music>,
        currentPlayingIndex: Int,
        currentPlayingPosition: Long,
        onToggleRepeatMode: () -> Unit,
        totalDuration: Long = 0L,
        onSeek: (Long) -> Unit,
        shareMusicViewModel: ShareMusicViewModel = viewModel()
    ) {
        val context = LocalContext.current
        var currentPlayingPosition by remember { mutableLongStateOf(0L) }
        val favorites by favoriteViewModel.favorites.collectAsState()
        val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
        var hasIncrementedPlayCount by remember { mutableStateOf(false) }

        LaunchedEffect(musicService.currentMusic) {
            musicService.currentMusic?.let { music ->
                val job = launch {
                    while (true) {
                        if (musicService.isMusicPlaying) {
                            val currentPosition = musicService.mediaPlayer?.currentPosition ?: 0
                            currentPlayingPosition = currentPosition.toLong()

                            if (currentPlayingPosition >= totalDuration) {
                                if (musicService.currentPlayingIndex < musicService.musics.size - 1) {
                                    withContext(Dispatchers.Main) {
                                        onNextSong()
                                    }
                                }
                            }

                            if (!musicService.isCheckView && currentPlayingPosition >= totalDuration / 2) {
                                musicService.currentMusic?.let { music ->
                                    musicViewModel.incrementPlayCount(music.musicId)
                                    musicService.isCheckView = true
                                }
                            }
                        }
                        delay(1000)
                    }
                }
                job.join()
            }
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            Surface(
                color = Color(28, 28, 28)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Player Music",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                        )
                        IconButton(
                            onClick = onClose
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "",
                                tint = Color.White,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Divider(color = Color(130, 130, 130), thickness = 1.dp)

                    Spacer(modifier = Modifier.size(20.dp))

                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Image(
                                painter = rememberImagePainter(music?.imageUrl),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(250.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                            )

                            Spacer(modifier = Modifier.size(20.dp))
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    music?.songName?.let {
                                        Text(
                                            text = it,
                                            color = Color.White,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 18.sp
                                        )
                                    }

                                    music?.artist?.let {
                                        Text(
                                            text = it,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 12.sp
                                        )
                                    }
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
                                        tint = if (isFavorite) Color.Red else Color.White,
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatTime(currentPlayingPosition),
                                    color = Color.White,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                val sliderColors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.Gray,
                                    inactiveTrackColor = Color.Gray
                                )

                                Slider(
                                    value = currentPlayingPosition.toFloat(),
                                    onValueChange = { newValue ->
                                        onSeek(newValue.toLong())
                                    },
                                    valueRange = 0f..totalDuration.toFloat(),
                                    steps = (totalDuration.toFloat() / 1000).toInt(),
                                    colors = sliderColors,
                                    modifier = Modifier.weight(3f),
                                    thumb = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_music_note_24),
                                            contentDescription = "",
                                            tint = Color(0, 191, 255),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Text(
                                    text = formatTime(totalDuration),
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!shareMusicViewModel.sharedMusics.value.any { it.musicId == music?.musicId && it.userId == userId }) {
                                    IconButton(
                                        onClick = {
                                            music?.musicId?.let {
                                                shareMusicViewModel.saveShareMusic(userId, it)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Share, contentDescription = "", tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                } else {
                                    IconButton(
                                        onClick = {
                                            music?.musicId?.let { musicId ->
                                                val shareMusicId = shareMusicViewModel.sharedMusics.value.find { it.musicId == musicId && it.userId == userId }?.shareId
                                                shareMusicId?.let {
                                                    shareMusicViewModel.deleteShareMusic(it)
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Share, contentDescription = "", tint = Color.Yellow,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                }

                                IconButton(onClick = { onPreviousSong() }) {
                                    Icon(
                                        Icons.Default.ArrowBack, contentDescription = "", tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                IconButton(onClick = { onPlayOrPause() }) {
                                    Icon(
                                        painterResource(id = if (isMusicPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                                        contentDescription = "", tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                IconButton(onClick = { onNextSong() }) {
                                    Icon(
                                        Icons.Default.ArrowForward, contentDescription = "", tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                IconButton(onClick = { musicService.toggleRepeatMode() }) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "",
                                        tint = when (musicService.repeatMode) {
                                            RepeatMode.OFF -> Color.White
                                            RepeatMode.ONE -> Color(0,205,0)
                                        },
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.size(20.dp))

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Lyric music",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Divider(
                                modifier = Modifier.width(100.dp),
                                color = Color(130, 130, 130),
                                thickness = 2.dp
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            music?.lyric?.let { Text(
                                text = it,
                                color = Color.White,
                                fontSize = 16.sp
                            ) }
                        }
                    }
                }
            }
        }
    }

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastDialog(
    podcastViewModel: PodcastViewModel = viewModel(),
    loginViewModel: LoginViewModel? = null,
    listPodcastViewModel: ListPodcastViewModel = viewModel(),
    podcastService: PodcastService,
    visible: Boolean,
    podcast: Podcast?,
    isPodcastPlaying: Boolean,
    onNextSong: () -> Unit,
    onPreviousSong: () -> Unit,
    onPlayOrPause: () -> Unit,
    onClose: () -> Unit,
    podcasts: List<Podcast>,
    currentPlayingIndex: Int,
    currentPlayingPodcastPosition: Long,
    onToggleRepeatMode: () -> Unit,
    totalDurationPodcast: Long = 0L,
    onSeek: (Long) -> Unit,
    commentPodcastViewModel: CommentPodcastViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    var currentPlayingPosition by remember { mutableLongStateOf(0L) }
    val listpodcasts by listPodcastViewModel.listPodcasts.collectAsState()
    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
    var comment by remember { mutableStateOf("") }

    val commentPodcasts by commentPodcastViewModel.commentPodcast.collectAsState()
    val users by userViewModel.users.collectAsState()

    LaunchedEffect(podcastService.currentPodcast) {
        podcastService.currentPodcast?.let { podcast ->
            val job = launch {
                while (true) {
                    if (podcastService.isPodcastPlaying) {
                        val currentPosition = podcastService.mediaPlayer?.currentPosition ?: 0
                        currentPlayingPosition = currentPosition.toLong()

                        if (currentPlayingPosition >= totalDurationPodcast) {
                            if (podcastService.currentPlayingIndex < podcastService.podcasts.size - 1) {
                                withContext(Dispatchers.Main) {
                                    onNextSong()
                                }
                            }
                        }
                    }
                    delay(1000)
                }
            }
            job.join()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 500)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(durationMillis = 500)
        )
    ) {
        Surface(
            color = Color(28, 28, 28)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Player Podcast",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                    )
                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            tint = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Divider(color = Color(130, 130, 130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(20.dp))

                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Image(
                            painter = rememberImagePainter(podcast?.imageUrl),
                            contentDescription = "",
                            modifier = Modifier
                                .size(250.dp)
                                .clip(RoundedCornerShape(10.dp)),
                        )

                        Spacer(modifier = Modifier.size(20.dp))
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                horizontalAlignment = Alignment.Start
                            ) {
                                podcast?.podcastName?.let {
                                    Text(
                                        text = it,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp
                                    )
                                }

                                podcast?.artist?.let {
                                    Text(
                                        text = it,
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentPlayingPosition),
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            val sliderColors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.Gray,
                                inactiveTrackColor = Color.Gray
                            )

                            Slider(
                                value = currentPlayingPosition.toFloat(),
                                onValueChange = { newValue ->
                                    onSeek(newValue.toLong())
                                },
                                valueRange = 0f..totalDurationPodcast.toFloat(),
                                steps = (totalDurationPodcast.toFloat() / 1000).toInt(),
                                colors = sliderColors,
                                modifier = Modifier.weight(3f),
                                thumb = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_music_note_24),
                                        contentDescription = "",
                                        tint = Color(0, 191, 255),
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = formatTime(totalDurationPodcast),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val listpodcastId = listpodcasts.find { it.podcastId == podcast?.podcastId && it.userId == userId }?.listPodcastId

                            IconButton(
                                onClick = {
                                    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                    podcast?.podcastId?.let { podcastId ->
                                        if (listPodcastViewModel.isListPodcast(userId, podcastId)) {
                                            if (listpodcastId != null) {
                                                listPodcastViewModel.removeListPodcast(listpodcastId)
                                            }
                                        } else {
                                            listPodcastViewModel.addListPodcast(userId, podcastId)
                                        }
                                    }
                                }
                            ) {
                                val isListPodcast = podcast?.podcastId?.let { podcastId ->
                                    val userId = loginViewModel?.currentUser?.value?.uid ?: "defaultUserId"
                                    listPodcastViewModel.isListPodcast(userId, podcastId)
                                } ?: false

                                Icon(
                                    imageVector = if (isListPodcast) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "",
                                    tint = if (isListPodcast) Color.Red else Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            IconButton(onClick = { onPreviousSong() }) {
                                Icon(
                                    Icons.Default.ArrowBack, contentDescription = "", tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            IconButton(onClick = { onPlayOrPause() }) {
                                Icon(
                                    painterResource(id = if (isPodcastPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                                    contentDescription = "", tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            IconButton(onClick = { onNextSong() }) {
                                Icon(
                                    Icons.Default.ArrowForward, contentDescription = "", tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            IconButton(onClick = { podcastService.toggleRepeatMode() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "",
                                    tint = when (podcastService.repeatMode) {
                                        RepeatModePodcast.OFF -> Color.White
                                        RepeatModePodcast.ONE -> Color(0,205,0)
                                    },
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.size(10.dp))

                        Divider(color = Color(130,130,130), thickness = 1.dp)

                        Column {
                            Spacer(modifier = Modifier.size(10.dp))

                            Text(
                                text = "Comment",
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Divider(
                                modifier = Modifier.width(50.dp),
                                color = Color(130,130,130),
                                thickness = 2.dp
                            )

                            Spacer(modifier = Modifier.size(10.dp))

                            val keyboardController = LocalSoftwareKeyboardController.current

                            Row {
                                TextField(
                                    modifier = Modifier
                                        .height(50.dp),
                                    value = comment,
                                    onValueChange = { comment = it },
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Done,
                                    ),
                                    textStyle = TextStyle.Default.copy(fontSize = 16.sp, color = Color.White)
                                )

                                IconButton(
                                    onClick = {
                                        podcast?.podcastId?.let {
                                            commentPodcastViewModel.addComment(
                                                it, userId, comment)
                                        }

                                        keyboardController?.hide()
                                        comment = ""
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Send, contentDescription = "Send", tint = Color.Cyan,
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.size(20.dp))
                    }

                    val commentsForPodcast = commentPodcasts.filter { it.podcastId == podcast?.podcastId }

                    items(commentsForPodcast.asReversed()) { comment ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.hip_hop),
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(shape = CircleShape)
                                        .border(2.dp, Color.White, CircleShape)
                                )

                                Spacer(modifier = Modifier.size(10.dp))

                                Column(
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    val user = users.find { it.userId == comment.userId }

                                    if (user != null) {
                                        Text(
                                            text = user.email + " \uD83D\uDD25",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    Text(
                                        fontSize = 14.sp,
                                        text = comment.content,
                                        color = Color.Gray
                                    )
                                }
                            }

                            if(comment.userId == loginViewModel?.currentUser?.value?.uid) {
                                Icon(
                                    Icons.Default.Close, contentDescription = "X",
                                    tint = Color.White,
                                    modifier = Modifier.clickable {
                                        podcast?.podcastId?.let {
                                            commentPodcastViewModel.deleteComment(comment.commentId,
                                                it
                                            )
                                        }
                                    },
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistDialog(
    visible: Boolean,
    playlistViewModel: PlaylistViewModel = viewModel(),
    playlists: List<Playlist>,
    selectedSongId: String?,
    onClose: () -> Unit,
    onSelectPlaylist: (playlistId: String) -> Unit
) {
    AnimatedVisibility(visible = visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(54, 54, 54))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Select Playlist",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                    )

                    IconButton(
                        onClick = onClose
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "",
                            tint = Color.White,
                        )
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))

                Divider(color = Color(130, 130, 130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(10.dp))

                LazyColumn {
                    items(playlists) { playlist ->
                        val numberOfSongs = playlistViewModel.getNumberOfSongsInPlaylist(playlist.playlistId)
                        Row(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(10.dp))
                                .fillMaxWidth()
                                .clickable {
                                    if (selectedSongId != null) {
                                        onSelectPlaylist(playlist.playlistId)
                                    }
                                    onClose()
                                },
                            verticalAlignment = Alignment.CenterVertically,
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

                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    musicService: MusicService,
    showMusicDialog: () -> Unit
) {
    val currentMusic = musicService.currentMusic
    val isPlaying = musicService.isMusicPlaying
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { showMusicDialog() },
        color = Color(54, 54, 54)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            currentMusic?.let {
                Image(
                    painter = rememberImagePainter(it.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = it.songName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = it.artist,
                        color = Color.Gray,
                        fontSize = 12.sp,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = { musicService.togglePlayPause() }) {
                    Icon(
                        painter = painterResource(id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { musicService.playNextMusic(context) }) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun MiniPodcastPlayer(
    podcastService: PodcastService,
    showPodcastDialog: () -> Unit
) {
    val currentMusic = podcastService.currentPodcast
    val isPlaying = podcastService.isPodcastPlaying
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable { showPodcastDialog() },
        color = Color(54, 54, 54)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            currentMusic?.let {
                Image(
                    painter = rememberImagePainter(it.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = if (it.podcastName.length > 20) "${it.podcastName.take(20)}..." else it.podcastName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = it.artist,
                        color = Color.Gray,
                        fontSize = 12.sp,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = { podcastService.togglePlayPause() }) {
                    Icon(
                        painter = painterResource(id = if (isPlaying) R.drawable.baseline_pause_24 else R.drawable.baseline_play_arrow_24),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { podcastService.playNextPodcast(context) }) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ShowToastMessage(message: String) {
    val context = LocalContext.current

    LaunchedEffect(message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}




