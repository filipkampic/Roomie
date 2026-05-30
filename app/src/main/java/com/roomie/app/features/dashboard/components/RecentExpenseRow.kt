package com.roomie.app.features.dashboard.components

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
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.ExpenseRed
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealLight
import com.roomie.app.core.ui.theme.TealPrimary

enum class ExpenseCategory {
    GROCERIES, BILLS, CLEANING, OTHER
}

@Preview(showBackground = false)
@Composable
fun RecentExpenseRowPreview() {
    RecentExpenseRow(
        expense = RecentExpenseItem(
            title = "Groceries",
            dateText = "Today",
            amount = 10.0,
            category = ExpenseCategory.GROCERIES
        )
    )
}

@Composable
fun RecentExpenseRow(expense: RecentExpenseItem) {
    RoomieCard(modifier = Modifier.fillMaxWidth()) {
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
                    imageVector = categoryIcon(expense.category),
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
                    text = expense.dateText,
                    style = RoomieTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "-%.2f €".format(expense.amount),
                style = RoomieTypography.titleSmall,
                color = ExpenseRed
            )
        }
    }
}

private fun categoryIcon(category: ExpenseCategory): ImageVector {
    return when (category) {
        ExpenseCategory.GROCERIES -> Icons.Outlined.ShoppingBag
        ExpenseCategory.BILLS -> Icons.Outlined.Receipt
        ExpenseCategory.CLEANING -> Icons.Outlined.Home
        ExpenseCategory.OTHER -> Icons.Outlined.Receipt
    }
}
