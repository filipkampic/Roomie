package com.roomie.app.data.model

data class Chore(
    val id: String = "",
    val title: String = "",
    val assignedTo: String = "",
    val deadline: Long = 0L,
    val completed: Boolean = false,
    val createdBy: String = "",
    val householdId: String = "",
    val notes: String = ""
)
