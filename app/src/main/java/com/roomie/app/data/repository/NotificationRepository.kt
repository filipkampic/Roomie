package com.roomie.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.roomie.app.data.model.HouseholdNotification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private fun notificationsCollection(householdId: String) =
        firestore.collection("households")
            .document(householdId)
            .collection("notifications")

    fun getNotificationsFlow(householdId: String): Flow<List<HouseholdNotification>> = callbackFlow {
        val listener = notificationsCollection(householdId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notifications = snapshots?.documents
                    ?.mapNotNull { it.toObject(HouseholdNotification::class.java) }
                    ?: emptyList()
                trySend(notifications)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveNotification(notification: HouseholdNotification): Result<Unit> {
        return try {
            val docRef = notificationsCollection(notification.householdId).document()
            docRef.set(notification.copy(id = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMemberFcmTokens(
        householdId: String,
        excludeUid: String
    ): List<String> {
        return try {
            val household = firestore.collection("households")
                .document(householdId)
                .get().await()
            val members = household.get("members") as? List<String> ?: emptyList()
            val otherMembers = members.filter { it != excludeUid }

            otherMembers.mapNotNull { uid ->
                val userDoc = firestore.collection("users").document(uid).get().await()
                userDoc.getString("fcmToken")
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getFcmToken(uid: String): String? {
        return try {
            firestore.collection("users").document(uid)
                .get().await()
                .getString("fcmToken")
        } catch (e: Exception) {
            null
        }
    }
}
