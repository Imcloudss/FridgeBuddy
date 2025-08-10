package com.example.fridgebuddy.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fridgebuddy.R
import com.example.fridgebuddy.ui.screens.recipe.RecipeFilters

@Composable
fun FilterSection(
    title: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(options) { option ->
                FilterChip(
                    text = option,
                    isSelected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) Color(0xFF06D6A0) else Color.White,
                shape = RoundedCornerShape(30.dp)
            )
            .border(
                width = 2.dp,
                color = Color(0xFF06D6A0),
                shape = RoundedCornerShape(30.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else Color(0xFF06D6A0)
        )
    }
}

@Composable
fun SearchRecipes(
    onSearch: (String, RecipeFilters) -> Unit
) {
    var searchRecipe by remember { mutableStateOf("") }
    var showFilters by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }
    var selectedDifficulty by remember { mutableStateOf<String?>(null) }

    // Mappa categorie inglese -> italiano per Spoonacular
    val categoryMap = mapOf(
        "Italiana" to "italian",
        "Asiatica" to "asian",
        "Messicana" to "mexican",
        "Dolce" to "dessert",
        "Salutare" to "healthy",
        "Veloce" to null // gestito con tempo
    )

    val categories = categoryMap.keys.toList()
    val prepTimes = listOf("< 15 min", "15-30 min", "30-60 min", "> 60 min")
    val difficulties = listOf("Facile", "Media", "Difficile")

    // Funzione per eseguire la ricerca
    val performSearch = {
        if (searchRecipe.isNotBlank()) {
            val filters = RecipeFilters(
                category = selectedCategory?.let { categoryMap[it] },
                maxTime = when(selectedTime) {
                    "< 15 min" -> 15
                    "15-30 min" -> 30
                    "30-60 min" -> 60
                    else -> null
                },
                difficulty = selectedDifficulty
            )
            onSearch(searchRecipe, filters)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchRecipe,
                onValueChange = { searchRecipe = it },
                placeholder = {
                    Text(
                        text = "Looking for a recipe?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Research icon",
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(35.dp)
                    )
                },
                trailingIcon = {
                    if (searchRecipe.isNotEmpty()) {
                        IconButton(onClick = { searchRecipe = "" }) {
                            Image(
                                painter = painterResource(id = R.drawable.x),
                                contentDescription = "Delete icon",
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .size(35.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.weight(1f),
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
                keyboardActions = KeyboardActions(
                    onDone = { performSearch() }
                )
            )

            // Search Button
            Spacer(modifier = Modifier.width(5.dp))

            // Filter Button
            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = { showFilters = !showFilters },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (showFilters) Color(0xFF06D6A0) else Color.White,
                        shape = RoundedCornerShape(50)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFF06D6A0),
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Filter Options
        AnimatedVisibility(visible = showFilters) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
            ) {
                // Categories
                FilterSection(
                    title = "Categoria",
                    options = categories,
                    selectedOption = selectedCategory,
                    onOptionSelected = { selectedCategory = if (selectedCategory == it) null else it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Prep Time
                FilterSection(
                    title = "Tempo di preparazione",
                    options = prepTimes,
                    selectedOption = selectedTime,
                    onOptionSelected = { selectedTime = if (selectedTime == it) null else it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Difficulty
                FilterSection(
                    title = "Difficoltà",
                    options = difficulties,
                    selectedOption = selectedDifficulty,
                    onOptionSelected = { selectedDifficulty = if (selectedDifficulty == it) null else it }
                )

                // Clear Filters Button
                if (selectedCategory != null || selectedTime != null || selectedDifficulty != null) {
                    Spacer(modifier = Modifier.height(15.dp))

                    TextButton(
                        onClick = {
                            selectedCategory = null
                            selectedTime = null
                            selectedDifficulty = null
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "Cancella tutti i filtri",
                            color = Color(0xFF06D6A0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}