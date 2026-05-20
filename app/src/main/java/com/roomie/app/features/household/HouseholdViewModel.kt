package com.roomie.app.features.household

import androidx.compose.ui.input.key.Key.Companion.H
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.Household
import com.roomie.app.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HouseholdUiState {
    object Idle: HouseholdUiState()
    object Loading: HouseholdUiState()
    data class Success(val household: Household) : HouseholdUiState()
    data class Error(val message: String) : HouseholdUiState()
}

@HiltViewModel
class HouseholdViewModel @Inject constructor(
    private val householdRepository: HouseholdRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HouseholdUiState>(HouseholdUiState.Idle)
    val uiState: StateFlow<HouseholdUiState> = _uiState

    fun createHousehold(name: String) {
        viewModelScope.launch {
            _uiState.value = HouseholdUiState.Loading
            householdRepository.createHousehold(name)
                .onSuccess { _uiState.value = HouseholdUiState.Success(it) }
                .onFailure { _uiState.value = HouseholdUiState.Error(it.message ?: "Failed to create household") }
        }
    }

    fun joinHousehold(inviteCode: String) {
        viewModelScope.launch {
            _uiState.value = HouseholdUiState.Loading
            householdRepository.joinHousehold(inviteCode)
                .onSuccess { _uiState.value = HouseholdUiState.Success(it) }
                .onFailure { _uiState.value = HouseholdUiState.Error(it.message ?: "Failed to join household") }
        }
    }

    fun resetState() {
        _uiState.value = HouseholdUiState.Idle
    }
}