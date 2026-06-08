package com.roomie.app.features.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieLogo
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealDark
import com.roomie.app.core.ui.theme.TealLight
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.auth.AuthViewModel
import com.roomie.app.features.household.components.HouseholdOptionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdSetupScreen(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    TextButton(onClick = { authViewModel.logout(navController) }) {
                        Text("Logout", color = TealPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceXXL))

            RoomieLogo()

            Spacer(modifier = Modifier.height(Dimens.SpaceXL))

            Text(
                text = "Welcome Home 👋",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = "Create or join a household to start managing\nshared tasks, expenses and shopping lists.",
                style = MaterialTheme.typography.bodyMedium,
                color = NavySecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceXL))

            HouseholdOptionCard(
                icon = Icons.Filled.Home,
                title = "Create Household",
                subtitle = "Start a new shared household space",
                containerColor = TealPrimary,
                contentColor = SurfaceWhite,
                iconBgAlpha = 0.25f,
                onClick = { navController.navigate(Screen.CreateHousehold.route) }
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            HouseholdOptionCard(
                icon = Icons.Filled.Group,
                title = "Join Household",
                subtitle = "Enter an invite code from roommates",
                containerColor = TealLight,
                contentColor = TealDark,
                iconBgAlpha = 0.4f,
                onClick = { navController.navigate(Screen.JoinHousehold.route) }
            )
        }
    }
}
