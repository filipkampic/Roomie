package com.roomie.app.features.profile

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.DestructiveRedLight
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.features.auth.AuthViewModel
import com.roomie.app.features.profile.components.HouseholdMembersBottomSheetContent
import com.roomie.app.features.profile.components.ProfileStat
import com.roomie.app.features.profile.components.SettingsRow
import com.roomie.app.features.profile.components.ThemeModeBottomSheetContent
import com.roomie.app.features.theme.ThemeViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val themeMode by themeViewModel.themeMode.collectAsStateWithLifecycle()

    var showMembersSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showThemeSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val notificationsEnabled by themeViewModel.notificationsEnabled.collectAsStateWithLifecycle()

    val leaveHouseholdState by profileViewModel.leaveHouseholdState.collectAsStateWithLifecycle()
    var showLeaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(leaveHouseholdState) {
        if (leaveHouseholdState) {
            navController.navigate(Screen.HouseholdSetup.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = RoomieTypography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            RoomieCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.CardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.name,
                        style = RoomieTypography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Dimens.SpaceXS))
                    Text(
                        text = uiState.email,
                        style = RoomieTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStat(
                    value = uiState.tasksDone.toString(),
                    label = "Tasks Done"
                )
                ProfileStat(
                    value = "€${String.format(Locale.getDefault(), "%.0f", uiState.totalPaid)}",
                    label = "Paid"
                )
                ProfileStat(
                    value = uiState.shoppingItemsAdded.toString(),
                    label = "Shopping Items"
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Text(
                text = "Settings",
                style = RoomieTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            SettingsRow(label = "Household Members", onClick = { showMembersSheet = true })
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            SettingsRow(
                label = if (notificationsEnabled) "Notifications: On" else "Notifications: Off",
                onClick = { themeViewModel.setNotificationsEnabled(!notificationsEnabled) }
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            SettingsRow(
                label = "Theme Mode: ${themeMode.name.lowercase().replaceFirstChar { it.uppercase() }}",
                onClick = { showThemeSheet = true }
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Button(
                onClick = { authViewModel.logout(navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DestructiveRedLight,
                    contentColor = DestructiveRed
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Logout",
                    style = RoomieTypography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Button(
                onClick = { showLeaveDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.ButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Text(
                    text = "Leave Household",
                    style = RoomieTypography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }

    if (showMembersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMembersSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            HouseholdMembersBottomSheetContent(
                householdName = uiState.householdName,
                inviteCode = uiState.inviteCode,
                members = uiState.members
            )
        }
    }

    if (showThemeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = themeSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ThemeModeBottomSheetContent(
                currentMode = themeMode,
                onModeSelected = {
                    themeViewModel.setThemeMode(it)
                    showThemeSheet = false
                }
            )
        }
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Leave Household") },
            text = { Text("Are you sure you want to leave this household? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showLeaveDialog = false
                    profileViewModel.leaveHousehold()
                }) {
                    Text("Leave", color = DestructiveRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
