package com.roomie.app.features.expenses

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.roomie.app.core.ui.components.CategoryChip
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.data.model.ExpenseCategory
import com.roomie.app.core.ui.components.FieldLabel
import com.roomie.app.core.ui.components.RoomieButton
import com.roomie.app.core.ui.theme.TealPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    navController: NavHostController,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val actionState by viewModel.actionState.collectAsState()
    val members by viewModel.members.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf<Pair<String, String>?>(null) }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.GROCERIES) }
    var splitBetween by remember { mutableStateOf<Set<String>>(emptySet()) }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var splitError by remember { mutableStateOf(false) }
    var paidByDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(members, currentUserId) {
        if (paidBy == null && members.isNotEmpty() && currentUserId.isNotEmpty()) {
            paidBy = members.find { it.first == currentUserId } ?: members.first()
        }
        if (splitBetween.isEmpty() && members.isNotEmpty()) {
            splitBetween = members.map { it.first }.toSet()
        }
    }

    LaunchedEffect(actionState) {
        if (actionState is ExpenseActionState.Success) {
            viewModel.resetActionState()
            navController.popBackStack()
        }
    }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val splitCount = splitBetween.size
    val sharePerPerson = if (splitCount > 0 && amount > 0) amount / splitCount else 0.0
    val isLoading = actionState is ExpenseActionState.Loading

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
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))

            Text(
                text = "New Expense",
                style = RoomieTypography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Add a shared household expense",
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            RoomieCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.CardPadding),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
                ) {
                    FieldLabel("Expense Name")
                    RoomieTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        placeholder = "Groceries",
                        isError = titleError,
                        singleLine = true
                    )

                    FieldLabel("Amount")
                    RoomieTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it.filter { c -> c.isDigit() || c == '.' }
                            amountError = false
                        },
                        placeholder = "0.00",
                        isError = amountError,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = null
                    )

                    if (amount > 0) {
                        Text(
                            text = "€ ${"%.2f".format(amount)}",
                            style = RoomieTypography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    FieldLabel("Paid By")
                    ExposedDropdownMenuBox(
                        expanded = paidByDropdownExpanded,
                        onExpandedChange = { paidByDropdownExpanded = it }
                    ) {
                        RoomieTextField(
                            value = paidBy?.second ?: "",
                            onValueChange = {},
                            placeholder = "Select member",
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = paidByDropdownExpanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = paidByDropdownExpanded,
                            onDismissRequest = { paidByDropdownExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                        ) {
                            members.forEach { member ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = member.second,
                                            style = RoomieTypography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        paidBy = member
                                        paidByDropdownExpanded = false
                                    },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                )
                            }
                        }
                    }

                    FieldLabel("Split Between")
                    if (splitError) {
                        Text(
                            text = "Select at least one member",
                            style = RoomieTypography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    members.forEach { member ->
                        val isChecked = member.first in splitBetween
                        SplitMemberRow(
                            name = member.second,
                            shareAmount = if (isChecked && splitCount > 0) sharePerPerson else 0.0,
                            isChecked = isChecked,
                            onCheckedChange = { checked ->
                                splitBetween = if (checked)
                                    splitBetween + member.first
                                else
                                    splitBetween - member.first
                            }
                        )
                    }

                    FieldLabel("Category")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
                    ) {
                        ExpenseCategory.entries.forEach { category ->
                            CategoryChip(
                                label = category.toDisplayName(),
                                selected = category == selectedCategory,
                                onClick = { selectedCategory = category }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))

            RoomieButton(
                text = "Create Expense",
                onClick = {
                    titleError = title.isBlank()
                    amountError = amount <= 0
                    splitError = splitBetween.isEmpty()
                    if (!titleError && !amountError && paidBy != null && splitError.not()) {
                        viewModel.addExpense(
                            title = title.trim(),
                            amount = amount,
                            paidBy = paidBy!!.first,
                            splitBetween = splitBetween.toList(),
                            category = selectedCategory.name
                        )
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Dimens.SpaceLG))
        }
    }
}

@Composable
private fun SplitMemberRow(
    name: String,
    shareAmount: Double,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    RoomieCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.CardPadding, vertical = Dimens.SpaceSM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (shareAmount > 0) {
                Text(
                    text = "€${"%.2f".format(shareAmount)}",
                    style = RoomieTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceSM))
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = TealPrimary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
