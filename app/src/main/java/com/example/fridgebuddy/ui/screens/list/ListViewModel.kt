package com.example.fridgebuddy.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.repository.DispensaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ListUiState(
    val items: List<DispensaItem> = emptyList(),
    val filteredItems: List<DispensaItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val deletingItemId: String? = null
)

class ListViewModel(
    private val pantryRepository: DispensaRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadItems()

        // Osserva i cambiamenti nella query di ricerca
        viewModelScope.launch {
            _searchQuery.collect { query ->
                filterItems(query)
            }
        }
    }

    private fun loadItems() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User not logged in"
            )
            return
        }

        viewModelScope.launch {
            try {
                pantryRepository.getDispensaItems(userId).collect { items ->
                    _uiState.value = _uiState.value.copy(
                        items = items,
                        filteredItems = filterItemsByQuery(items, _searchQuery.value),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    private fun filterItems(query: String) {
        _uiState.value = _uiState.value.copy(
            filteredItems = filterItemsByQuery(_uiState.value.items, query)
        )
    }

    private fun filterItemsByQuery(items: List<DispensaItem>, query: String): List<DispensaItem> {
        if (query.isBlank()) return items

        val lowerCaseQuery = query.lowercase()
        return items.filter { item ->
            item.nomeIngrediente.lowercase().contains(lowerCaseQuery)
        }
    }

    fun deleteItem(itemId: String) {
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(deletingItemId = itemId)

            pantryRepository.deleteItem(userId, itemId).fold(
                onSuccess = {
                    // L'item sarà rimosso automaticamente dal Flow
                    _uiState.value = _uiState.value.copy(deletingItemId = null)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        deletingItemId = null,
                        errorMessage = "Errore nell'eliminazione: ${error.message}"
                    )
                }
            )
        }
    }

    fun formatQuantity(item: DispensaItem): String {
        return if (item.quantita.numero > 0) {
            "${item.quantita.numero} ${item.quantita.unita}".trim()
        } else {
            ""
        }
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        loadItems()
    }
}