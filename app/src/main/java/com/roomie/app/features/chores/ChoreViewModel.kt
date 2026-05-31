package com.roomie.app.features.chores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.Chore
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.ChoreRepository
import com.roomie.app.data.repository.HouseholdRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChoreListState {
    object Loading : ChoreListState()
    data class Success(val chores: List<Chore>) : ChoreListState()
    data class Error(val message: String) : ChoreListState()
}

sealed class ChoreActionState {
    object Idle : ChoreActionState()
    object Loading : ChoreActionState()
    object Success : ChoreActionState()
    data class Error(val message: String) : ChoreActionState()
}

@HiltViewModel
class ChoreViewModel @Inject constructor(
    private val choreRepository: ChoreRepository,
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<ChoreListState>(ChoreListState.Loading)
    val listState: StateFlow<ChoreListState> = _listState

    private val _actionState = MutableStateFlow<ChoreActionState>(ChoreActionState.Idle)
    val actionState: StateFlow<ChoreActionState> = _actionState

    private val _householdId = MutableStateFlow("")
    val householdId: StateFlow<String> = _householdId

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _members = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val members: StateFlow<List<Pair<String, String>>> = _members

    init {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            _currentUserId.value = uid

            val user = authRepository.fetchCurrentUser() ?: return@launch
            val hId = user.householdId.ifEmpty { return@launch }
            _householdId.value = hId

            loadMembers(hId)
            observeChores(hId)
        }
    }

    private fun loadMembers(householdId: String) {
        viewModelScope.launch {
            householdRepository.fetchHousehold(householdId)
                .onSuccess { household ->
                    val memberPairs = household.members.mapNotNull { uid ->
                        val name = fetchUserName(uid)
                        if (name != null) uid to name else null
                    }
                    _members.value = memberPairs
                }
        }
    }

    private suspend fun fetchUserName(uid: String): String? {
        return try {
            authRepository.fetchUserName(uid)
        } catch (e: Exception) {
            null
        }
    }

    private fun observeChores(householdId: String) {
        viewModelScope.launch {
            choreRepository.getChoresFlow(householdId)
                .catch { e -> _listState.value = ChoreListState.Error(e.message ?: "Failed to load chores") }
                .collect { chores -> _listState.value = ChoreListState.Success(chores) }
        }
    }

    fun addChore(
        title: String,
        assignedTo: String,
        deadline: Long,
        notes: String
    ) {
        val hId = _householdId.value.ifEmpty { return }
        viewModelScope.launch {
            _actionState.value = ChoreActionState.Loading
            val chore = Chore(
                title = title,
                assignedTo = assignedTo,
                deadline = deadline,
                completed = false,
                createdBy = _currentUserId.value,
                householdId = hId,
                notes = notes
            )
            choreRepository.addChore(chore)
                .onSuccess { _actionState.value = ChoreActionState.Success }
                .onFailure { e -> _actionState.value = ChoreActionState.Error(e.message ?: "Failed to add chore") }
        }
    }

    fun toggleComplete(chore: Chore) {
        viewModelScope.launch {
            choreRepository.toggleComplete(chore)
                .onFailure { _actionState.value = ChoreActionState.Error(it.message ?: "Failed to update chore" ) }
        }
    }

    fun deleteChore(chore: Chore) {
        viewModelScope.launch {
            choreRepository.deleteChore(chore.householdId, chore.id)
                .onFailure { _actionState.value = ChoreActionState.Error(it.message ?: "Failed to delete chore") }
        }
    }

    fun resetActionState() {
        _actionState.value = ChoreActionState.Idle
    }
}