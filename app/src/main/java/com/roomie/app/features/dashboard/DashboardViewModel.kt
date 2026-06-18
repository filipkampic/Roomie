package com.roomie.app.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ChoreRepository
import com.roomie.app.data.repository.ExpenseRepository
import com.roomie.app.data.repository.HouseholdRepository
import com.roomie.app.data.repository.ShoppingRepository
import com.roomie.app.features.chores.resolveStatus
import com.roomie.app.features.dashboard.components.RecentExpenseItem
import com.roomie.app.features.dashboard.components.UpcomingChoreItem
import com.roomie.app.features.expenses.calculateBalances
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DashboardHeaderState(
    val userName: String = "",
    val householdName: String = "",
    val isLoading: Boolean = true
)

data class DashboardSummaryState(
    val pendingChores: Int = 0,
    val netBalance: Double = 0.0,
    val shoppingItems: Int = 0,
    val overdueChores: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository,
    private val choreRepository: ChoreRepository,
    private val expenseRepository: ExpenseRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _headerState = MutableStateFlow(DashboardHeaderState())
    val headerState: StateFlow<DashboardHeaderState> = _headerState

    private val _summaryState = MutableStateFlow(DashboardSummaryState())
    val summaryState: StateFlow<DashboardSummaryState> = _summaryState

    private val _upcomingChores = MutableStateFlow<List<UpcomingChoreItem>>(emptyList())
    val upcomingChores: StateFlow<List<UpcomingChoreItem>> = _upcomingChores

    private val _recentExpenses = MutableStateFlow<List<RecentExpenseItem>>(emptyList())
    val recentExpenses: StateFlow<List<RecentExpenseItem>> = _recentExpenses


    init {
        loadHeaderData()
    }

    private fun loadHeaderData(){
        viewModelScope.launch {
            try {
                val user = authRepository.fetchCurrentUser() ?: return@launch
                val hId = user.householdId.ifEmpty { return@launch }

                val householdName = householdRepository.fetchHousehold(hId)
                    .getOrNull()?.name ?: ""

                _headerState.value = DashboardHeaderState(
                    userName = user.name,
                    householdName = householdName,
                    isLoading = false
                )

                observeData(hId)
            } catch (e: Exception) {
                _headerState.value = DashboardHeaderState(isLoading = false)
            }
        }
    }

    private fun observeData(householdId: String) {
        viewModelScope.launch {
            combine(
                choreRepository.getChoresFlow(householdId),
                expenseRepository.getExpensesFlow(householdId),
                shoppingRepository.getItemsFlow(householdId)
            ) { chores, expenses, shoppingItems ->
                Triple(chores, expenses, shoppingItems)
            }
            .catch { }
            .collect { (chores, expenses, shoppingItems) ->
                    val now = System.currentTimeMillis()

                    val currentUid = authRepository.currentUser?.uid ?: ""
                    val memberUids = expenses.flatMap { it.splitBetween }.distinct()
                    val balances = calculateBalances(expenses, memberUids)
                    val netBalance = balances[currentUid] ?: 0.0

                    _summaryState.value = DashboardSummaryState(
                        pendingChores = chores.count { !it.completed && (it.deadline == 0L || it.deadline >= now) },
                        netBalance = netBalance,
                        shoppingItems = shoppingItems.count { !it.completed },
                        overdueChores = chores.count { !it.completed && it.deadline > 0 && it.deadline < now }
                    )

                    val dateFormatter = SimpleDateFormat("MMM d, HH:mm", Locale.ENGLISH)
                    _upcomingChores.value = chores
                        .filter { !it.completed && it.deadline > 0 }
                        .sortedBy { it.deadline }
                        .take(2)
                        .map { chore ->
                            UpcomingChoreItem(
                                title = chore.title,
                                deadlineText = dateFormatter.format(Date(chore.deadline)),
                                status = chore.resolveStatus()
                            )
                        }

                val expDateFormatter = SimpleDateFormat("MMM d", Locale.ENGLISH)
                _recentExpenses.value = expenses
                    .take(3)
                    .map { expense ->
                        RecentExpenseItem(
                            title = expense.title,
                            dateText = expDateFormatter.format(Date(expense.date)),
                            amount = expense.amount,
                            category = expense.expenseCategory()
                        )
                    }
                }
        }
    }
}