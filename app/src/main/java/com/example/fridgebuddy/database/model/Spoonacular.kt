package com.example.fridgebuddy.database.model

// Risposta semplificata quando cerchi per ingredienti
data class SpoonacularRecipeSimple(
    val id: Int,
    val title: String,
    val image: String,
    val imageType: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int,
    val missedIngredients: List<SpoonacularIngredient>,
    val usedIngredients: List<SpoonacularIngredient>,
    val likes: Int
)

// Risposta completa con tutti i dettagli della ricetta
data class SpoonacularRecipeDetail(
    val id: Int,
    val title: String,
    val summary: String,
    val image: String?,
    val instructions: String?,
    val readyInMinutes: Int,
    val servings: Int,
    val dishTypes: List<String>,
    val cuisines: List<String>,
    val analyzedInstructions: List<AnalyzedInstruction>,
    val extendedIngredients: List<ExtendedIngredient>
)

// Ingrediente base
data class SpoonacularIngredient(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String
)

// Istruzioni analizzate passo per passo
data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
)

data class Step(
    val number: Int,
    val step: String
)

// Ingrediente con tutti i dettagli
data class ExtendedIngredient(
    val id: Int,
    val name: String,
    val original: String,
    val amount: Double,
    val unit: String,
    val meta: List<String>?
)