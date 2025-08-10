package com.example.fridgebuddy.ui.screens.sign

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgebuddy.ui.utils.AnimationUrls
import com.example.fridgebuddy.ui.utils.SimpleLottieAnimation
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel = koinViewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            navController.navigate(FridgeBuddyRoute.Home) {
                popUpTo(FridgeBuddyRoute.SignUp) { inclusive = true }
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
            Spacer(modifier = Modifier.fillMaxHeight(0.05f))

            SimpleLottieAnimation(
                animationUrl = AnimationUrls.SIGN_UP_ANIMATION,
                size = 300.dp,
                speed = 1.5f
            )

            Text(
                text = "A new face? A new buddy!",
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.07f))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it},
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
                        modifier = Modifier
                            .size(50.dp)
                            .padding(start = 20.dp),
                        contentDescription = "Chef icon"
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
                )
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it},
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
                )
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it},
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
                )
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

            Spacer(modifier = Modifier.fillMaxHeight(0.14f))

            Button(
                onClick = {
                    viewModel.signUp(username, email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 90.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06D6A0)
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFF8FFE5)
                    )
                } else {
                    Text(
                        text = "Sign up",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF8FFE5)
                    )
                }
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.05f))

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.Gray)) {
                        append("Already have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF06D6A0),
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                                    append("Sign in")
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(FridgeBuddyRoute.SignIn) },
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF06D6A0),
            )
        }
    }
}