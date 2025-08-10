package com.example.fridgebuddy.database.repository

import com.example.fridgebuddy.database.api.SpoonacularApi
import com.example.fridgebuddy.database.mapper.RecipeMapper
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.model.DispensaItem
import kotlinx.coroutines.Dispatchers
import com.example.fridgebuddy.BuildConfig
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val spoonacularApi: SpoonacularApi,
    private val apiKey: String = BuildConfig.SPOONACULAR_API_KEY
) {

    // Cerca ricette basandosi sugli ingredienti della dispensa
    suspend fun searchRecipesByDispensaItems(
        dispensaItems: List<DispensaItem>
    ): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            // Estrai i nomi degli ingredienti
            val ingredientNames = dispensaItems.map { it.nomeIngrediente }
            val ingredientsString = ingredientNames.joinToString(",") { it.trim() }

            // Chiama Spoonacular API
            val response = spoonacularApi.findRecipesByIngredients(
                apiKey = apiKey,
                ingredients = ingredientsString,
                number = 20,
                ranking = 1
            )

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Errore API: ${response.code()}")
                )
            }

            val simpleRecipes = response.body() ?: emptyList()

            // Per ogni ricetta trovata, recupera i dettagli
            val detailJobs = simpleRecipes.map { simpleRecipe ->
                async {
                    try {
                        val detailResponse = spoonacularApi.getRecipeDetails(
                            recipeId = simpleRecipe.id,
                            apiKey = apiKey
                        )

                        if (detailResponse.isSuccessful && detailResponse.body() != null) {
                            RecipeMapper.mapToRecipe(detailResponse.body()!!)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            val recipes = detailJobs.awaitAll().filterNotNull()
            Result.success(recipes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ottieni una ricetta specifica per ID
    suspend fun getRecipeById(recipeId: Int): Result<Recipe> = withContext(Dispatchers.IO) {
        try {
            val response = spoonacularApi.getRecipeDetails(
                recipeId = recipeId,
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body() != null) {
                val recipe = RecipeMapper.mapToRecipe(response.body()!!)
                Result.success(recipe)
            } else {
                Result.failure(Exception("Ricetta non trovata"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Ottieni ricette random per scoprire nuove idee
    suspend fun getRandomRecipes(count: Int = 5): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val response = spoonacularApi.getRandomRecipes(
                apiKey = apiKey,
                number = count
            )

            if (response.isSuccessful && response.body() != null) {
                val recipes = response.body()!!.recipes.map {
                    RecipeMapper.mapToRecipe(it)
                }
                Result.success(recipes)
            } else {
                Result.failure(Exception("Errore nel recupero ricette"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Cerca ricette per query testuale
    suspend fun searchRecipesByQuery(
        query: String,
        cuisine: String? = null,
        diet: String? = null,
        maxReadyTime: Int? = null
    ): Result<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val response = spoonacularApi.searchRecipes(
                apiKey = apiKey,
                query = query,
                cuisine = cuisine,
                diet = diet,
                maxReadyTime = maxReadyTime,
                number = 20
            )

            if (!response.isSuccessful || response.body() == null) {
                return@withContext Result.failure(Exception("Errore nella ricerca"))
            }

            val searchResults = response.body()!!.results

            // Recupera i dettagli per ogni ricetta
            val detailJobs = searchResults.map { result ->
                async {
                    try {
                        val detailResponse = spoonacularApi.getRecipeDetails(
                            recipeId = result.id,
                            apiKey = apiKey
                        )

                        if (detailResponse.isSuccessful && detailResponse.body() != null) {
                            RecipeMapper.mapToRecipe(detailResponse.body()!!)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            val recipes = detailJobs.awaitAll().filterNotNull()
            Result.success(recipes)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Filtra ricette localmente per categoria
    fun filterRecipesByCategory(recipes: List<Recipe>, categoria: String): List<Recipe> {
        return recipes.filter { it.categoria.equals(categoria, ignoreCase = true) }
    }

    // Filtra ricette localmente per difficoltà
    fun filterRecipesByDifficulty(recipes: List<Recipe>, difficolta: String): List<Recipe> {
        return recipes.filter { it.difficolta.equals(difficolta, ignoreCase = true) }
    }

    // Filtra ricette localmente per tempo di preparazione
    fun filterRecipesByTime(recipes: List<Recipe>, maxMinutes: Int): List<Recipe> {
        return recipes.filter { recipe ->
            val minutes = recipe.tempoPrep.filter { it.isDigit() }.toIntOrNull() ?: Int.MAX_VALUE
            minutes <= maxMinutes
        }
    }
}