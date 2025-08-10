package com.example.fridgebuddy.database.mapper

import com.example.fridgebuddy.database.model.AnalyzedInstruction
import com.example.fridgebuddy.database.model.ExtendedIngredient
import com.example.fridgebuddy.database.model.Recipe
import com.example.fridgebuddy.database.model.SpoonacularRecipeDetail
import com.example.fridgebuddy.database.model.SpoonacularRecipeSimple

object RecipeMapper {

    fun mapToRecipe(spoonacularRecipe: SpoonacularRecipeDetail): Recipe {
        return Recipe(
            nome = spoonacularRecipe.title,
            desc = cleanHtml(spoonacularRecipe.summary),
            categoria = mapCategoria(spoonacularRecipe.dishTypes),
            tempoPrep = "${spoonacularRecipe.readyInMinutes} minuti",
            difficolta = calcolaDifficolta(spoonacularRecipe),
            ingredienti = mapIngredienti(spoonacularRecipe.extendedIngredients),
            passaggi = mapPassaggi(spoonacularRecipe.analyzedInstructions),
            recipeImg = spoonacularRecipe.image ?: ""
        )
    }

    fun mapSimpleToRecipe(simpleRecipe: SpoonacularRecipeSimple): Recipe {
        return Recipe(
            nome = simpleRecipe.title,
            desc = "Usa ${simpleRecipe.usedIngredientCount} dei tuoi ingredienti",
            categoria = "Da definire",
            tempoPrep = "Da definire",
            difficolta = "Da definire",
            ingredienti = simpleRecipe.usedIngredients.map { it.original },
            passaggi = listOf("Carica i dettagli per vedere la preparazione"),
            recipeImg = simpleRecipe.image
        )
    }

    private fun cleanHtml(text: String): String {
        return text.replace(Regex("<.*?>"), "").trim()
    }

    private fun mapCategoria(dishTypes: List<String>): String {
        val categoriaMap = mapOf(
            "main course" to "Piatto principale",
            "dessert" to "Dolce",
            "appetizer" to "Antipasto",
            "salad" to "Insalata",
            "bread" to "Pane",
            "breakfast" to "Colazione",
            "soup" to "Zuppa",
            "beverage" to "Bevanda",
            "sauce" to "Salsa",
            "side dish" to "Contorno"
        )

        dishTypes.forEach { type ->
            categoriaMap[type.lowercase()]?.let { return it }
        }

        return "Piatto principale"
    }

    private fun calcolaDifficolta(recipe: SpoonacularRecipeDetail): String {
        return when {
            recipe.readyInMinutes <= 30 -> "Facile"
            recipe.readyInMinutes <= 60 -> "Media"
            else -> "Difficile"
        }
    }

    private fun mapIngredienti(ingredients: List<ExtendedIngredient>): List<String> {
        return ingredients.map { ingredient ->
            "${ingredient.amount.toInt()} ${ingredient.unit} di ${ingredient.name}"
        }
    }

    private fun mapPassaggi(instructions: List<AnalyzedInstruction>): List<String> {
        val passaggi = mutableListOf<String>()

        instructions.forEach { instruction ->
            instruction.steps.forEach { step ->
                passaggi.add("${step.number}. ${step.step}")
            }
        }

        if (passaggi.isEmpty()) {
            passaggi.add("Istruzioni non disponibili")
        }

        return passaggi
    }
}