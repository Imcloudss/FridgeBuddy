package com.example.fridgebuddy.database.repository

import android.util.Log
import com.example.fridgebuddy.database.model.FoodItem
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FoodRepository(
    private val database: FirebaseDatabase
) {
    fun getAllFoodItems(): Flow<List<FoodItem>> = callbackFlow {
        val ref = database.getReference("/food")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(FoodItem::class.java) }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FoodRepository", "DB error: ${error.message}")
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
