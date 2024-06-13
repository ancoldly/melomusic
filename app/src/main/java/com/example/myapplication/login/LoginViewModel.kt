package com.example.myapplication.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _currentUser = repository.currentUser
    val currentUser: LiveData<FirebaseUser?> = _currentUser

    val hasUser: Boolean
        get() = repository.hasUser()

    var loginUiState by mutableStateOf(LoginUiState())
        private set

    fun onUserNameChange(userName: String) {
        loginUiState = loginUiState.copy(userName = userName)
    }

    fun onUserPasswordChange(password: String) {
        loginUiState = loginUiState.copy(password = password)
    }

    fun onUserNameChangeSignUp(userName: String) {
        loginUiState = loginUiState.copy(userNameSignUp = userName)
    }

    fun onPasswordChangeSignUp(password: String) {
        loginUiState = loginUiState.copy(passwordSignUp = password)
    }

    private fun validateLoginForm() =
        loginUiState.userName.isNotBlank() &&
                loginUiState.password.isNotBlank()

    private fun validateSignUpForm() =
        loginUiState.userNameSignUp.isNotBlank() &&
                loginUiState.passwordSignUp.isNotBlank()


    fun getCurrentUserEmail(): String? {
        return currentUser.value?.email
    }

    fun createUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateSignUpForm()) {
                throw IllegalArgumentException("Email and password can not be empty")
            }

            loginUiState = loginUiState.copy(isLoading = true)
            loginUiState = loginUiState.copy(signUpError = null)
            repository.createUser(
                loginUiState.userNameSignUp,
                loginUiState.passwordSignUp
            ) { isSuccessful ->
                if (isSuccessful) {
                    val userData = hashMapOf(
                        "email" to loginUiState.userNameSignUp,
                    )

                    val db = Firebase.database
                    val usersRef = db.reference.child("users")
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    userId?.let {
                        usersRef.child(userId).setValue(userData)
                    }

                    Toast.makeText(
                        context,
                        "Success Login",
                        Toast.LENGTH_SHORT
                    ).show()

                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                } else {
                    Toast.makeText(
                        context,
                        "Failed Login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(signUpError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }

        Log.d("LoginViewModel", "Current user after sign up: ${currentUser.value?.email}")
    }

    fun loginUser(context: Context) = viewModelScope.launch {
        try {
            if (!validateLoginForm()) {
                throw IllegalArgumentException("Email and password cannot be empty")
            }

            loginUiState = loginUiState.copy(isLoading = true)
            loginUiState = loginUiState.copy(loginError = null)
            repository.login(
                loginUiState.userName,
                loginUiState.password
            ) { isSuccessful ->
                if (isSuccessful) {
                    val db = Firebase.database
                    val usersRef = db.reference.child("users")
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    userId?.let {
                        usersRef.child(userId).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val snapshot = task.result
                                if (snapshot != null && snapshot.exists()) {
                                    Toast.makeText(
                                        context,
                                        "Success Login",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    loginUiState = loginUiState.copy(isSuccessLogin = true)
                                } else {
                                    if(loginUiState.userName == "adminmelo@gmail.com") {
                                        Toast.makeText(
                                            context,
                                            "Success Login",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        loginUiState = loginUiState.copy(isSuccessLogin = true)
                                    } else {
                                        loginUiState = loginUiState.copy(isSuccessLogin = false)
                                        FirebaseAuth.getInstance().signOut()
                                        Toast.makeText(
                                            context,
                                            "User not found",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Database error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Failed Login",
                        Toast.LENGTH_SHORT
                    ).show()
                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                }
            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(loginError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }

        Log.d("LoginViewModel", "Current user after login: ${currentUser.value?.email}")
    }

    fun logout(context: Context) = viewModelScope.launch {
        try {
            loginUiState = loginUiState.copy(isLoading = true)
            repository.logout { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(
                        context,
                        "Logged out successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    loginUiState = loginUiState.copy(isSuccessLogin = false)
                } else {
                    Toast.makeText(
                        context,
                        "Failed to logout",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            loginUiState = loginUiState.copy(logoutError = e.localizedMessage)
            e.printStackTrace()
        } finally {
            loginUiState = loginUiState.copy(isLoading = false)
        }

        Log.d("LoginViewModel", "Current user after logout: ${currentUser.value?.email}")
    }
}

data class LoginUiState(
    val userName: String = "",
    val password: String = "",
    val userNameSignUp: String = "",
    val passwordSignUp: String = "",

    val isLoading: Boolean = false,
    val isSuccessLogin: Boolean = false,
    val signUpError: String? = null,
    val loginError: String? = null,
    val logoutError: String? = null,
)
