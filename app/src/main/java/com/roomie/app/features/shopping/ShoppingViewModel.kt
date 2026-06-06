package com.roomie.app.features.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.ShoppingCategory
import com.roomie.app.data.model.ShoppingItem
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.HouseholdRepository
import com.roomie.app.data.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ShoppingListState {
    object Loading : ShoppingListState()
    data class Success(val items: List<ShoppingItem>) : ShoppingListState()
    data class Error(val message: String) : ShoppingListState()
}

sealed class ShoppingActionState {
    object Idle : ShoppingActionState()
    object Loading : ShoppingActionState()
    object Success : ShoppingActionState()
    data class Error(val message: String) : ShoppingActionState()
}

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val authRepository: AuthRepository,
    private val householdRepository: HouseholdRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<ShoppingListState>(ShoppingListState.Loading)
    val listState: StateFlow<ShoppingListState> = _listState

    private val _actionState = MutableStateFlow<ShoppingActionState>(ShoppingActionState.Idle)
    val actionState: StateFlow<ShoppingActionState> = _actionState

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
            observeItems(hId)
        }
    }

    private fun loadMembers(householdId: String) {
        viewModelScope.launch {
            householdRepository.fetchHousehold(householdId)
                .onSuccess { household ->
                    val pairs = household.members.mapNotNull { uid ->
                        val name = authRepository.fetchUserName(uid)
                        if (name != null) uid to name else null
                    }
                    _members.value = pairs
                }
        }
    }

    private fun observeItems(householdId: String) {
        viewModelScope.launch {
            shoppingRepository.getItemsFlow(householdId)
                .catch { e -> _listState.value = ShoppingListState.Error(e.message ?: "Failed to load items") }
                .collect { items -> _listState.value = ShoppingListState.Success(items) }
        }
    }

    fun quickAddItem(name: String) {
        val hId = _householdId.value.ifEmpty { return }
        val uid = _currentUserId.value
        viewModelScope.launch {
            _actionState.value = ShoppingActionState.Loading
            val item = ShoppingItem(
                name = name.trim(),
                quantity = 1,
                category = ShoppingCategory.GROCERIES.name,
                addedBy = _currentUserId.value,
                householdId = hId
            )
            shoppingRepository.addItem(item)
                .onSuccess { _actionState.value = ShoppingActionState.Success }
                .onFailure { _actionState.value = ShoppingActionState.Error(it.message ?: "Failed to add item") }
        }
    }

    fun addItem(
        name: String,
        quantity: Int,
        category: String,
        notes: String
    ) {
        val hId = _householdId.value.ifEmpty { return }
        viewModelScope.launch {
            _actionState.value = ShoppingActionState.Loading
            val item = ShoppingItem(
                name = name.trim(),
                quantity = quantity,
                category = category,
                notes = notes,
                addedBy = _currentUserId.value,
                householdId = hId
            )
            shoppingRepository.addItem(item)
                .onSuccess { _actionState.value = ShoppingActionState.Success }
                .onFailure { _actionState.value = ShoppingActionState.Error(it.message ?: "Failed to add item") }
        }
    }

    fun toggleCompleted(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.toggleCompleted(item)
                .onFailure { _actionState.value = ShoppingActionState.Error(it.message ?: "Failed to update item") }
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(item.householdId, item.id)
                .onFailure { _actionState.value = ShoppingActionState.Error(it.message ?: "Failed to delete item") }
        }
    }

    fun resetActionState() {
        _actionState.value = ShoppingActionState.Idle
    }
}
