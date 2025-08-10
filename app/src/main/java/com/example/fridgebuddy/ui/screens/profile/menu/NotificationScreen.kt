package com.example.fridgebuddy.ui.screens.profile.menu

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.composables.BottomNavigationBar
import com.example.fridgebuddy.ui.screens.profile.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun NotificationsScreen(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Initialize with current values from state
    var expiryEnabled by remember(uiState.notificationSettings) {
        mutableStateOf(uiState.notificationSettings.expiryEnabled)
    }
    var expiryDays by remember(uiState.notificationSettings) {
        mutableFloatStateOf(uiState.notificationSettings.expiryDays.toFloat())
    }
    var recipeEnabled by remember(uiState.notificationSettings) {
        mutableStateOf(uiState.notificationSettings.recipeEnabled)
    }
    var shoppingEnabled by remember(uiState.notificationSettings) {
        mutableStateOf(uiState.notificationSettings.shoppingEnabled)
    }

    var hasChanges by remember { mutableStateOf(false) }

    // Update local state when data loads from Firebase
    LaunchedEffect(uiState.notificationSettings) {
        expiryEnabled = uiState.notificationSettings.expiryEnabled
        expiryDays = uiState.notificationSettings.expiryDays.toFloat()
        recipeEnabled = uiState.notificationSettings.recipeEnabled
        shoppingEnabled = uiState.notificationSettings.shoppingEnabled
    }

    // Check for changes
    LaunchedEffect(expiryEnabled, expiryDays, recipeEnabled, shoppingEnabled) {
        hasChanges = expiryEnabled != uiState.notificationSettings.expiryEnabled ||
                expiryDays.roundToInt() != uiState.notificationSettings.expiryDays ||
                recipeEnabled != uiState.notificationSettings.recipeEnabled ||
                shoppingEnabled != uiState.notificationSettings.shoppingEnabled
    }

    // Handle success message
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Settings saved successfully!")
            viewModel.clearUpdateSuccess()
            hasChanges = false
        }
    }

    // Handle error messages
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF06D6A0))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            // Save changes if there are any before navigating back
                            if (hasChanges) {
                                viewModel.updateNotificationSettings(
                                    expiryEnabled = expiryEnabled,
                                    expiryDays = expiryDays.roundToInt(),
                                    recipeEnabled = recipeEnabled,
                                    shoppingEnabled = shoppingEnabled
                                )
                            }
                            navController.popBackStack()
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color(0xFF06D6A0),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.bell),
                        contentDescription = "Notifications icon",
                        modifier = Modifier
                            .size(48.dp)
                            .offset(y = (-4).dp)
                    )

                    Text(
                        text = "Notifications",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF06D6A0),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Manage your notification preferences",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Expiry Alerts Card
                    NotificationCard(
                        title = "Expiry Alerts",
                        description = "Get notified when ingredients are about to expire",
                        iconRes = R.drawable.bell,
                        iconTint = Color(0xFFFFB347),
                        isEnabled = expiryEnabled,
                        onToggle = { expiryEnabled = it },
                        additionalContent = {
                            if (expiryEnabled) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                        .animateContentSize()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Alert before",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "${expiryDays.roundToInt()} days",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF06D6A0)
                                        )
                                    }

                                    Slider(
                                        value = expiryDays,
                                        onValueChange = { expiryDays = it },
                                        valueRange = 1f..7f,
                                        steps = 5,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFF06D6A0),
                                            activeTrackColor = Color(0xFF06D6A0),
                                            inactiveTrackColor = Color(0xFF06D6A0).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Recipe Suggestions Card
                    NotificationCard(
                        title = "Recipe Suggestions",
                        description = "Daily recipe recommendations based on your ingredients",
                        iconRes = R.drawable.completed,
                        iconTint = Color(0xFF06D6A0),
                        isEnabled = recipeEnabled,
                        onToggle = { recipeEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Shopping Reminders Card
                    NotificationCard(
                        title = "Shopping Reminders",
                        description = "Remind you to buy ingredients when running low",
                        iconRes = R.drawable.storage,
                        iconTint = Color(0xFF9B59B6),
                        isEnabled = shoppingEnabled,
                        onToggle = { shoppingEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Save button - only show when there are changes
                    if (hasChanges) {
                        Button(
                            onClick = {
                                viewModel.updateNotificationSettings(
                                    expiryEnabled = expiryEnabled,
                                    expiryDays = expiryDays.roundToInt(),
                                    recipeEnabled = recipeEnabled,
                                    shoppingEnabled = shoppingEnabled
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF06D6A0)
                            )
                        ) {
                            Text(
                                text = "Save Changes",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF06D6A0).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFF06D6A0).copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF06D6A0).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "i",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF06D6A0)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Notification Tips",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Enable notifications to make the most of FridgeBuddy. We'll help you reduce food waste and discover new recipes!",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    title: String,
    description: String,
    iconRes: Int,
    iconTint: Color,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    additionalContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isEnabled) Color(0xFF06D6A0).copy(alpha = 0.5f)
            else Color.Gray.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isEnabled) 4.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF06D6A0),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                        uncheckedBorderColor = Color.Transparent
                    )
                )
            }

            additionalContent?.invoke()
        }
    }
}