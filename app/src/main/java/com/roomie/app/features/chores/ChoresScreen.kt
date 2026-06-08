package com.roomie.app.features.chores

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.components.RoomieBottomNavBar
import com.roomie.app.core.ui.components.RoomieTopBar
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.Chore
import com.roomie.app.features.chores.components.ChoresEmptyState
import com.roomie.app.features.chores.components.ChoresList
import com.roomie.app.features.chores.components.FilterChipRow

fun Chore.resolveStatus(): ChoreStatus {
    if (completed) return ChoreStatus.COMPLETED
    return if (deadline > 0 && deadline < System.currentTimeMillis())
        ChoreStatus.OVERDUE else ChoreStatus.PENDING
}

enum class ChoreFilter(val label: String) {
    ALL("All"),
    PENDING("Pending"),
    COMPLETED("Completed"),
    OVERDUE("Overdue")
}

@Composable
fun ChoresScreen(
    navController: NavHostController,
    viewModel: ChoreViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    val members by viewModel.members.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var activeFilter by remember { mutableStateOf(ChoreFilter.ALL) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(actionState) {
        if (actionState is ChoreActionState.Error) {
            snackbarHostState.showSnackbar((actionState as ChoreActionState.Error).message)
            viewModel.resetActionState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RoomieTopBar(
                title = "Chores",
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) }
            )
        },
        bottomBar = {
            RoomieBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddChore.route) },
                containerColor = TealPrimary,
                contentColor = SurfaceWhite,
                modifier = Modifier.size(Dimens.FabSize)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add chore")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FilterChipRow(
                activeFilter = activeFilter,
                onFilterSelected = { activeFilter = it }
            )

            when (val state = listState) {
                is ChoreListState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealPrimary)
                    }
                }

                is ChoreListState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = RoomieTypography.bodyMedium
                        )
                    }
                }

                is ChoreListState.Success -> {
                    val chores = state.chores
                    val filtered = when (activeFilter) {
                        ChoreFilter.ALL -> chores
                        ChoreFilter.PENDING -> chores.filter { it.resolveStatus() == ChoreStatus.PENDING }
                        ChoreFilter.COMPLETED -> chores.filter { it.resolveStatus() == ChoreStatus.COMPLETED }
                        ChoreFilter.OVERDUE -> chores.filter { it.resolveStatus() == ChoreStatus.OVERDUE }
                    }

                    if (filtered.isEmpty()) {
                        ChoresEmptyState(filter = activeFilter)
                    } else {
                        ChoresList(
                            chores = filtered,
                            members = members,
                            onToggle = { viewModel.toggleComplete((it)) },
                            onDelete = { viewModel.deleteChore(it) },
                            onEdit = { navController.navigate(Screen.AddChore.editRoute(it.id)) }
                        )
                    }
                }
            }
        }
    }
}
