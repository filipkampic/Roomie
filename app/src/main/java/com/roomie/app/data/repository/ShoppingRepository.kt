package com.roomie.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.roomie.app.data.model.ShoppingItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun itemsCollection(householdId: String) =
        firestore.collection("households")
            .document(householdId)
            .collection("shopping_items")

    fun getItemsFlow(householdId: String): Flow<List<ShoppingItem>> = callbackFlow {
        val listener = itemsCollection(householdId)
            .orderBy("completed")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshots?.documents
                    ?.mapNotNull { it.toObject(ShoppingItem::class.java) }
                    ?: emptyList()
                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addItem(item: ShoppingItem): Result<Unit> {
        return try {
            val docRef = itemsCollection(item.householdId).document()
            docRef.set(item.copy(id = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleCompleted(item: ShoppingItem): Result<Unit> {
        return try {
            itemsCollection(item.householdId)
                .document(item.id)
                .update("completed", !item.completed)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(householdId: String, itemId: String): Result<Unit> {
        return try {
            itemsCollection(householdId).document(itemId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(item: ShoppingItem): Result<Unit> {
        return try {
            itemsCollection(item.householdId)
                .document(item.id)
                .set(item)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
