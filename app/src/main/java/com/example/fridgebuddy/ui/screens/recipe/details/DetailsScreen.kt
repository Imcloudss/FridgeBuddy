package com.example.fridgebuddy.ui.screens.recipe.details

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fridgebuddy.R
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.ui.composables.DetailsTopBar
import com.example.fridgebuddy.ui.screens.profile.ProfileViewModel
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DetailsScreen(
    recipe: Recipe,
    navController: NavController,
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = recipe.passaggi.size
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showCompletionDialog by remember { mutableStateOf(false) }
    val isLastStep = currentStep == totalSteps - 1

    Scaffold(
        topBar = {
            DetailsTopBar(
                recipeName = recipe.nome,
                navController = navController
            )
        },
        containerColor = Color(0xFFF8FFE5)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                if (recipe.recipeImg.isNotEmpty()) {
                    AsyncImage(
                        model = recipe.recipeImg,
                        contentDescription = recipe.nome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avocado),
                        contentDescription = recipe.nome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Text(
                    text = recipe.nome,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                Text(
                    text = recipe.desc,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoChip(
                        icon = "⏱️",
                        label = "Time",
                        value = recipe.tempoPrep
                    )
                    InfoChip(
                        icon = "🍽️",
                        label = "Servings",
                        value = "4"
                    )
                    InfoChip(
                        icon = "📊",
                        label = "Difficulty",
                        value = recipe.difficolta
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ingredients",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF06D6A0),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        recipe.ingredienti.forEach { ingredient ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 16.sp,
                                    color = Color(0xFF06D6A0),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = ingredient,
                                    fontSize = 16.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Instructions",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06D6A0)
                            )
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isLastStep) Color(0xFFFF6B6B).copy(alpha = 0.1f)
                                else Color(0xFF06D6A0).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = if (isLastStep) "Final Step!" else "Step ${currentStep + 1} of $totalSteps",
                                    fontSize = 14.sp,
                                    color = if (isLastStep) Color(0xFFFF6B6B) else Color(0xFF06D6A0),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LinearProgressIndicator(
                            progress = { (currentStep + 1).toFloat() / totalSteps.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isLastStep) Color(0xFFFF6B6B) else Color(0xFF06D6A0),
                            trackColor = Color(0xFF06D6A0).copy(alpha = 0.2f),
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = recipe.passaggi.getOrNull(currentStep) ?: "",
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = Color.Black.copy(alpha = 0.8f)
                        )

                        val timerData = extractTimerData(recipe.passaggi.getOrNull(currentStep) ?: "")
                        if (timerData != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            TimerComponent(
                                totalSeconds = timerData,
                                onTimerComplete = {
                                    var playCount = 1
                                    val mediaPlayer = MediaPlayer.create(context, R.raw.alarm)

                                    mediaPlayer.setOnCompletionListener {
                                        if (playCount < 3) {
                                            playCount++
                                            mediaPlayer.seekTo(0)
                                            mediaPlayer.start()
                                        } else {
                                            mediaPlayer.release()
                                        }
                                    }

                                    mediaPlayer.start()
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (currentStep > 0) currentStep--
                        },
                        enabled = currentStep > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF06D6A0),
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Previous",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Previous", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    if (isLastStep) {
                        Button(
                            onClick = { showCompletionDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6B6B)
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Complete",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Complete", fontSize = 16.sp, color = Color.White)
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentStep < totalSteps - 1) currentStep++
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF06D6A0)
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text("Next", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Next",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCompletionDialog) {
        RecipeCompletionDialog(
            recipeName = recipe.nome,
            onConfirm = {
                coroutineScope.launch {
                    profileViewModel.incrementCompletedRecipes()
                    showCompletionDialog = false
                    navController.navigate(FridgeBuddyRoute.Recipe) {
                        popUpTo(FridgeBuddyRoute.Details("{recipe}")) { inclusive = true }
                    }
                }
            },
            onDismiss = { showCompletionDialog = false }
        )
    }
}

@Composable
fun RecipeCompletionDialog(
    recipeName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        icon = {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = "Complete",
                modifier = Modifier.size(48.dp),
                tint = Color(0xFFFF6B6B)
            )
        },
        title = {
            Text(
                text = "Recipe Completed!",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Congratulations! You've completed",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = recipeName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your recipe count will be updated!",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Not yet", color = Color.Gray)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Yes, I'm done!", color = Color.White)
            }
        }
    )
}

@Composable
fun InfoChip(
    icon: String,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFF06D6A0).copy(alpha = 0.1f),
            modifier = Modifier.size(56.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun TimerComponent(
    totalSeconds: Int,
    onTimerComplete: () -> Unit
) {
    var timeLeft by remember { mutableIntStateOf(totalSeconds) }
    var isRunning by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (totalSeconds > 0) {
            (totalSeconds - timeLeft).toFloat() / totalSeconds.toFloat()
        } else 0f,
        animationSpec = tween(1000), label = ""
    )

    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                isRunning = false
                onTimerComplete()
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF06D6A0).copy(alpha = 0.05f)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF06D6A0).copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.timer),
                    contentDescription = "Timer",
                    tint = Color(0xFF06D6A0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cooking Timer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF06D6A0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formatTime(timeLeft),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (timeLeft > 0) Color(0xFF06D6A0) else Color.Red
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF06D6A0),
                trackColor = Color.LightGray.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isRunning) {
                    FilledTonalButton(
                        onClick = { isRunning = true },
                        enabled = timeLeft > 0,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF06D6A0).copy(alpha = 0.2f),
                            contentColor = Color(0xFF06D6A0)
                        )
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Start")
                    }
                } else {
                    FilledTonalButton(
                        onClick = { isRunning = false },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color.Magenta.copy(alpha = 0.2f),
                            contentColor = Color.Magenta
                        )
                    ) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Pause")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        isRunning = false
                        timeLeft = totalSeconds
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = "Reset",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }
        }
    }
}

fun extractTimerData(text: String): Int? {
    val patterns = listOf(
        Regex("(\\d+)\\s*minutes?", RegexOption.IGNORE_CASE),
        Regex("(\\d+)\\s*mins?", RegexOption.IGNORE_CASE),
        Regex("(\\d+)\\s*hours?", RegexOption.IGNORE_CASE),
        Regex("(\\d+)\\s*hrs?", RegexOption.IGNORE_CASE)
    )

    patterns.forEach { pattern ->
        val match = pattern.find(text)
        if (match != null) {
            val value = match.groupValues[1].toIntOrNull() ?: return null
            return when {
                pattern.pattern.contains("hour", ignoreCase = true) -> value * 3600
                pattern.pattern.contains("hr", ignoreCase = true) -> value * 3600
                else -> value * 60
            }
        }
    }

    return null
}

@SuppressLint("DefaultLocale")
fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    return when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, secs)
        else -> String.format("%02d:%02d", minutes, secs)
    }
}