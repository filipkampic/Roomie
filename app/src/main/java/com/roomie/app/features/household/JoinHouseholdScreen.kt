package com.roomie.app.features.household

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
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
fun JoinHouseholdScreen(
    navController: NavHostController,
    viewModel: HouseholdViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val previewHousehold by viewModel.previewHousehold.collectAsState()
    var inviteCode by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is HouseholdUiState.Success) {
            viewModel.resetState()
            viewModel.resetPreview()
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
                    imageVector = Icons.Filled.Group,
                    contentDescription = null,
                    tint = SurfaceWhite,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            Text(
                text = "Join Household",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = "Enter an invite code shared by your\nroommates to join their household",
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
                        text = "Invite Code",
                        style = MaterialTheme.typography.labelLarge,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    RoomieTextField(
                        value = inviteCode,
                        onValueChange = { input ->
                            val filtered = input.uppercase().filter { it.isLetterOrDigit() }
                            if (filtered.length <= 6) {
                                inviteCode = filtered
                                viewModel.onInviteCodeChanged(filtered)
                            }
                        },
                        placeholder = "ABCD-1234",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )

                    Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                    Text(
                        text = "Ask a roommate to share their household invite code",
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
                        text = "Join Household",
                        onClick = {
                            if (inviteCode.isNotBlank()) {
                                viewModel.joinHousehold(inviteCode.trim())
                            }
                        },
                        isLoading = uiState is HouseholdUiState.Loading,
                        enabled = inviteCode.length == 6
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceMD))

            AnimatedVisibility(
                visible = previewHousehold != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                previewHousehold?.let { household ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.CardCornerRadius),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.CardPadding),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Preview",
                                style = MaterialTheme.typography.labelMedium,
                                color = NavySecondary
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
                            Text(
                                text = household.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
                            Text(
                                text = "${household.members.size} member${if (household.members.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NavySecondary
                            )
                        }
                    }
                }
            }
        }
    }
}