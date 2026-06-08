package com.roomie.app.features.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.navigation.Screen
import com.roomie.app.core.ui.components.RoomieButton
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavyPrimary
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHouseholdScreen(
    navController: NavHostController,
    viewModel: HouseholdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var householdName by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is HouseholdUiState.Success) {
            viewModel.resetState()
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(TealPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Text(
                text = "Create Household",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = "Set up a shared space for chores, expenses\nand shopping lists",
                style = MaterialTheme.typography.bodyMedium,
                color = NavySecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceXL))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.CardPadding)
                ) {
                    Text(
                        text = "Household Name",
                        style = MaterialTheme.typography.labelLarge,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    RoomieTextField(
                        value = householdName,
                        onValueChange = { householdName = it },
                        placeholder = "Apartment 3B"
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    Text(
                        text = "This name will be visible to all household members",
                        style = MaterialTheme.typography.bodySmall,
                        color = TealPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                    if (uiState is HouseholdUiState.Error) {
                        Text(
                            text = (uiState as HouseholdUiState.Error).message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(Dimens.SpaceSM))
                    }

                    RoomieButton(
                        text = "Create Household",
                        onClick = {
                            if (householdName.isNotBlank()) {
                                viewModel.createHousehold(householdName.trim())
                            }
                        },
                        isLoading = uiState is HouseholdUiState.Loading,
                        enabled = householdName.isNotBlank()
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceXL))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Key,
                    contentDescription = null,
                    tint = NavySecondary,
                    modifier = Modifier.size(Dimens.IconSizeSM)
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                Text(
                    text = "Invite code will be generated automatically",
                    style = MaterialTheme.typography.bodySmall,
                    color = NavySecondary
                )
            }
        }
    }
}
