package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.data.model.ExpenseCategory
import kotlin.collections.take

data class RecentExpenseItem(
    val title: String,
    val dateText: String,
    val amount: Double,
    val category: ExpenseCategory
)

@Composable
fun RecentExpensesSection(
    expenses: List<RecentExpenseItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Recent Expenses",
            style = RoomieTypography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Dimens.SpaceMD))

        if (expenses.isEmpty()) {
            Text(
                text = "No recent expenses",
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                Dimens.SpaceSM)) {
                expenses.take(3).forEach { expense ->
                    RecentExpenseRow(expense = expense)
                }
            }
        }
    }
}
