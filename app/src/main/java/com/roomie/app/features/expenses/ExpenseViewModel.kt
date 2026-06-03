package com.roomie.app.features.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.Expense
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ExpenseRepository
import com.roomie.app.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ExpenseListState {
    object Loading : ExpenseListState()
    data class Success(val expenses: List<Expense>) : ExpenseListState()
    data class Error(val message: String) : ExpenseListState()
}

sealed class ExpenseActionState {
    object Idle : ExpenseActionState()
    object Loading : ExpenseActionState()
    object Success : ExpenseActionState()
    data class Error(val message: String) : ExpenseActionState()
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository
)  : ViewModel() {

    private val _listState = MutableStateFlow<ExpenseListState>(ExpenseListState.Loading)
    val listState: StateFlow<ExpenseListState> = _listState

    private val _actionState = MutableStateFlow<ExpenseActionState>(ExpenseActionState.Idle)
    val actionState: StateFlow<ExpenseActionState> = _actionState

    private val _householdId = MutableStateFlow("")
    val householdId: StateFlow<String> = _householdId

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _members = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val members: StateFlow<List<Pair<String, String>>> = _members

    private val _balances = MutableStateFlow<Map<String, Double>>(emptyMap())
    val balances: StateFlow<Map<String, Double>> = _balances

    init {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            _currentUserId.value = uid

            val user = authRepository.fetchCurrentUser() ?: return@launch
            val hId = user.householdId.ifEmpty { return@launch }
            _householdId.value = hId

            loadMembers(hId)
            observeExpenses(hId)
        }
    }

    private fun loadMembers(householdId: String) {
        viewModelScope.launch {
            householdRepository.fetchHousehold(householdId)
                .onSuccess { household ->
                    val pairs = mutableListOf<Pair<String, String>>()
                    for (uid in household.members) {
                        val name = authRepository.fetchUserName(uid)
                        if (name != null) pairs.add(uid to name)
                    }
                    _members.value = pairs

                    val currentExpenses = (_listState.value as? ExpenseListState.Success)?.expenses
                    if (currentExpenses != null) recomputeBalances(currentExpenses)
                }
        }
    }

    private fun observeExpenses(householdId: String) {
        viewModelScope.launch {
            expenseRepository.getExpensesFlow(householdId)
                .catch { e -> _listState.value = ExpenseListState.Error(e.message ?: "Failed to load expenses") }
                .collect { expenses ->
                    _listState.value = ExpenseListState.Success(expenses)
                    recomputeBalances(expenses)
                }
        }
    }

    private fun recomputeBalances(expenses: List<Expense>) {
        val memberUids = _members.value.map { it.first }
        _balances.value = calculateBalances(expenses, memberUids)
    }

    fun addExpense(
        title: String,
        amount: Double,
        paidBy: String,
        splitBetween: List<String>,
        category: String
    ) {
        val hId = _householdId.value.ifEmpty { return }
        viewModelScope.launch {
            _actionState.value = ExpenseActionState.Loading
            val expense = Expense(
                title = title,
                amount = amount,
                paidBy = paidBy,
                splitBetween = splitBetween,
                date = System.currentTimeMillis(),
                householdId = hId,
                category = category
            )
            expenseRepository.addExpense(expense)
                .onSuccess { _actionState.value = ExpenseActionState.Success }
                .onFailure { _actionState.value = ExpenseActionState.Error(it.message ?: "Failed to add expense") }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(expense.householdId, expense.id)
                .onFailure { _actionState.value = ExpenseActionState.Error(it.message ?: "Failed to delete expense") }
        }
    }

    fun resetActionState() {
        _actionState.value = ExpenseActionState.Idle
    }
}
