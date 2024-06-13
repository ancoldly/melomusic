package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.components.DividerTextComponent
import com.example.myapplication.components.HeadingText
import com.example.myapplication.components.ImageBackground
import com.example.myapplication.components.MinText
import com.example.myapplication.components.PasswordFieldComponent
import com.example.myapplication.components.TextFieldComponent
import com.example.myapplication.login.LoginViewModel

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToAdminHomePage:() -> Unit,
    onNavToHomePage:() -> Unit,
    onNavToSignUpPage:() -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current

    Surface(

    ) {
        ImageBackground(imageResId = R.drawable.bgrlogin) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                ,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(50.dp))

                HeadingText(
                    value = "Welcome, please log in ♫", color = Color.White
                )

                Spacer(modifier = Modifier.size(100.dp))

                if(isError) {
                    Text(text = loginUiState?.loginError ?: "Unknown error",
                        color = Color.Red,
                    )
                }

                TextFieldComponent(
                    isError = isError,
                    value = loginUiState?.userName ?: "",
                    placeholderValue = "Enter your email",
                    leadingIcon = R.drawable.mail,
                    contentDescription = "Email",
                    onTextChanged = {
                        loginViewModel?.onUserNameChange(it)
                    }
                )

                Spacer(modifier = Modifier.size(20.dp))
                
                PasswordFieldComponent(
                    isError = isError,
                    placeholderValue = "Enter your password",
                    value = loginUiState?.password ?: "",
                    onTextChanged = {
                       loginViewModel?.onUserPasswordChange(it)
                    },
                )

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var isChecked by remember { mutableStateOf(false) }

                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { newCheckedState ->
                                isChecked = newCheckedState

                            }
                        )

                        MinText(value = "Remember me", fontSize = 16, color = Color.White)
                    }

                    TextButton(
                        onClick = {},
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        MinText(value = "Forgot Password?", fontSize = 16, color = Color(255, 130, 71))
                    }

                }

                com.example.myapplication.components.Button(
                    onClick = {
                       loginViewModel?.loginUser(context)

                        loginViewModel?.currentUser?.observeForever { user ->
                            user?.let { currentUser ->
                                if (currentUser.email == "adminmelo@gmail.com") {
                                    onNavToAdminHomePage.invoke()
                                } else {
                                    onNavToHomePage.invoke()
                                }
                            }
                        }
                    },
                    value = "Login",
                    fontSize = 18,
                    color = Color.White
                )

                DividerTextComponent()

                Row {
                    Button(
                        modifier = Modifier.size(50.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = "Google",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.size(20.dp))

                    Button(
                        modifier = Modifier.size(50.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(Color.Transparent),
                        onClick = {}
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = "Facebook",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MinText(value = "Don't have an account?", fontSize = 18, color = Color.White)

                    TextButton(
                        onClick = {
                            onNavToSignUpPage.invoke()
                        },
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        MinText(value = "Register", fontSize = 18, color = Color(255, 130, 71))
                    }
                }
            }
        }
    }

    if(loginUiState?.isLoading == true) {
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
                }
            }
        }
    }
}