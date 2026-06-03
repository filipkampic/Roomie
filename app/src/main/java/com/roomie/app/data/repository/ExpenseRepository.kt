package com.roomie.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.roomie.app.data.model.Expense
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun expensesCollection(householdId: String) =
        firestore.collection("households")
            .document(householdId)
            .collection("expenses")

    fun getExpensesFlow(householdId: String): Flow<List<Expense>> = callbackFlow {
        val listener = expensesCollection(householdId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshots?.documents
                    ?.mapNotNull { it.toObject(Expense::class.java) }
                    ?: emptyList()
                trySend(expenses)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addExpense(expense: Expense): Result<Unit> {
        return try {
            val docRef = expensesCollection(expense.householdId).document()
            docRef.set(expense.copy(id = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExpense(householdId: String, expenseId: String): Result<Unit> {
        return try {
            expensesCollection(householdId).document(expenseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
