package com.example.fridgebuddy.ui.screens.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.api.RetrofitClient
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.repository.DispensaRepository
import com.example.fridgebuddy.database.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeUiState(
    val ingredientiInScadenza: List<DispensaItem> = emptyList(),
    val ricetteSuggerite: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingRecipes: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val pantryRepository: DispensaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val recipeRepository = RecipeRepository(
        spoonacularApi = RetrofitClient.spoonacularApi
    )

    init {
        refreshData()
    }

    fun refreshData() {
        loadExpiringIngredients()
    }

    private fun loadExpiringIngredients() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid

            if (userId == null) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Unauthenticated user",
                        isLoading = false
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            pantryRepository.getItemsInScadenza(userId, giorni = 7)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            errorMessage = "Error in loading: ${e.message}",
                            isLoading = false
                        )
                    }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(
                            ingredientiInScadenza = items,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    // Cerca ricette basate sugli ingredienti in scadenza
                    if (items.isNotEmpty()) {
                        searchRecipesForExpiringItems(items)
                    }
                }
        }
    }

    private fun searchRecipesForExpiringItems(expiringItems: List<DispensaItem>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRecipes = true) }

            // Prendi solo i primi 5 ingredienti in scadenza per non fare troppe richieste
            val itemsForSearch = expiringItems.take(5)

            recipeRepository.searchRecipesByDispensaItems(itemsForSearch)
                .onSuccess { recipes ->
                    // Prendi solo le prime 5 ricette
                    val topRecipes = recipes.take(5)
                    _uiState.update {
                        it.copy(
                            ricetteSuggerite = topRecipes,
                            isLoadingRecipes = false
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingRecipes = false,
                            errorMessage = "Errore nel caricamento ricette: ${error.message}"
                        )
                    }
                }
        }
    }

    fun rimuoviErrore() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

fun DispensaItem.giorniAllaScadenza(): Long {
    return try {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
        val dataScadenza = dateFormat.parse(this.dataScadenza)
        val oggi = Calendar.getInstance()
        oggi.set(Calendar.HOUR_OF_DAY, 0)
        oggi.set(Calendar.MINUTE, 0)
        oggi.set(Calendar.SECOND, 0)
        oggi.set(Calendar.MILLISECOND, 0)

        if (dataScadenza != null) {
            val calScadenza = Calendar.getInstance()
            calScadenza.time = dataScadenza
            calScadenza.set(Calendar.HOUR_OF_DAY, 0)
            calScadenza.set(Calendar.MINUTE, 0)
            calScadenza.set(Calendar.SECOND, 0)
            calScadenza.set(Calendar.MILLISECOND, 0)

            val diffInMillis = calScadenza.timeInMillis - oggi.timeInMillis
            val giorni = diffInMillis / (1000 * 60 * 60 * 24)
            giorni
        } else {
            -1L
        }
    } catch (e: Exception) {
        -1L
    }
}

fun DispensaItem.coloreScadenza(): Color {
    val giorni = giorniAllaScadenza()
    return when {
        giorni < 0 -> Color.Red // Already expired
        giorni == 0L -> Color(0xFFFF4444) // Expires today
        giorni <= 2 -> Color(0xFFFF6B6B) // Expires very soon
        giorni <= 5 -> Color(0xFFFFD93D) // Expires in a few days
        giorni <= 7 -> Color(0xFF6BCF7F) // Expires in a week
        else -> Color(0xFF06D6A0)
    }
}

fun DispensaItem.testoScadenza(): String {
    val days = giorniAllaScadenza()
    return when {
        days < 0 -> "Expired"
        days == 0L -> "Expires today!"
        days == 1L -> "Expires tomorrow"
        days <= 7 -> "Expires in $days days"
        else -> dataScadenza
    }
}