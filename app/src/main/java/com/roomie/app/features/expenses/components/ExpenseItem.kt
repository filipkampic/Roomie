package com.roomie.app.features.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.components.DeleteConfirmDialog
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.ExpenseRed
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.StatusCompletedBg
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.SurfaceWhite
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
    paidByName: String,
    userShare: Double,
    currentUserId: String,
    onDelete: (Expense) -> Unit,
    onSettle: (Expense) -> Unit,
    modifier: Modifier = Modifier
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    val isSettledByMe = expense.isSettledBy(currentUserId)
    val isFullySettled = expense.isFullySettled()
    val isPayer = expense.paidBy == currentUserId

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    if (!showDeleteDialog.value) showDeleteDialog.value = true
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    if (!isSettledByMe && !isPayer) onSettle(expense)
                    false
                }
                else -> false
            }
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = !isSettledByMe && !isPayer,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            if (direction == SwipeToDismissBoxValue.StartToEnd) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp)
                        .clip(RoomieShapes.large)
                        .background(StatusCompletedText),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Settle",
                        tint = SurfaceWhite,
                        modifier = Modifier.padding(start = Dimens.SpaceLG)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp)
                        .clip(RoomieShapes.large)
                        .background(DestructiveRed),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SurfaceWhite,
                        modifier = Modifier.padding(end = Dimens.SpaceLG)
                    )
                }
            }
        }
    ) {
        val borderColor = when {
            isFullySettled -> StatusCompletedText
            isSettledByMe -> StatusCompletedText.copy(alpha = 0.5f)
            else -> null
        }

        RoomieCard(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (borderColor != null)
                                Modifier.border(1.dp, borderColor, RoomieShapes.large)
                        else Modifier
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.CardPadding)
                    .alpha(if (isFullySettled) 0.6f else 1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(Dimens.IconSizeLG)
                        .clip(CircleShape)
                        .background(if (isFullySettled) StatusCompletedBg else TealLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFullySettled) Icons.Default.CheckCircle
                                      else expense.expenseCategory().toIcon(),
                        contentDescription = null,
                        tint = if (isFullySettled) StatusCompletedText else TealPrimary,
                        modifier = Modifier.size(Dimens.IconSizeMD)
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.SpaceMD))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = expense.title,
                        style = RoomieTypography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (isFullySettled) TextDecoration.LineThrough
                                         else TextDecoration.None
                    )
                    Text(
                        text = formatExpenseDate(expense.date),
                        style = RoomieTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Paid by $paidByName",
                        style = RoomieTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    when {
                        isFullySettled -> Text(
                            text = "Fully settled",
                            style = RoomieTypography.bodySmall,
                            color = StatusCompletedText
                        )
                        isSettledByMe -> Text(
                            text = "Your share settled",
                            style = RoomieTypography.bodySmall,
                            color = StatusCompletedText
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${"%.2f".format(expense.amount)} €",
                        style = RoomieTypography.titleSmall,
                        color = if (isFullySettled) MaterialTheme.colorScheme.onSurfaceVariant
                                else ExpenseRed
                    )
                    when {
                        !isSettledByMe && !isPayer && userShare > 0 -> Text(
                            text = "You owe €${"%.2f".format(userShare)}",
                            style = RoomieTypography.bodySmall,
                            color = ExpenseRed
                        )
                        isPayer && !isFullySettled -> Text(
                            text = "You paid",
                            style = RoomieTypography.bodySmall,
                            color = StatusCompletedText
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog.value) {
        DeleteConfirmDialog(
            title = "Delete expense?",
            message = "This action cannot be undone. The expense will be permanently removed.",
            onConfirm = {
                showDeleteDialog.value = false
                onDelete(expense)
            },
            onDismiss = { showDeleteDialog.value = false }
        )
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
