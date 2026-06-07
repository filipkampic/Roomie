package com.roomie.app.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.data.model.HouseholdNotification
import com.roomie.app.data.repository.AuthRepository
import com.roomie.app.data.repository.FcmRepository
import com.roomie.app.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class QuickActionType(val label: String, val message: String) {
    NEED_HELP("Need Help", "I need some help around the place!"),
    GOING_SHOPPING("Going Shopping", "I'm heading to the store, need anything?"),
    GUESTS_COMING("Guests Coming", "I have guests coming over!"),
    NEED_QUIET("Need Quiet", "Please keep it down, I need some quiet.")
}

sealed class NotificationsListState {
    object Loading : NotificationsListState()
    data class Success(val notifications: List<HouseholdNotification>) : NotificationsListState()
    data class Error(val message: String) : NotificationsListState()
}

sealed class NotificationActionState {
    object Idle : NotificationActionState()
    object Loading : NotificationActionState()
    object Success : NotificationActionState()
    data class Error(val message: String) : NotificationActionState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository,
    private val fcmRepository: FcmRepository
) : ViewModel() {

    private val _listState = MutableStateFlow<NotificationsListState>(NotificationsListState.Loading)
    val listState: StateFlow<NotificationsListState> = _listState

    private val _actionState = MutableStateFlow<NotificationActionState>(NotificationActionState.Idle)
    val actionState: StateFlow<NotificationActionState> = _actionState

    private val _householdId = MutableStateFlow("")
    private val _currentUserId = MutableStateFlow("")
    private val _currentUserName = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val user = authRepository.fetchCurrentUser() ?: return@launch
            val hId = user.householdId.ifEmpty { return@launch }
            _householdId.value = hId
            _currentUserId.value = user.id
            _currentUserName.value = user.name
            observeNotifications(hId)
        }
    }

    private fun observeNotifications(householdId: String) {
        viewModelScope.launch {
            notificationRepository.getNotificationsFlow(householdId)
                .catch { e -> _listState.value = NotificationsListState.Error(e.message ?: "Failed to load") }
                .collect { _listState.value = NotificationsListState.Success(it) }
        }
    }

    fun sendQuickAction(type: QuickActionType) {
        val hId = _householdId.value.ifEmpty { return }
        val uId = _currentUserId.value
        val senderName = _currentUserName.value

        viewModelScope.launch {
            _actionState.value = NotificationActionState.Loading

            val notification = HouseholdNotification(
                senderId = uId,
                senderName = senderName,
                type = type.name,
                message = type.message,
                timestamp = System.currentTimeMillis(),
                householdId = hId
            )

            notificationRepository.saveNotification(notification)

            val tokens = notificationRepository.getMemberFcmTokens(hId, uId)
            if (tokens.isNotEmpty()) {
                fcmRepository.sendNotificationToTokens(
                    tokens = tokens,
                    title = senderName,
                    body = type.message
                )
            }

            _actionState.value = NotificationActionState.Success
        }
    }

    fun resetActionState() {
        _actionState.value = NotificationActionState.Idle
    }
}
