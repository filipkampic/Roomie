package com.roomie.app.features.theme

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roomie.app.core.notifications.NotificationScheduler
import com.roomie.app.data.repository.ThemeMode
import com.roomie.app.data.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = themeRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.SYSTEM
        )

    val notificationsEnabled: StateFlow<Boolean> = themeRepository.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themeRepository.setThemeMode(mode)
        }
    }

    fun resetToSystem() {
        viewModelScope.launch {
            themeRepository.setThemeMode(ThemeMode.SYSTEM)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            themeRepository.setNotificationsEnabled(enabled)
            if (enabled) {
                NotificationScheduler.scheduleChoreReminders(appContext)
                NotificationScheduler.scheduleOverdueAlerts(appContext)
            } else {
                NotificationScheduler.cancelAll(appContext)
            }
        }
    }
}
