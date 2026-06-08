package com.roomie.app.features.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.notifications.components.NotificationHistoryItem
import com.roomie.app.features.notifications.components.QuickActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        if (actionState is NotificationActionState.Error) {
            snackbarHostState.showSnackbar((actionState as NotificationActionState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notifications",
                        style = RoomieTypography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = Dimens.ScreenPadding,
                vertical = Dimens.SpaceMD
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
        ) {
            item {
                Text(
                    text = "Quick Actions",
                    style = RoomieTypography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickActionButton(
                            type = QuickActionType.NEED_HELP,
                            isLoading = actionState is NotificationActionState.Loading,
                            onClick = { viewModel.sendQuickAction(QuickActionType.NEED_HELP) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            type = QuickActionType.GOING_SHOPPING,
                            isLoading = actionState is NotificationActionState.Loading,
                            onClick = { viewModel.sendQuickAction(QuickActionType.GOING_SHOPPING) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        QuickActionButton(
                            type = QuickActionType.GUESTS_COMING,
                            isLoading = actionState is NotificationActionState.Loading,
                            onClick = { viewModel.sendQuickAction(QuickActionType.GUESTS_COMING) },
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionButton(
                            type = QuickActionType.NEED_QUIET,
                            isLoading = actionState is NotificationActionState.Loading,
                            onClick = { viewModel.sendQuickAction(QuickActionType.NEED_QUIET) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Dimens.SpaceSM)) }

            item {
                Text(
                    text = "History",
                    style = RoomieTypography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            when (val state = listState) {
                is NotificationsListState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(Dimens.SpaceXXL),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TealPrimary)
                        }
                    }
                }
                is NotificationsListState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is NotificationsListState.Success -> {
                    if (state.notifications.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.SpaceXXL),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No notifications yet",
                                    style = RoomieTypography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(state.notifications, key = { it.id }) { notification ->
                            NotificationHistoryItem(notification = notification)
                        }
                    }
                }
            }
        }
    }
}