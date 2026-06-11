package com.roomie.app.core.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class OverdueAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                NotificationScheduler.scheduleOverdueAlerts(context)
            }
            "OVERDUE_ALERT" -> {
                val request = OneTimeWorkRequestBuilder<OverdueAlertWorker>().build()
                WorkManager.getInstance(context).enqueue(request)
                NotificationScheduler.scheduleOverdueAlerts(context)
            }
        }
    }
}
