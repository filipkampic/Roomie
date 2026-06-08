package com.roomie.app.data.model

enum class ExpenseCategory {
    GROCERIES, BILLS, CLEANING, OTHER;

    fun toDisplayName(): String = when (this) {
        GROCERIES -> "Groceries"
        BILLS -> "Bills"
        CLEANING -> "Cleaning"
        OTHER -> "Other"
    }
}