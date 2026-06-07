package com.roomie.app.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.roomie.app.MainActivity
import com.roomie.app.R

object NotificationHelper {

    const val CHANNEL_CHORE_REMINDERS = "chore_reminders"
    const val CHANNEL_OVERDUE_ALERTS = "overdue_alerts"

    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        val reminderChannel = NotificationChannel(
            CHANNEL_CHORE_REMINDERS,
            "Chore Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Reminders for upcoming chores"
        }

        val overdueChannel = NotificationChannel(
            CHANNEL_OVERDUE_ALERTS,
            "Overdue Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for overdue chores"
        }

        manager.createNotificationChannel(reminderChannel)
        manager.createNotificationChannel(overdueChannel)
    }

    fun buildChoreReminderNotification(
        context: Context,
        choreTitle: String,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_CHORE_REMINDERS)
            .setSmallIcon(R.drawable.ic_roomie_logo)
            .setContentTitle("Chore Reminder")
            .setContentText("\"$choreTitle\" is due in 1 hour!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(notificationId, notification)
    }

    fun buildOverdueNotification(
        context: Context,
        overdueCount: Int,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (overdueCount == 1)
            "You have 1 overdue chore!"
        else
            "You have $overdueCount overdue chores!"

        val notification = NotificationCompat.Builder(context, CHANNEL_OVERDUE_ALERTS)
            .setSmallIcon(R.drawable.ic_roomie_logo)
            .setContentTitle("Overdue Chores")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(notificationId, notification)
    }
}
