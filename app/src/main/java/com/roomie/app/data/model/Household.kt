package com.roomie.app.data.model

data class Household(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val members: List<String> = emptyList()
)
