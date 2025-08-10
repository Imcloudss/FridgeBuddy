package com.example.fridgebuddy.database.api

import com.example.fridgebuddy.database.model.SpoonacularRecipeDetail
import com.example.fridgebuddy.database.model.SpoonacularRecipeSimple
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    @GET("recipes/findByIngredients")
    suspend fun findRecipesByIngredients(
        @Query("apiKey") apiKey: String,
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 10,
        @Query("ranking") ranking: Int = 1,
        @Query("ignorePantry") ignorePantry: Boolean = true
    ): Response<List<SpoonacularRecipeSimple>>

    @GET("recipes/{id}/information")
    suspend fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = false
    ): Response<SpoonacularRecipeDetail>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @Query("query") query: String,
        @Query("cuisine") cuisine: String? = null,
        @Query("diet") diet: String? = null,
        @Query("maxReadyTime") maxReadyTime: Int? = null,
        @Query("number") number: Int = 10
    ): Response<SearchResponse>

    @GET("recipes/random")
    suspend fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int = 1
    ): Response<RandomRecipeResponse>
}

data class SearchResponse(
    val results: List<SpoonacularRecipeSimple>,
    val totalResults: Int
)

data class RandomRecipeResponse(
    val recipes: List<SpoonacularRecipeDetail>
)