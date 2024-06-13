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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.myapplication.login.LoginViewModel
import com.example.myapplication.viewmodel.UserViewModel
import com.example.myapplication.R

@Composable
fun AdminUserScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminHomePage: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val users by userViewModel.users.collectAsState(initial = emptyList())

    Surface(
        color = Color(28, 28, 28)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
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
                        text = "Manager user",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(20.dp))

                Divider(color = Color(130,130,130), thickness = 1.dp)

                Spacer(modifier = Modifier.size(20.dp))
            }

            item {
                Row {
                    Icon(
                        Icons.Default.List, contentDescription = "",
                        tint = Color.White
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Text(
                        text = "List user",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.size(10.dp))
            }

            items(users) { user ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 10.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier.size(60.dp),
                                painter = painterResource(id = R.drawable.usermusic),
                                contentDescription = "",
                            )

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    text = user.email,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = user.userId,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        }

//                        IconButton(
//                            onClick = {
//                                userViewModel.deleteUser(user.userId)
//                            }
//                        ) {
//                            Icon(Icons.Default.Close, contentDescription = "")
//                        }
                    }
                }

                Spacer(modifier = Modifier.size(10.dp))
            }
        }
    }
}