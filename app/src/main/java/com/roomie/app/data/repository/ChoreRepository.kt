package com.roomie.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.roomie.app.data.model.Chore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private fun choresCollection(householdId: String) =
        firestore.collection("households")
            .document(householdId)
            .collection("chores")

    fun getChoresFlow(householdId: String): Flow<List<Chore>> = callbackFlow {
        val listener = choresCollection(householdId)
            .orderBy("deadline", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chores = snapshots?.documents
                    ?.mapNotNull { it.toObject(Chore::class.java) }
                    ?: emptyList()
                trySend(chores)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addChore(chore: Chore): Result<Unit> {
        return try {
            val householdId = chore.householdId.ifEmpty {
                return Result.failure(Exception("householdId missing"))
            }
            val docRef = choresCollection(householdId).document()
            docRef.set(chore.copy(id = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateChore(chore: Chore): Result<Unit> {
        return try {
            choresCollection(chore.householdId)
                .document(chore.id)
                .set(chore)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChore(householdId: String, choreId: String): Result<Unit> {
        return try {
            choresCollection(householdId).document(choreId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleComplete(chore: Chore): Result<Unit> =
        updateChore(chore.copy(completed = !chore.completed))

    fun currentUserId(): String? = auth.currentUser?.uid

    suspend fun getChoresSnapshot(householdId: String): List<Chore> {
        return try {
            choresCollection(householdId)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Chore::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
