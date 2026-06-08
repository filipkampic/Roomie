package com.roomie.app.core.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard.route, "Home", Icons.Filled.Home),
    BottomNavItem(Screen.Chores.route, "Chores", Icons.Filled.List),
    BottomNavItem(Screen.Expenses.route, "Expenses", Icons.Filled.Wallet),
    BottomNavItem(Screen.Shopping.route, "Shopping", Icons.Filled.ShoppingBag)
)

@Preview(showBackground = false)
@Composable
fun RoomieBottomNavBarPreview() {
    RoomieBottomNavBar(
        currentRoute = Screen.Dashboard.route,
        onNavigate = {}
    )
}

@Composable
fun RoomieBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = Dimens.CardElevation
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) onNavigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = RoomieTypography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TealPrimary,
                    selectedTextColor = TealPrimary,
                    indicatorColor = TealPrimary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
