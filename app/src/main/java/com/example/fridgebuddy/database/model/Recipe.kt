package com.example.fridgebuddy.database.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val nome: String = "",
    val desc: String = "",
    val categoria: String = "",
    val tempoPrep: String = "",
    val difficolta: String = "",
    val ingredienti: List<String> = emptyList(),
    val passaggi: List<String> = emptyList(),
    val recipeImg: String = ""
)