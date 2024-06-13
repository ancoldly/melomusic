package com.example.myapplication.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.CategoryViewModel
import com.example.myapplication.viewmodel.MusicViewModel
import com.example.myapplication.viewmodel.PodcastViewModel

@Composable
fun AdminHomeScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToLoginPage: () -> Unit,
    onNavToAdminHomePage: () -> Unit,
    onNavToAdminCategoryPage: () -> Unit,
    onNavToAdminMusicPage: () -> Unit,
    onNavToAdminUserPage: () -> Unit,
    onNavToAdminPodcastPage: () -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.logoutError != null
    val context = LocalContext.current
    val categoryViewModel: CategoryViewModel = viewModel()
    val musicViewModel: MusicViewModel = viewModel()
    val podcastViewModel: PodcastViewModel = viewModel()

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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Admin Home",
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        loginViewModel?.logout(context)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Transparent)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")

                        Text(
                            modifier = Modifier.padding(5.dp),
                            text = "Logout",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.manager),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shape = CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )

                Spacer(modifier = Modifier.size(10.dp))

                loginViewModel?.currentUser?.let { currentUser ->
                    Text(
                        text = "${currentUser.value?.email} ♫",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.size(10.dp))
            
            Text(
                text = "Manage admin functions",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.size(10.dp))

            Column(
                
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            onNavToAdminUserPage.invoke()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "AddPlaylist",
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = "Manager User",
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

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            onNavToAdminCategoryPage.invoke()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.category),
                        contentDescription = "AddPlaylist",
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = "Manager Genre Music",
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

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            onNavToAdminMusicPage.invoke()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.audio),
                        contentDescription = "AddPlaylist",
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = "Manager Music",
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

                Spacer(modifier = Modifier.size(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable {
                            onNavToAdminPodcastPage.invoke()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.podcast),
                        contentDescription = "AddPlaylist",
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = "Manager Podcast",
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
            }
        }
    }
}