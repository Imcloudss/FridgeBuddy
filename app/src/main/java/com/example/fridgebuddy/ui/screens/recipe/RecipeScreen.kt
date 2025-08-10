package com.example.fridgebuddy.ui.screens.recipe

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fridgebuddy.R
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.ui.composables.BottomNavigationBar
import com.example.fridgebuddy.ui.theme.FridgeBuddyRoute
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@Composable
fun RecipeScreen(
    navController: NavController,
    dispensaItems: List<DispensaItem>,
    viewModel: RecipeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dispensaItems) {
        if (dispensaItems.isNotEmpty()) {
            viewModel.searchRecipesByIngredients(dispensaItems)
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0xFFF8FFE5),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF06D6A0)
                    )
                }

                uiState.error != null -> {
                    ErrorMessage(
                        error = uiState.error!!,
                        onRetry = { viewModel.getRandomRecipes() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.recipes.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🥘",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (dispensaItems.isEmpty())
                                "Add ingredients to your pantry for seeing recipes!"
                            else
                                "No recipes found!",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }
                }

                else -> {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.receipe),
                                contentDescription = "Recipe icon",
                                modifier = Modifier.size(40.dp)
                            )

                            Text(
                                text = "Recipes",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06D6A0),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }

                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(uiState.recipes) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = {
                                        val recipeJson = Json.encodeToString(recipe)
                                        val encodedJson = URLEncoder.encode(recipeJson, "UTF-8")
                                        navController.navigate(FridgeBuddyRoute.Details(encodedJson))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color(0xFF06D6A0)
        )
    ) {
        Column {
            if (recipe.recipeImg.isNotEmpty()) {
                AsyncImage(
                    model = recipe.recipeImg,
                    contentDescription = recipe.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop,
                    onError = {
                        println("Loading error: ${it.result.throwable}")
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🍽️",
                        fontSize = 48.sp
                    )
                    Text(
                        text = "No picture available",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = recipe.nome,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = recipe.desc,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoChip(
                        label = recipe.categoria,
                        emoji = "🍽️"
                    )
                    InfoChip(
                        label = recipe.tempoPrep,
                        emoji = "⏱️"
                    )
                    InfoChip(
                        label = recipe.difficolta,
                        emoji = "👨‍🍳"
                    )
                }
            }
        }
    }
}

@Composable
fun InfoChip(
    label: String,
    emoji: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
fun ErrorMessage(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF06D6A0)
            )
        ) {
            Text("Retry")
        }
    }
}