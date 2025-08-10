package com.example.fridgebuddy.ui.screens.sign

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.model.User
import com.example.fridgebuddy.database.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {

        when {
            !email.contains("@") || email.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please enter a valid email"
                )
                return
            }
            password.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Password cannot be empty"
                )
                return
            }
        }

        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Starting login process")
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                val result = authRepository.login(email, password)

                result.fold(
                    onSuccess = { firebaseUser ->
                        Log.d("AuthViewModel", "Login successful, UID: ${firebaseUser.uid}")

                        // Recupera i dati dell'utente
                        val userDataResult = authRepository.getUserData(firebaseUser.uid)

                        userDataResult.fold(
                            onSuccess = { user ->
                                Log.d("AuthViewModel", "User data retrieved: ${user.username}")
                                _uiState.value = AuthUiState(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    currentUser = user,
                                    errorMessage = null
                                )
                            },
                            onFailure = { error ->
                                Log.e("AuthViewModel", "Failed to get user data: ${error.message}")
                                // Anche se non riusciamo a recuperare i dati, l'utente è comunque loggato
                                _uiState.value = AuthUiState(
                                    isLoading = false,
                                    isLoggedIn = true,
                                    currentUser = null,
                                    errorMessage = null
                                )
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e("AuthViewModel", "Login failed: ${exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = getErrorMessage(exception)
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Unexpected error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "An unexpected error occurred"
                )
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        // Validazione input
        when {
            username.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Username cannot be empty"
                )
                return
            }
            username.length < 3 -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Username must be at least 3 characters"
                )
                return
            }
            !email.contains("@") || email.isBlank() -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Please enter a valid email"
                )
                return
            }
            password.length < 6 -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Password must be at least 6 characters"
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            authRepository.register(email, password, username).fold(
                onSuccess = { firebaseUser ->
                    // Recupera i dati dell'utente appena creato
                    authRepository.getUserData(firebaseUser.uid).fold(
                        onSuccess = { user ->
                            _uiState.value = AuthUiState(
                                isLoggedIn = true,
                                currentUser = user,
                                isRegistrationSuccessful = true
                            )
                        },
                        onFailure = {
                            _uiState.value = AuthUiState(
                                isLoggedIn = true,
                                isRegistrationSuccessful = true
                            )
                        }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = getErrorMessage(exception)
                    )
                }
            )
        }
    }

    private fun getErrorMessage(exception: Throwable): String {
        return when {
            exception.message?.contains("email address is already in use") == true ->
                "This email is already registered"
            exception.message?.contains("no user record") == true ->
                "No account found with this email"
            exception.message?.contains("password is invalid") == true ->
                "Incorrect password"
            exception.message?.contains("email address is badly formatted") == true ->
                "Invalid email format"
            exception.message?.contains("network") == true ->
                "Network error. Please check your connection"
            exception.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                "Invalid email or password"
            exception.message?.contains("too-many-requests") == true ->
                "Too many failed attempts. Please try again later"
            else -> "An error occurred: ${exception.message}"
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetLoginState() {
        _uiState.value = AuthUiState()
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Password reset email sent successfully"
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to send reset email: ${exception.message}"
                    )
                }
            )
        }
    }
}