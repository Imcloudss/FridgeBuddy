package com.example.fridgebuddy.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fridgebuddy.ui.utils.AnimationUrls
import com.example.fridgebuddy.ui.utils.SimpleLottieAnimation
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute

@Composable
fun SplashScreen(
    navController: NavController
) {
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
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(
                text = "FridgeBuddy",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF06D6A0),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Subtitle
            Text(
                text = "Your smart fridge assistant",
                fontSize = 20.sp,
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 15.dp)
            )

            // Animation container with background
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(450.dp),
                contentAlignment = Alignment.Center
            ) {
                SimpleLottieAnimation(
                    animationUrl = AnimationUrls.SPLASH_ANIMATION,
                    size = 250.dp,
                    speed = 1.5f
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Get Started button
            Button(
                onClick = { navController.navigate(FridgeBuddyRoute.SignIn) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF06D6A0)
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFF8FFE5)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}