package com.example.fridgebuddy.database.model

import com.google.firebase.firestore.DocumentId

data class Categoria(
    @DocumentId
    val id: String = "",
    val nome: String = "",
    val descrizione: String = ""
)