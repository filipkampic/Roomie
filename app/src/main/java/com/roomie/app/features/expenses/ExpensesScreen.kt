package com.roomie.app.features.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieBottomNavBar
import com.roomie.app.core.ui.components.RoomieTopBar
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.expenses.components.BalanceSummaryRow
import com.roomie.app.features.expenses.components.ExpenseItem
import com.roomie.app.features.expenses.components.HouseholdBalanceCard

@Composable
fun ExpensesScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val balances by viewModel.balances.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        if (actionState is ExpenseActionState.Error) {
            snackbarHostState.showSnackbar((actionState as ExpenseActionState.Error).message)
            viewModel.resetActionState()
        }
    }

    val userBalance = balances[currentUserId] ?: 0.0
    val youOwe = if (userBalance < 0) -userBalance else 0.0
    val youAreOwed = if (userBalance > 0) userBalance else 0.0
    val totalExpenses = when (val s = listState) {
        is ExpenseListState.Success -> totalHouseholdExpenses(s.expenses)
        else -> 0.0
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RoomieTopBar(
                title = "Expenses",
                onMenuClick = {  },
                onProfileClick = { navController.navigate(Screen.Profile.route) }
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
                onClick = { navController.navigate(Screen.AddExpense.route) },
                containerColor = TealPrimary,
                contentColor = SurfaceWhite,
                modifier = Modifier.size(Dimens.FabSize)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        }
    ) { innerPadding ->
        when (val state = listState) {
            is ExpenseListState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            }
            is ExpenseListState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(Dimens.ScreenPadding)
                    )
                }
            }
            is ExpenseListState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = Dimens.ScreenPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                        HouseholdBalanceCard(
                            totalExpenses = totalExpenses,
                            userBalance = userBalance
                        )
                    }
                    item {
                        BalanceSummaryRow(
                            youOwe = youOwe,
                            youAreOwed = youAreOwed
                        )
                    }
                    item {
                        Text(
                            text = "Recent Expenses",
                            style = RoomieTypography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (state.expenses.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Dimens.SpaceXL),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No expenses yet.\nTap + to add one.",
                                    style = RoomieTypography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(state.expenses, key = { it.id }) { expense ->
                            val members = viewModel.members.collectAsState().value
                            val paidByName = members.find { it.first == expense.paidBy }?.second ?: expense.paidBy
                            val splitCount = expense.splitBetween.size
                            val userShare = if (currentUserId in expense.splitBetween && splitCount > 0)
                                expense.amount / splitCount else 0.0

                            ExpenseItem(
                                expense = expense,
                                paidByName = paidByName,
                                userShare = userShare,
                                currentUserId = currentUserId,
                                onDelete = { viewModel.deleteExpense(it) }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(Dimens.SpaceXL)) }
                }
            }
        }
    }
}