package com.example.fridgebuddy.ui.utils

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun SimpleLottieAnimation(
    animationUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    iterations: Int = LottieConstants.IterateForever,
    speed: Float = 1f
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url(animationUrl)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        speed = speed
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size)
    )
}

// URL delle animazioni
object AnimationUrls {
    const val SPLASH_ANIMATION =
        "https://lottie.host/5c8e8d59-3f48-4c44-bc0b-2a3c409423a5/cytDuZQZYq.lottie"
    const val SIGN_IN_ANIMATION =
        "https://lottie.host/ef8c3090-3b50-4d34-b379-a380fef70b41/XmJnU6Hlm8.lottie"
    const val SIGN_UP_ANIMATION =
        "https://lottie.host/580c8eaf-3b6e-416c-8560-3a28f4c4e260/P4OMZ6am1z.lottie"
    const val ADD_ANIMATION =
        "https://lottie.host/51304551-06f5-4982-9412-b0637612987a/q4K87qDNQI.lottie"
    const val EDIT_PROFILE_ANIMATION =
        "https://lottie.host/7ba2dc07-5e70-4804-b07f-d71d20d9337c/lkLHx07C4Z.lottie"
}