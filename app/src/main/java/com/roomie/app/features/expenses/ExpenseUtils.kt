package com.roomie.app.features.expenses

import com.roomie.app.data.model.Expense

fun calculateBalances(
    expenses: List<Expense>,
    members: List<String>
): Map<String, Double> {
    val allUids = (members + expenses.flatMap { it.splitBetween } + expenses.map { it.paidBy }).distinct()
    val balances = allUids.associateWith { 0.0 }.toMutableMap()

    for (expense in expenses) {
        val splitCount = expense.splitBetween.size
        if (splitCount == 0) continue

        val sharePerPerson = expense.amount / splitCount

        for (uid in expense.splitBetween) {
            if (uid == expense.paidBy) continue
            if (uid !in expense.settledBy) {
                balances[uid] = (balances[uid] ?: 0.0) - sharePerPerson
            }
        }

        val unsettledCount = expense.splitBetween.count { it !in expense.settledBy &&  it !in expense.paidBy }
        val creditedAmount = sharePerPerson * unsettledCount
        balances[expense.paidBy] = (balances[expense.paidBy] ?: 0.0) + creditedAmount
    }

    return balances
}

fun calculateYouOwe(expenses: List<Expense>, currentUid: String): Double {
    var total = 0.0
    for (expense in expenses) {
        if (expense.paidBy == currentUid) continue
        if (currentUid !in expense.splitBetween) continue
        if (currentUid in expense.settledBy) continue
        val share = expense.amount / expense.splitBetween.size
        total += share
    }
    return total
}

fun calculateYouAreOwed(expenses: List<Expense>, currentUid: String): Double {
    var total = 0.0
    for (expense in expenses) {
        if (expense.paidBy != currentUid) continue
        for (uid in expense.splitBetween) {
            if (uid == currentUid) continue
            if (uid in expense.settledBy) continue
            val share = expense.amount / expense.splitBetween.size
            total += share
        }
    }
    return total
}

fun userNetBalance(balances: Map<String, Double>, userId: String): Double =
    balances[userId] ?: 0.0

fun totalHouseholdExpenses(expenses: List<Expense>): Double =
    expenses.sumOf { it.amount }
