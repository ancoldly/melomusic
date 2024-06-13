package com.example.myapplication.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.myapplication.R
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.PodcastViewModel

@Composable
fun AdminPodcastScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminHomePage: () -> Unit,
    onNavToAddPodcastHomePage: () -> Unit,
    onNavToEditPodcastHomePage: (String) -> Unit,
) {
    val podcastViewModel: PodcastViewModel = viewModel()
    val context = LocalContext.current

    val podcasts by podcastViewModel.podcasts.collectAsState()

    Surface(
        color = Color(28, 28, 28)
    ) {
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
                    onClick = { onNavToAdminHomePage.invoke() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack, contentDescription = "Home",
                        tint = Color.White
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Manager podcast",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.size(20.dp))

            Divider(color = Color(130,130,130), thickness = 1.dp)

            Spacer(modifier = Modifier.size(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavToAddPodcastHomePage.invoke()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "AddPodcast",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.size(10.dp))

                Column {
                    Text(
                        text = "Add podcast",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "@Melomusic",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            Divider(color = Color(130,130,130), thickness = 1.dp)

            Spacer(modifier = Modifier.size(20.dp))

            Row {
                Icon(
                    Icons.Default.List, contentDescription = "",
                    tint = Color.White
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "List podcast",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(podcasts) {
                    podcast ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
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
                                        podcastViewModel.deletePodcast(podcast.podcastId, podcast.imageUrl, podcast.audioUrl)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.delete)
                                        , contentDescription = ""
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        onNavToEditPodcastHomePage.invoke(podcast.podcastId)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.edit)
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