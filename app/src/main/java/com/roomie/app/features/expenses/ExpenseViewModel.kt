package com.roomie.app.features.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.Expense
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ExpenseRepository
import com.roomie.app.data.repository.FcmRepository
import com.roomie.app.data.repository.HouseholdRepository
import com.roomie.app.data.repository.NotificationRepository
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
    private val householdRepository: HouseholdRepository,
    private val notificationRepository: NotificationRepository,
    private val fcmRepository: FcmRepository
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

    private val _youOwe = MutableStateFlow(0.0)
    val youOwe: StateFlow<Double> = _youOwe

    private val _youAreOwed = MutableStateFlow(0.0)
    val youAreOwed: StateFlow<Double> = _youAreOwed

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

    private suspend fun loadMembers(householdId: String) {
        householdRepository.fetchHousehold(householdId)
            .onSuccess { household ->
                val pairs = mutableListOf<Pair<String, String>>()
                for (uid in household.members) {
                    val name = authRepository.fetchUserName(uid)
                    if (name != null) pairs.add(uid to name)
                }
                _members.value = pairs
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
        val owe = calculateYouOwe(expenses, _currentUserId.value)
        val owed = calculateYouAreOwed(expenses, _currentUserId.value)
        _youOwe.value = owe
        _youAreOwed.value = owed
    }

    fun addExpense(
        title: String,
        amount: Double,
        paidBy: String,
        splitBetween: List<String>,
        category: String
    ) {
        val hId = _householdId.value.ifEmpty { return }
        val uid = _currentUserId.value
        viewModelScope.launch {
            _actionState.value = ExpenseActionState.Loading
            val expense = Expense(
                title = title,
                amount = amount,
                createdBy = uid,
                paidBy = paidBy,
                splitBetween = splitBetween,
                settledBy = emptyList(),
                date = System.currentTimeMillis(),
                householdId = hId,
                category = category
            )
            expenseRepository.addExpense(expense)
                .onSuccess {
                    sendExpenseNotification(expense)
                    _actionState.value = ExpenseActionState.Success
                }
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

    fun settleExpense(expense: Expense) {
        val uid = _currentUserId.value.ifEmpty { return }
        viewModelScope.launch {
            expenseRepository.settleExpense(expense.householdId, expense.id, uid)
                .onFailure { _actionState.value = ExpenseActionState.Error(it.message ?: "Failed to settle expense") }
        }
    }

    private suspend fun sendExpenseNotification(expense: Expense) {
        try {
            val currentUserName = authRepository.fetchCurrentUser()?.name ?: return
            val splitCount = expense.splitBetween.size
            val shareAmount = if (splitCount > 0) expense.amount / splitCount else 0.0
            val body = "$currentUserName paid €${"%.2f".format(expense.amount)} for ${expense.title} — you owe €${"%.2f".format(shareAmount)}"

            val tokens = expense.splitBetween
                .filter { it != _currentUserId.value }
                .mapNotNull { notificationRepository.getFcmToken(it) }

            if (tokens.isNotEmpty()) {
                fcmRepository.sendNotificationToTokens(
                    tokens = tokens,
                    title = "New Expense Added",
                    body = body
                )
            }
        } catch (e: Exception) {

        }
    }
}
