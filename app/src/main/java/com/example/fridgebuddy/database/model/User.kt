package com.example.fridgebuddy.database.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

    data class User(
        @DocumentId
        val id: String = "",
        val username: String = "",
        val email: String = "",
        @ServerTimestamp
        val dataRegistrazione: Timestamp? = null,
        val img: String? = "",
        val completedRecipe: Int = 0,

    ) {
        constructor(username: String, email: String) : this("", username, email, null)
    }