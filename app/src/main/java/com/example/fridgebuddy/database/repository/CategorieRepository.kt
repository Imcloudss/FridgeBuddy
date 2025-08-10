package com.example.fridgebuddy.database.repository

import com.example.fridgebuddy.database.model.Categoria
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class CategorieRepository(
    private val database: FirebaseDatabase
) {

    suspend fun getAllCategorie(): Result<List<Categoria>> {
        return try {
            val snapshot = database.getReference("categorie").get().await()
            val categorie = snapshot.children.mapNotNull { it.getValue(Categoria::class.java) }
            Result.success(categorie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}