package com.roomie.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.roomie.app.data.model.Household
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val householdsCollection = firestore.collection("households")
    private val usersCollection = firestore.collection("users")

    suspend fun createHousehold(name: String): Result<Household> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            val inviteCode = generateInviteCode()
            val docRef = householdsCollection.document()
            val household = Household(
                id = docRef.id,
                name = name,
                inviteCode = inviteCode,
                members = listOf(uid)
            )
            docRef.set(household).await()
            usersCollection.document(uid)
                .update("householdId", docRef.id).await()
            Result.success(household)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinHousehold(inviteCode: String): Result<Household> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not authenticated"))
            val query = householdsCollection
                .whereEqualTo("inviteCode", inviteCode)
                .get().await()
            if (query.isEmpty) return Result.failure(Exception("Invalid invite code"))
            val doc = query.documents.first()
            val household = doc.toObject(Household::class.java) ?: return Result.failure(Exception("Failed to parse household"))
            householdsCollection.document(doc.id)
                .update("members", household.members + uid).await()
            usersCollection.document(uid)
                .update("householdId", doc.id).await()
            Result.success(household.copy(members = household.members + uid))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchHousehold(householdId: String): Result<Household> {
        return try {
            val doc = householdsCollection.document(householdId).get().await()
            val household = doc.toObject(Household::class.java) ?: return Result.failure(Exception("Household not found"))
            Result.success(household)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    suspend fun findHouseholdByInviteCode(inviteCode: String): Result<Household?> {
        return try {
            val query = householdsCollection
                .whereEqualTo("inviteCode", inviteCode.uppercase())
                .get().await()
            if (query.isEmpty) Result.success(null)
            else Result.success(query.documents.first().toObject(Household::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}