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
            balances[uid] = (balances[uid] ?: 0.0) - sharePerPerson
        }

        balances[expense.paidBy] = (balances[expense.paidBy] ?: 0.0) + expense.amount
    }

    return balances
}

fun userNetBalance(balances: Map<String, Double>, userId: String): Double =
    balances[userId] ?: 0.0

fun totalHouseholdExpenses(expenses: List<Expense>): Double =
    expenses.sumOf { it.amount }
