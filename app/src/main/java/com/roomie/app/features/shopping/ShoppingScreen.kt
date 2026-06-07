package com.roomie.app.features.shopping

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieBottomNavBar
import com.roomie.app.core.ui.components.RoomieTopBar
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.shopping.components.QuickAddBar
import com.roomie.app.features.shopping.components.ShoppingEmptyState
import com.roomie.app.features.shopping.components.ShoppingItemRow

@Composable
fun ShoppingScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val listState by viewModel.listState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val members by viewModel.members.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var quickAddText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (actionState) {
            is ShoppingActionState.Success -> {
                quickAddText = ""
                viewModel.resetActionState()
            }
            is ShoppingActionState.Error -> {
                snackbarHostState.showSnackbar((actionState as ShoppingActionState.Error).message)
                viewModel.resetActionState()
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            RoomieTopBar(
                title = "Shopping List",
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
                onClick = { navController.navigate(Screen.AddShopping.route) },
                containerColor = TealPrimary,
                contentColor = SurfaceWhite,
                modifier = Modifier.size(Dimens.FabSize)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { innerPadding ->
        when (val state = listState) {
            is ShoppingListState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealPrimary)
                }
            }

            is ShoppingListState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(Dimens.ScreenPadding)
                    )
                }
            }

            is ShoppingListState.Success -> {
                val toBuy = state.items.filter { !it.completed }
                val purchased = state.items.filter { it.completed }

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
                        QuickAddBar(
                            value = quickAddText,
                            onValueChange = { quickAddText = it },
                            onAdd = {
                                if (quickAddText.isNotBlank()) {
                                    viewModel.quickAddItem(quickAddText)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (state.items.isEmpty()) {
                        item {
                            ShoppingEmptyState(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = Dimens.SpaceXXL)
                            )
                        }
                    } else {
                        if (toBuy.isNotEmpty()) {
                            item {
                                Text(
                                    text = "To Buy",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(
                                        top = Dimens.SpaceSM,
                                        bottom = Dimens.SpaceXS
                                    )
                                )
                            }
                            items(toBuy, key = { it.id }) { item ->
                                ShoppingItemRow(
                                    item = item,
                                    addedByName = members.find { it.first == item.addedBy }?.second
                                        ?: "Unknown",
                                    onToggle = { viewModel.toggleCompleted(item) },
                                    onEdit = { navController.navigate(Screen.AddShopping.editRoute(item.id)) },
                                    onDelete = { viewModel.deleteItem(item) }
                                )
                            }
                        }

                        if (purchased.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Purchased",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = StatusCompletedText,
                                    modifier = Modifier.padding(
                                        top = Dimens.SpaceLG,
                                        bottom = Dimens.SpaceXS
                                    )
                                )
                            }
                            items(purchased, key = { it.id }) { item ->
                                ShoppingItemRow(
                                    item = item,
                                    addedByName = members.find { it.first == item.addedBy }?.second
                                        ?: "Unknown",
                                    onToggle = { viewModel.toggleCompleted(item) },
                                    onEdit = { navController.navigate(Screen.AddShopping.editRoute(item.id)) },
                                    onDelete = { viewModel.deleteItem(item) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
