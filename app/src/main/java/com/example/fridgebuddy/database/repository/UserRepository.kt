package com.example.fridgebuddy.database.repository

import android.util.Log
import com.example.fridgebuddy.database.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    // Ottieni un utente specifico come Flow dal Realtime Database
    fun getUser(userId: String): Flow<User?> = callbackFlow {
        val userRef = database.getReference("utenti/$userId")

        Log.d("UserRepository", "🔍 Realtime DB path: utenti/$userId")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("UserRepository", "📡 User data received")
                Log.d("UserRepository", "📄 Snapshot exists: ${snapshot.exists()}")

                try {
                    if (snapshot.exists()) {
                        val username = snapshot.child("username").getValue(String::class.java) ?: ""
                        val email = snapshot.child("email").getValue(String::class.java) ?: ""
                        val completedRecipe = snapshot.child("completedRecipe").getValue(Int::class.java) ?: 0
                        val img = snapshot.child("img").getValue(String::class.java) ?: ""
                        val createdAt = snapshot.child("createdAt").getValue(String::class.java)

                        val user = User(
                            id = userId,
                            username = username,
                            email = email,
                            completedRecipe = completedRecipe,
                            img = img
                        )

                        Log.d("UserRepository", "✅ User loaded: $username")
                        trySend(user)
                    } else {
                        Log.w("UserRepository", "⚠️ User snapshot doesn't exist")
                        trySend(null)
                    }
                } catch (e: Exception) {
                    Log.e("UserRepository", "❌ Error parsing user: ${e.message}", e)
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("UserRepository", "❌ Realtime DB error: ${error.message}")
                close(error.toException())
            }
        }

        userRef.addValueEventListener(listener)

        awaitClose {
            Log.d("UserRepository", "🔌 Removing user listener")
            userRef.removeEventListener(listener)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return this.currentUser
    }

    suspend fun createNewUser(userId: String, username: String, email: String): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId")
            val userData = mapOf(
                "username" to username,
                "email" to email,
                "completedRecipe" to 0,
                "img" to "",
                "createdAt" to System.currentTimeMillis().toString()
            )
            userRef.updateChildren(userData).await()
            Log.d("UserRepository", "✅ New user created")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error creating user: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Aggiorna l'immagine profilo
    suspend fun updateProfileImage(userId: String, imageUrl: String): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId/img")
            userRef.setValue(imageUrl).await()
            Log.d("UserRepository", "✅ Profile image updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error updating profile image: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Aggiorna il numero di ricette completate
    suspend fun updateCompletedRecipes(userId: String, count: Int): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId/completedRecipe")
            userRef.setValue(count).await()
            Log.d("UserRepository", "✅ Completed recipes updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error updating completed recipes: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Incrementa il numero di ricette completate
    suspend fun incrementCompletedRecipes(userId: String): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId/completedRecipe")

            // Ottieni il valore corrente
            val snapshot = userRef.get().await()
            val currentCount = snapshot.getValue(Int::class.java) ?: 0

            // Incrementa e salva
            userRef.setValue(currentCount + 1).await()

            Log.d("UserRepository", "✅ Completed recipes incremented to ${currentCount + 1}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error incrementing completed recipes: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun updateUsername(userId: String, username: String): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId/username")
            userRef.setValue(username).await()
            Log.d("UserRepository", "✅ Username updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error updating username: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun updateEmail(userId: String, email: String): Result<Unit> {
        return try {
            val userRef = database.getReference("utenti/$userId/email")
            userRef.setValue(email).await()
            Log.d("UserRepository", "✅ Email updated")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Error updating email: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val usernameResult = updateUsername(user.id, user.username)
            val emailResult = updateEmail(user.id, user.email)

            if (usernameResult.isSuccess && emailResult.isSuccess) {
                Log.d("UserRepository", "✅ User updated")
                Result.success(Unit)
            } else {
                val errorMessages = buildList {
                    if (usernameResult.isFailure) add("username: ${usernameResult.exceptionOrNull()?.message}")
                    if (emailResult.isFailure) add("email: ${emailResult.exceptionOrNull()?.message}")
                }.joinToString(", ")
                Log.e("UserRepository", "❌ Failed to update: $errorMessages")
                Result.failure(Exception("Errore aggiornamento: $errorMessages"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "❌ Unexpected error in updateUser: ${e.message}", e)
            Result.failure(e)
        }
    }

}