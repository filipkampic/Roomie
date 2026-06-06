package com.roomie.app.data.model

enum class ShoppingCategory {
    GROCERIES, CLEANING, BATHROOM, KITCHEN, OTHER;

    fun toDisplayName(): String = when (this) {
        GROCERIES -> "Groceries"
        CLEANING -> "Cleaning"
        BATHROOM -> "Bathroom"
        KITCHEN -> "Kitchen"
        OTHER -> "Other"
    }
}
