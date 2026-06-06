package com.roomie.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.roomie.app.features.auth.AuthViewModel
import com.roomie.app.features.auth.LoginScreen
import com.roomie.app.features.auth.RegisterScreen
import com.roomie.app.features.chores.AddChoreScreen
import com.roomie.app.features.chores.ChoresScreen
import com.roomie.app.features.dashboard.DashboardScreen
import com.roomie.app.features.expenses.AddExpenseScreen
import com.roomie.app.features.expenses.ExpensesScreen
import com.roomie.app.features.household.CreateHouseholdScreen
import com.roomie.app.features.household.HouseholdSetupScreen
import com.roomie.app.features.household.JoinHouseholdScreen
import com.roomie.app.features.profile.ProfileScreen
import com.roomie.app.features.shopping.ShoppingScreen
import com.roomie.app.features.shopping.components.AddShoppingItemScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val startDestination by authViewModel.startDestination.collectAsState()

    if (startDestination == null) return

    NavHost(
        navController = navController,
        startDestination = startDestination!!
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // Household setup
        composable(Screen.HouseholdSetup.route) {
            HouseholdSetupScreen(navController = navController)
        }
        composable(Screen.CreateHousehold.route) {
            CreateHouseholdScreen(navController = navController)
        }
        composable(Screen.JoinHousehold.route) {
            JoinHouseholdScreen(navController = navController)
        }

        // Main app
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Chores.route) {
            ChoresScreen(navController = navController)
        }
        composable(
            route = "add_chore?choreId={choreId}",
            arguments = listOf(navArgument("choreId") {
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val choreId = backStackEntry.arguments?.getString("choreId")
            AddChoreScreen(navController = navController, choreId = choreId)
        }
        composable(Screen.Expenses.route) {
            ExpensesScreen(navController = navController)
        }
        composable(Screen.AddExpense.route) {
            AddExpenseScreen(navController = navController)
        }
        composable(Screen.Shopping.route) {
            ShoppingScreen(navController = navController)
        }
        composable(Screen.AddShopping.route) {
            AddShoppingItemScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
    }
}