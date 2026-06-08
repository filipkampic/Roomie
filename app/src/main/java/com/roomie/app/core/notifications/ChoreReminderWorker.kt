package com.roomie.app.core.notifications

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ChoreRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ChoreReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val authRepository: AuthRepository,
    private val choreRepository: ChoreRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val user = authRepository.fetchCurrentUser() ?: return Result.success()
            val householdId = user.householdId.ifEmpty { return Result.success() }
            val currentUserId = user.id

            val chores = choreRepository.getChoresSnapshot(householdId)

            val now = System.currentTimeMillis()
            val oneHour = 60 * 60 * 1000L

            chores
                .filter { chore ->
                    !chore.completed &&
                    chore.assignedTo == currentUserId &&
                    chore.deadline > now &&
                    chore.deadline <= now + oneHour
                }
                .forEach { chore ->
                    NotificationHelper.buildChoreReminderNotification(
                        context = applicationContext,
                        choreTitle = chore.title,
                        notificationId = chore.id.hashCode()
                    )
                }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}