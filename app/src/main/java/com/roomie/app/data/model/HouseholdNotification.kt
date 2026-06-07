package com.roomie.app.data.model

data class HouseholdNotification(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val type: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val householdId: String = ""
)
