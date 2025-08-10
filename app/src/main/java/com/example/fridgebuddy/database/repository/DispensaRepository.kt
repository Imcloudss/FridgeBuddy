package com.example.fridgebuddy.database.repository

import android.util.Log
import com.example.fridgebuddy.database.model.DispensaItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DispensaRepository(
    private val database: FirebaseDatabase
) {
    // Helper function per verificare se un item è in scadenza
    private fun isItemInScadenza(item: DispensaItem, giorni: Int): Boolean {
        if (item.dataScadenza.isBlank()) return false

        return try {
            // CAMBIA IL FORMATO DA "yyyy-MM-dd" A "dd/MM/yyyy"
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ITALIAN)
            val dataScadenza = formatter.parse(item.dataScadenza)

            if (dataScadenza == null) {
                Log.w("DispensaRepository", "Data null per ${item.nomeIngrediente}: ${item.dataScadenza}")
                return false
            }

            // Ottieni la data di oggi (mezzanotte)
            val oggi = Calendar.getInstance()
            oggi.set(Calendar.HOUR_OF_DAY, 0)
            oggi.set(Calendar.MINUTE, 0)
            oggi.set(Calendar.SECOND, 0)
            oggi.set(Calendar.MILLISECOND, 0)

            // Calcola la differenza in giorni
            val oggiMillis = oggi.timeInMillis
            val scadenzaMillis = dataScadenza.time
            val millisInDay = 1000L * 60 * 60 * 24
            val giorniDifferenza = (scadenzaMillis - oggiMillis) / millisInDay

            // L'item è in scadenza se scade tra oggi e i prossimi N giorni
            val isInScadenza = giorniDifferenza in 0..giorni

            Log.d("DispensaRepository",
                "${item.nomeIngrediente} (${item.dataScadenza}): " +
                        "giorni alla scadenza = $giorniDifferenza, " +
                        "in scadenza = $isInScadenza"
            )

            isInScadenza
        } catch (e: Exception) {
            Log.e("DispensaRepository",
                "Errore parsing data per ${item.nomeIngrediente}: ${item.dataScadenza}", e)
            false
        }
    }

    // Ottieni tutti gli items della dispensa di un utente come Flow
    fun getDispensaItems(userId: String): Flow<List<DispensaItem>> = callbackFlow {
        val dispensaRef = database.getReference("utenti/$userId/dispensa")

        Log.d("DispensaRepository", "🔍 Realtime DB path: utenti/$userId/dispensa")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DispensaRepository", "📡 Realtime DB data received")
                Log.d("DispensaRepository", "📄 Snapshot exists: ${snapshot.exists()}")
                Log.d("DispensaRepository", "📄 Children count: ${snapshot.childrenCount}")

                val items = mutableListOf<DispensaItem>()

                for (itemSnapshot in snapshot.children) {
                    try {
                        Log.d("DispensaRepository", "📋 Processing child: ${itemSnapshot.key}")
                        Log.d("DispensaRepository", "📋 Child data: ${itemSnapshot.value}")

                        val item = itemSnapshot.getValue(DispensaItem::class.java)
                        if (item != null) {
                            // Imposta l'ID dal key del nodo
                            val itemWithId = item.copy(id = itemSnapshot.key ?: "")
                            items.add(itemWithId)
                            Log.d("DispensaRepository", "✅ Item aggiunto: ${item.nomeIngrediente}")
                        } else {
                            Log.w("DispensaRepository", "⚠️ Item null per child: ${itemSnapshot.key}")
                        }
                    } catch (e: Exception) {
                        Log.e("DispensaRepository", "❌ Errore parsing item: ${e.message}", e)
                    }
                }

                Log.d("DispensaRepository", "✅ Totale items: ${items.size}")
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DispensaRepository", "❌ Realtime DB error: ${error.message}")
                close(error.toException())
            }
        }

        dispensaRef.addValueEventListener(listener)

        awaitClose {
            Log.d("DispensaRepository", "🔌 Removing Realtime DB listener")
            dispensaRef.removeEventListener(listener)
        }
    }

    // Ottieni items in scadenza nei prossimi giorni
    fun getItemsInScadenza(userId: String, giorni: Int = 7): Flow<List<DispensaItem>> = callbackFlow {
        val dispensaRef = database.getReference("utenti/$userId/dispensa")

        Log.d("DispensaRepository", "🔍 Query items in scadenza per $giorni giorni")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<DispensaItem>()

                for (itemSnapshot in snapshot.children) {
                    try {
                        val item = itemSnapshot.getValue(DispensaItem::class.java)
                        if (item != null) {
                            val itemWithId = item.copy(id = itemSnapshot.key ?: "")

                            // Controlla se l'item è in scadenza
                            if (isItemInScadenza(itemWithId, giorni)) {
                                items.add(itemWithId)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DispensaRepository", "Errore parsing item in scadenza: ${e.message}")
                    }
                }

                Log.d("DispensaRepository", "✅ Items in scadenza trovati: ${items.size}")
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DispensaRepository", "❌ Errore items in scadenza: ${error.message}")
                close(error.toException())
            }
        }

        dispensaRef.addValueEventListener(listener)
        awaitClose { dispensaRef.removeEventListener(listener) }
    }

    // Aggiungi un nuovo item alla dispensa
    suspend fun addItem(userId: String, item: DispensaItem): Result<String> {
        return try {
            val dispensaRef = database.getReference("utenti/$userId/dispensa")
            val newItemRef = dispensaRef.push()
            val itemId = newItemRef.key ?: throw Exception("Impossibile generare ID")

            // Rimuovi l'ID dal data class per non salvarlo due volte
            val itemToSave = item.copy(id = "")
            newItemRef.setValue(itemToSave).await()

            Log.d("DispensaRepository", "✅ Item aggiunto con ID: $itemId")
            Result.success(itemId)
        } catch (e: Exception) {
            Log.e("DispensaRepository", "❌ Errore aggiunta item: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Aggiorna un item esistente
    suspend fun updateItem(userId: String, item: DispensaItem): Result<Unit> {
        return try {
            val itemRef = database.getReference("utenti/$userId/dispensa/${item.id}")

            // Rimuovi l'ID dal data class per non salvarlo nel DB
            val itemToSave = item.copy(id = "")
            itemRef.setValue(itemToSave).await()

            Log.d("DispensaRepository", "✅ Item aggiornato: ${item.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DispensaRepository", "❌ Errore aggiornamento item: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Elimina un item
    suspend fun deleteItem(userId: String, itemId: String): Result<Unit> {
        return try {
            val itemRef = database.getReference("utenti/$userId/dispensa/$itemId")
            itemRef.removeValue().await()

            Log.d("DispensaRepository", "✅ Item eliminato: $itemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("DispensaRepository", "❌ Errore eliminazione item: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Ottieni items per categoria
    fun getItemsByCategoria(userId: String, idCategoria: String): Flow<List<DispensaItem>> = callbackFlow {
        val dispensaRef = database.getReference("utenti/$userId/dispensa")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<DispensaItem>()

                for (itemSnapshot in snapshot.children) {
                    try {
                        val item = itemSnapshot.getValue(DispensaItem::class.java)
                        if (item != null && item.idCategoria == idCategoria) {
                            val itemWithId = item.copy(id = itemSnapshot.key ?: "")
                            items.add(itemWithId)
                        }
                    } catch (e: Exception) {
                        Log.e("DispensaRepository", "Errore parsing item per categoria: ${e.message}")
                    }
                }

                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        dispensaRef.addValueEventListener(listener)
        awaitClose { dispensaRef.removeEventListener(listener) }
    }
}