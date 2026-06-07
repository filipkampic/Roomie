package com.roomie.app.features.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieBottomNavBar
import com.roomie.app.core.ui.components.RoomieTopBar
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.dashboard.components.DashboardHeader
import com.roomie.app.features.dashboard.components.DashboardSummaryGrid
import com.roomie.app.features.dashboard.components.RecentExpensesSection
import com.roomie.app.features.dashboard.components.UpcomingChoresSection

@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val headerState by viewModel.headerState.collectAsState()
    val summaryState by viewModel.summaryState.collectAsState()
    val upcomingChores by viewModel.upcomingChores.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            RoomieTopBar(
                title = "Roomie",
                onMenuClick = { /* TODO */ },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        },
        bottomBar = {
            RoomieBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Dashboard.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        if (headerState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding)
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
            DashboardHeader(
                userName = headerState.userName,
                householdName = headerState.householdName
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
            DashboardSummaryGrid(summaryState = summaryState)
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
            UpcomingChoresSection(chores = upcomingChores)
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
            RecentExpensesSection(expenses = recentExpenses)
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }
}
