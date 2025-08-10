package com.example.fridgebuddy.ui.screens.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.api.RetrofitClient
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    private val repository = RecipeRepository(
        spoonacularApi = RetrofitClient.spoonacularApi
    )

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    fun setSelectedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }

    // Cerca ricette con query testuale
    fun searchRecipes(query: String, filters: RecipeFilters? = null) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.searchRecipesByQuery(
                query = query,
                cuisine = filters?.category,
                maxReadyTime = filters?.maxTime
            ).onSuccess { recipes ->
                var filteredRecipes = recipes

                // Applica filtro difficoltà localmente
                filters?.difficulty?.let { difficulty ->
                    filteredRecipes = repository.filterRecipesByDifficulty(recipes, difficulty)
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recipes = filteredRecipes,
                    currentSearch = query
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message
                )
            }
        }
    }

    // Cerca ricette basate sugli ingredienti della dispensa
    fun searchRecipesByIngredients(dispensaItems: List<DispensaItem>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.searchRecipesByDispensaItems(dispensaItems)
                .onSuccess { recipes ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        recipes = recipes
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    // Ottieni ricette casuali
    fun getRandomRecipes() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getRandomRecipes(count = 10)
                .onSuccess { recipes ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        recipes = recipes
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class RecipeUiState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val currentSearch: String = ""
)

data class RecipeFilters(
    val category: String? = null,
    val maxTime: Int? = null,
    val difficulty: String? = null
)