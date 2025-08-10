package com.example.fridgebuddy.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fridgebuddy.R
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.ui.composables.BottomNavigationBar
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import org.koin.androidx.compose.koinViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    // Show errors
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackBarHostState.showSnackbar(message)
            viewModel.rimuoviErrore()
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFF8FFE5),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Sezione ingredienti in scadenza
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.garbage),
                            contentDescription = "Calendar icon",
                            modifier = Modifier
                                .size(65.dp)
                                .padding(end = 15.dp)
                        )

                        Text(
                            text = "Expiring ingredients",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color(0xFF06D6A0),
                        )
                    }

                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(230.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFF06D6A0))
                            }
                        }
                        uiState.ingredientiInScadenza.isEmpty() -> {
                            EmptyStateCard(
                                text = "Nessun ingrediente in scadenza nei prossimi 7 giorni!",
                                modifier = Modifier.height(230.dp)
                            )
                        }
                        else -> {
                            ExpiringItemsRow(items = uiState.ingredientiInScadenza)
                        }
                    }
                }
            }

            // Sezione ricette suggerite
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                        .padding(top = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.suggestions),
                            contentDescription = "Suggestions icon",
                            modifier = Modifier
                                .size(65.dp)
                                .padding(end = 15.dp)
                        )

                        Text(
                            text = "Suggested recipes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = Color(0xFF06D6A0),
                        )
                    }
                }
            }

            // Lista ricette
            when {
                uiState.isLoadingRecipes -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 25.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF06D6A0))
                        }
                    }
                }
                uiState.ricetteSuggerite.isEmpty() -> {
                    item {
                        EmptyStateCard(
                            text = if (uiState.ingredientiInScadenza.isEmpty())
                                "Aggiungi ingredienti per vedere ricette suggerite"
                            else
                                "Nessuna ricetta trovata per gli ingredienti in scadenza",
                            modifier = Modifier
                                .height(150.dp)
                                .padding(horizontal = 25.dp)
                        )
                    }
                }
                else -> {
                    items(uiState.ricetteSuggerite) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = {
                                val recipeJson = Json.encodeToString(recipe)
                                val encodedJson = URLEncoder.encode(recipeJson, "UTF-8")
                                navController.navigate(FridgeBuddyRoute.Details(encodedJson))
                            },
                            modifier = Modifier.padding(horizontal = 25.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpiringItemsRow(items: List<DispensaItem>) {
    LazyRow(
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(items) { item ->
            ItemCard(item = item)
        }
    }
}

@Composable
fun ItemCard(item: DispensaItem) {
    val borderColor = item.coloreScadenza()
    val expirationText = item.testoScadenza()

    Column {
        Card(
            modifier = Modifier
                .size(185.dp)
                .padding(end = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8FFE5)
            ),
            border = BorderStroke(
                width = 2.dp,
                color = borderColor
            )
        ) {
            AsyncImage(
                model = item.imgUrl,
                contentDescription = item.nomeIngrediente,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = item.nomeIngrediente,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 8.dp, start = 5.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = expirationText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = borderColor,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 5.dp),
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FFE5)
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color(0xFF06D6A0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(95.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                if (recipe.recipeImg.isNotEmpty()) {
                    AsyncImage(
                        model = recipe.recipeImg,
                        contentDescription = recipe.nome,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🍽️",
                            fontSize = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = recipe.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoChip(
                        text = recipe.tempoPrep,
                        icon = "⏱️"
                    )
                    InfoChip(
                        text = recipe.difficolta,
                        icon = "📊"
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    text: String,
    icon: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF06D6A0).copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                color = Color(0xFF06D6A0),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun EmptyStateCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF06D6A0).copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📭",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}