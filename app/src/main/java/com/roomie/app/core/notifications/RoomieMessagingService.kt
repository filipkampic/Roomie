package com.roomie.app.core.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class RoomieMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: return
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: return

        NotificationHelper.buildGeneralNotification(
            context = applicationContext,
            title = title,
            body = body,
            notificationId = System.currentTimeMillis().toInt()
        )
    }

    override fun onNewToken(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update("fcmToken", token)
    }
}