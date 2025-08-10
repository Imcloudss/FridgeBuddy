package com.example.fridgebuddy.ui.screens.profile.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.composables.BottomNavigationBar
import com.example.fridgebuddy.ui.screens.profile.ProfileViewModel
import com.example.fridgebuddy.ui.utils.AnimationUrls
import com.example.fridgebuddy.ui.utils.SimpleLottieAnimation
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var username by remember(uiState.user) {
        mutableStateOf(uiState.user?.username ?: "")
    }
    var email by remember(uiState.user) {
        mutableStateOf(uiState.user?.email ?: "")
    }

    var isUsernameError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }
    var emailErrorMessage by remember { mutableStateOf("") }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Profile successfully updated!")
            viewModel.clearUpdateSuccess()
            kotlinx.coroutines.delay(1000)
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFF8FFE5),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.editprofile),
                    contentDescription = "Edit profile icon",
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = (-6).dp)
                )

                Text(
                    text = "Edit profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF06D6A0),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SimpleLottieAnimation(
                animationUrl = AnimationUrls.EDIT_PROFILE_ANIMATION,
                size = 250.dp,
                speed = 1.5f
            )

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    isUsernameError = it.isBlank() || it.length < 3
                },
                placeholder = {
                    Text(
                        text = "Username",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.chef),
                        contentDescription = "Chef icon",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp)
                    )
                },
                isError = isUsernameError,
                supportingText = if (isUsernameError) {
                    { Text("Username must be at least 3 characters long") }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                shape = RoundedCornerShape(60),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06D6A0),
                    unfocusedBorderColor = Color(0xFF06D6A0),
                    errorBorderColor = Color(0xFFFF6B6B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    val emailPattern = android.util.Patterns.EMAIL_ADDRESS
                    isEmailError = !emailPattern.matcher(it).matches()
                    emailErrorMessage = if (isEmailError) "Use a valid email" else ""
                },
                placeholder = {
                    Text(
                        text = "Email",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.email),
                        contentDescription = "Mail icon",
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp)
                    )
                },
                isError = isEmailError,
                supportingText = if (isEmailError) {
                    { Text(emailErrorMessage) }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                shape = RoundedCornerShape(60),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06D6A0),
                    unfocusedBorderColor = Color(0xFF06D6A0),
                    errorBorderColor = Color(0xFFFF6B6B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = Color(0xFF06D6A0),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                // Save button
                Button(
                    onClick = {
                        if (!isUsernameError && !isEmailError && username.isNotBlank() && email.isNotBlank()) {
                            viewModel.updateUserProfile(username, email)
                        }
                    },
                    enabled = !isUsernameError && !isEmailError &&
                            username.isNotBlank() && email.isNotBlank() &&
                            (username != uiState.user?.username || email != uiState.user?.email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(60),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF06D6A0),
                        disabledContainerColor = Color(0xFF06D6A0).copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "Save changes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(60),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF06D6A0)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}