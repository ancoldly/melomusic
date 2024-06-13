package com.example.myapplication.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun RegisterScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage:() -> Unit,
    onNavToLoginPage:() -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.signUpError != null
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
                    value = "Register Account", color = Color.White
                )

                Spacer(modifier = Modifier.size(100.dp))

                if(isError) {
                    Text(text = loginUiState?.signUpError ?: "Unknown error",
                        color = Color.Red,
                    )
                }

                TextFieldComponent(
                    isError = isError,
                    value = loginUiState?.userNameSignUp ?: "",
                    placeholderValue = "Enter your email",
                    leadingIcon = R.drawable.mail,
                    contentDescription = "Email",
                    onTextChanged = {
                        loginViewModel?.onUserNameChangeSignUp(it)
                    }
                )

                Spacer(modifier = Modifier.size(20.dp))

                PasswordFieldComponent(
                    isError = isError,
                    placeholderValue = "Enter your password",
                    value = loginUiState?.passwordSignUp ?: "",
                    onTextChanged = {
                        loginViewModel?.onPasswordChangeSignUp(it)
                    },
                )

                Spacer(modifier = Modifier.size(20.dp))

                com.example.myapplication.components.Button(
                    onClick = {
                        loginViewModel?.createUser(context)
                    },
                    value = "Register",
                    fontSize = 24,
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
                    MinText(value = "Have an account?", fontSize = 18, color = Color.White)

                    TextButton(
                        onClick = {
                            onNavToLoginPage.invoke()
                        },
                        modifier = Modifier.background(Color.Transparent)
                    ) {
                        MinText(value = "Login", fontSize = 18, color = Color(255, 130, 71))
                    }
                }
            }
        }
    }

    if(loginUiState?.isLoading == true) {
        CircularProgressIndicator()
    }

    LaunchedEffect(key1 = loginViewModel?.hasUser) {
        if(loginViewModel?.hasUser == true) {
            onNavToHomePage.invoke()
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(onNavToHomePage = { }) {

    }
}