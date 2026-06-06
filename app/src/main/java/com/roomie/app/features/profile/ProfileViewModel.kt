package com.roomie.app.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ChoreRepository
import com.roomie.app.data.repository.ExpenseRepository
import com.roomie.app.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val tasksDone: Int = 0,
    val totalPaid: Double = 0.0,
    val shoppingItemsAdded: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val choreRepository: ChoreRepository,
    private val expenseRepository: ExpenseRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val user = authRepository.fetchCurrentUser() ?: return@launch
                val hId = user.householdId.ifEmpty { return@launch }
                val uid = user.id

                combine(
                    choreRepository.getChoresFlow(hId),
                    expenseRepository.getExpensesFlow(hId),
                    shoppingRepository.getItemsFlow(hId)
                ) { chores, expenses, shoppingItems ->
                    val tasksDone = chores.count { it.completed && it.assignedTo == uid }
                    val totalPaid = expenses
                        .filter { it.paidBy == uid }
                        .sumOf { it.amount }
                    val shoppingAdded = shoppingItems.count { it.addedBy == uid }

                    ProfileUiState(
                        name = user.name,
                        email = user.email,
                        tasksDone = tasksDone,
                        totalPaid = totalPaid,
                        shoppingItemsAdded = shoppingAdded,
                        isLoading = false
                    )
                }
                .catch { _uiState.value = _uiState.value.copy(isLoading = false) }
                .collect { _uiState.value = it }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
