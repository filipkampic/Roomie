package com.roomie.app.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardHeaderState(
    val userName: String = "",
    val householdName: String = "",
    val isLoading: Boolean = true
)

data class DashboardSummaryState(
    val pendingChores: Int = 0,
    val totalExpenses: Double = 0.0,
    val shoppingItems: Int = 0,
    val overdueChores: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository
) : ViewModel() {

    private val _headerState = MutableStateFlow(DashboardHeaderState())
    val headerState: StateFlow<DashboardHeaderState> = _headerState

    private val _summaryState = MutableStateFlow(DashboardSummaryState())
    val summaryState: StateFlow<DashboardSummaryState> = _summaryState


    init {
        loadHeaderData()
    }

    private fun loadHeaderData(){
        viewModelScope.launch {
            try {
                val user = authRepository.fetchCurrentUser() ?: return@launch
                val householdName = if (user.householdId.isNotEmpty()) {
                    householdRepository.fetchHousehold(user.householdId)
                        .getOrNull()?.name ?: ""
                } else ""

                _headerState.value = DashboardHeaderState(
                    userName = user.name,
                    householdName = householdName,
                    isLoading = false
                )
            } catch (e: Exception) {
                _headerState.value = DashboardHeaderState(isLoading = false)
            }
        }
    }
}