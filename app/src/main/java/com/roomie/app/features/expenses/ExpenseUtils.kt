package com.roomie.app.features.expenses

import com.roomie.app.data.model.Expense

fun calculateBalances(
    expenses: List<Expense>,
    members: List<String>
): Map<String, Double> {

    val balances = members.associateWith { 0.0 }.toMutableMap()

    for (expense in expenses) {
        val splitCount = expense.splitBetween.size
        if (splitCount == 0) continue

        val sharePerPerson = expense.amount / splitCount

        for (uid in expense.splitBetween) {
            if (uid !in expense.settledBy) {
                balances[uid] = (balances[uid] ?: 0.0) - sharePerPerson
            }
        }

        val unsettledCount = expense.splitBetween.count { it !in expense.settledBy }
        val creditedAmount = sharePerPerson * unsettledCount
        balances[expense.paidBy] = (balances[expense.paidBy] ?: 0.0) + creditedAmount
    }

    return balances
}

fun userNetBalance(balances: Map<String, Double>, userId: String): Double =
    balances[userId] ?: 0.0

fun totalHouseholdExpenses(expenses: List<Expense>): Double =
    expenses.sumOf { it.amount }
