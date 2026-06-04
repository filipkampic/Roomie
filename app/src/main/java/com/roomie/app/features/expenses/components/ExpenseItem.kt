package com.roomie.app.features.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.roomie.app.core.ui.components.DeleteConfirmDialog
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.ExpenseRed
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealLight
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.Expense
import com.roomie.app.data.model.ExpenseCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            title = "Delete expense?",
            message = "This action cannot be undone. The expense will be permanently removed.",
            onConfirm = {
                showDeleteDialog = false
                onDelete(expense)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    RoomieCard(
        modifier = modifier.fillMaxWidth(),
        onClick = { showDeleteDialog = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.IconSizeLG)
                    .clip(CircleShape)
                    .background(TealLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = expense.expenseCategory().toIcon(),
                    contentDescription = null,
                    tint = TealPrimary,
                    modifier = Modifier.size(Dimens.IconSizeMD)
                )
            }
            Spacer(modifier = Modifier.width(Dimens.SpaceMD))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = RoomieTypography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatExpenseDate(expense.date),
                    style = RoomieTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "-${"%.2f".format(expense.amount)} €",
                style = RoomieTypography.titleSmall,
                color = ExpenseRed
            )
        }
    }
}

fun formatExpenseDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun ExpenseCategory.toIcon(): ImageVector = when (this) {
    ExpenseCategory.GROCERIES -> Icons.Outlined.ShoppingBag
    ExpenseCategory.BILLS -> Icons.Outlined.Receipt
    ExpenseCategory.CLEANING -> Icons.Outlined.Home
    ExpenseCategory.OTHER -> Icons.Outlined.MoreHoriz
}
