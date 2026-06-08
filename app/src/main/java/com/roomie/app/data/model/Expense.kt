package com.roomie.app.data.model

data class Expense(
    val id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val createdBy: String = "",
    val paidBy: String = "",
    val splitBetween: List<String> = emptyList(),
    val settledBy: List<String> = emptyList(),
    val date: Long = 0L,
    val householdId: String = "",
    val category: String = ExpenseCategory.OTHER.name
) {
    fun expenseCategory(): ExpenseCategory =
        runCatching { ExpenseCategory.valueOf(category) }.getOrDefault(ExpenseCategory.OTHER)

    fun isFullySettled(): Boolean =
        splitBetween.isNotEmpty() && splitBetween.all { it in settledBy }

    fun isSettledBy(uid: String): Boolean = uid in settledBy
}
