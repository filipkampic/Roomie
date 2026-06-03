package com.roomie.app.core.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object Register : Screen("register")

    // Household setup
    object HouseholdSetup : Screen("household_setup")
    object CreateHousehold : Screen("create_household")
    object JoinHousehold : Screen("join_household")

    // Main app
    object Dashboard : Screen("dashboard")
    object Chores : Screen("chores")
    object AddChore : Screen("add_chore") { fun editRoute(choreId: String) = "add_chore?choreId=$choreId" }
    object Expenses : Screen("expenses")
    object AddExpense : Screen("add_expense")
    object Shopping : Screen("shopping")
    object Profile : Screen("profile")
}