package com.example.fridgebuddy.ui.screens.list.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fridgebuddy.database.model.Categoria
import com.example.fridgebuddy.database.model.DispensaItem
import com.example.fridgebuddy.database.model.FoodItem
import com.example.fridgebuddy.database.model.QuantitaItem
import com.example.fridgebuddy.database.repository.CategorieRepository
import com.example.fridgebuddy.database.repository.DispensaRepository
import com.example.fridgebuddy.database.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AddIngredientViewModel(
    private val foodRepository: FoodRepository,
    private val dispensaRepository: DispensaRepository,
    private val categorieRepository: CategorieRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())

    val filteredFoodItems: StateFlow<List<FoodItem>> = combine(
        _foodItems,
        _searchQuery
    ) { items, query ->
        when {
            query.isBlank() -> emptyList()
            else -> items
                .filter { it.title.contains(query, ignoreCase = true) }
                .take(5)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedFoodItem = MutableStateFlow<FoodItem?>(null)
    val selectedFoodItem: StateFlow<FoodItem?> = _selectedFoodItem.asStateFlow()

    private val _categorie = MutableStateFlow<List<Categoria>>(emptyList())
    val categorie: StateFlow<List<Categoria>> = _categorie.asStateFlow()

    private val _selectedCategoria = MutableStateFlow("")
    val selectedCategoria: StateFlow<String> = _selectedCategoria.asStateFlow()

    private val _addResult = MutableStateFlow<Result<String>?>(null)
    val addResult: StateFlow<Result<String>?> = _addResult.asStateFlow()

    init {
        loadFoodItems()
        loadCategorie()
    }

    private fun loadFoodItems() {
        viewModelScope.launch {
            foodRepository.getAllFoodItems()
                .catch {
                    _foodItems.value = emptyList()
                }
                .collect { items ->
                    _foodItems.value = items
                }
        }
    }

    private fun loadCategorie() {
        viewModelScope.launch {
            categorieRepository.getAllCategorie()
                .onSuccess { list ->
                    _categorie.value = list
                }
                .onFailure {
                    _categorie.value = emptyList()
                }
        }
    }

    fun onSearchChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFoodItemSelected(item: FoodItem) {
        _selectedFoodItem.value = item
        _searchQuery.value = item.title
    }

    fun onCategoriaSelected(categoriaId: String) {
        _selectedCategoria.value = categoriaId
    }

    fun addDispensaItem(userId: String, item: DispensaItem) {
        viewModelScope.launch {
            // Reset of the previous result
            _addResult.value = null

            if (item.nomeIngrediente.isBlank()) {
                _addResult.value = Result.failure(Exception("Ingredient name cannot be empty"))
                return@launch
            }

            if (item.quantita.numero <= 0) {
                _addResult.value = Result.failure(Exception("Quantity must be greater than 0"))
                return@launch
            }

            _addResult.value = dispensaRepository.addItem(userId, item)
        }
    }

    // Helper method to create a complete DispensaItem
    fun createDispensaItem(
        nomeIngrediente: String,
        quantita: Int,
        unita: String,
        dataScadenza: String
    ): DispensaItem? {
        val food = _selectedFoodItem.value
        val categoriaId = _selectedCategoria.value

        return DispensaItem(
            idIngrediente = food?.id ?: return null,
            nomeIngrediente = food.title.ifBlank { nomeIngrediente },
            idCategoria = categoriaId,
            quantita = QuantitaItem(
                numero = quantita,
                unita = unita
            ),
            dataScadenza = dataScadenza,
            imgUrl = food.imgUrl,
            createdAt = System.currentTimeMillis().toString()
        )
    }
}