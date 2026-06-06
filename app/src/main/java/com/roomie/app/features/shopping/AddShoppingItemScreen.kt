package com.roomie.app.features.shopping

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.ui.components.CategoryChip
import com.roomie.app.core.ui.components.FieldLabel
import com.roomie.app.core.ui.components.RoomieButton
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.ShoppingCategory
import com.roomie.app.features.shopping.components.QuantityStepper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShoppingItemScreen(
    navController: NavHostController,
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val actionState by viewModel.actionState.collectAsState()
    val members by viewModel.members.collectAsState()

    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedCategory by remember { mutableStateOf(ShoppingCategory.GROCERIES) }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    LaunchedEffect(actionState) {
        if (actionState is ShoppingActionState.Success) {
            viewModel.resetActionState()
            navController.popBackStack()
        }
    }

    val isLoading = actionState is ShoppingActionState.Loading

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (members.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.ScreenPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(Dimens.SpaceSM))

            Text(
                text = "Add Shopping Item",
                style = RoomieTypography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Add a new item to the shared shopping list",
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(Dimens.SpaceLG))

            RoomieCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.CardPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
                ) {
                    FieldLabel("Item Name")
                    RoomieTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        placeholder = "Milk",
                        isError = nameError,
                        singleLine = true
                    )

                    FieldLabel("Quantity")
                    QuantityStepper(
                        quantity = quantity,
                        onDecrement = { if (quantity > 1) quantity-- },
                        onIncrement = { if (quantity < 99) quantity++ },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    FieldLabel("Category")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
                    ) {
                        ShoppingCategory.entries.forEach { category ->
                            CategoryChip(
                                label = category.toDisplayName(),
                                selected = category == selectedCategory,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }

                    FieldLabel("Notes")
                    RoomieTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = "Optional item details...",
                        singleLine = false,
                        minLines = 3
                    )
                }
            }

            Spacer(Modifier.height(Dimens.SpaceLG))

            RoomieButton(
                text = "Add Item",
                onClick = {
                    nameError = name.isBlank()
                    if (!nameError) {
                        viewModel.addItem(
                            name = name.trim(),
                            quantity = quantity,
                            category = selectedCategory.name,
                            notes = notes.trim()
                        )
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(Dimens.SpaceLG))
        }
    }
}
