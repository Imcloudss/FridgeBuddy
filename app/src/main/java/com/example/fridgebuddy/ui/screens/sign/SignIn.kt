package com.example.fridgebuddy.ui.screens.sign

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import com.example.fridgebuddy.ui.utils.AnimationUrls
import com.example.fridgebuddy.ui.utils.SimpleLottieAnimation
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResetPasswordDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Reset lo stato quando entriamo nella schermata
    DisposableEffect(Unit) {
        viewModel.resetLoginState()
        onDispose { }
    }

    // Monitora lo stato per il debug
    LaunchedEffect(uiState) {
        Log.d("SignInScreen", "UiState changed - isLoggedIn: ${uiState.isLoggedIn}, currentUser: ${uiState.currentUser?.username}, isLoading: ${uiState.isLoading}")
    }

    // Naviga quando il login ha successo
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn && !uiState.isRegistrationSuccessful) {
            Log.d("SignInScreen", "Navigating to Home")
            navController.navigate(FridgeBuddyRoute.Home) {
                popUpTo(FridgeBuddyRoute.SignIn) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FFE5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

            SimpleLottieAnimation(
                animationUrl = AnimationUrls.SIGN_IN_ANIMATION,
                size = 300.dp,
                speed = 1.5f
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.04f))

            Text(
                text = "Get back in there, buddy!",
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.05f))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.clearError()
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp),
                        contentDescription = "Email icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(60),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06D6A0),
                    unfocusedBorderColor = Color(0xFF06D6A0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.clearError()
                },
                placeholder = {
                    Text(
                        text = "Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.psw),
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp),
                        contentDescription = "Password icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(60),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF06D6A0),
                    unfocusedBorderColor = Color(0xFF06D6A0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF06D6A0)
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.errorMessage != null
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.01f))

            // Forgotten password link
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                        append("Forgotten password?")
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                modifier = Modifier
                    .clickable { showResetPasswordDialog = true }
                    .align(Alignment.CenterHorizontally)
            )

            // Error message
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 25.dp)
                )
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.08f))

            // Sign in button
            Button(
                onClick = {
                    Log.d("SignInScreen", "Sign in button clicked")
                    viewModel.signIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 90.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06D6A0)
                ),
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFF8FFE5)
                    )
                } else {
                    Text(
                        text = "Sign in",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF8FFE5)
                    )
                }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.05f))

            // Sign up link
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append("No account yet? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF06D6A0),
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Sign up")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(FridgeBuddyRoute.SignUp) },
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // Dialog per reset password
    if (showResetPasswordDialog) {
        ResetPasswordDialog(
            onDismiss = { showResetPasswordDialog = false },
            onConfirm = { email ->
                viewModel.resetPassword(email)
                showResetPasswordDialog = false
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun ResetPasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reset Password",
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter your email address and we'll send you a link to reset your password.",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        viewModel.clearError()
                    },
                    placeholder = {
                        Text(
                            text = "Email",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(60),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06D6A0),
                        unfocusedBorderColor = Color(0xFF06D6A0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    isError = uiState.errorMessage != null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(email) },
                enabled = email.contains("@")
            ) {
                Text(
                    "Send Reset Link",
                    color = Color(0xFF06D6A0),
                    fontWeight = FontWeight.Medium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = Color.Gray,
                )
            }
        },
        containerColor = Color(0xFFF8FFE5)
    )
}