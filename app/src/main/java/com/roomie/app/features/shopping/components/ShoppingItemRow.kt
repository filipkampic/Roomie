package com.roomie.app.features.shopping.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.components.DeleteConfirmDialog
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.DestructiveRedLight
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.StatusCompletedBg
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    addedByName: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog.value = true
            }
            false
        },
        positionalThreshold = { it * 0.4f }
    )

    if (showDeleteDialog.value) {
        DeleteConfirmDialog(
            title = "Delete item?",
            message = "This action cannot be undone. The item will be permanently removed.",
            onConfirm = {
                showDeleteDialog.value = false
                onDelete()
            },
            onDismiss = { showDeleteDialog.value = false }
        )
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                    DestructiveRedLight else Color.Transparent,
                label = "swipe_bg"
            )
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoomieShapes.large,
                color = color
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(end = Dimens.SpaceLG),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = DestructiveRed
                    )
                }
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoomieShapes.large,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp,
            onClick = onToggle
        ) {
            Row(
                modifier = Modifier.padding(Dimens.CardPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoomieShapes.medium,
                    color = if (item.completed) StatusCompletedBg
                    else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (item.completed) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Purchased",
                                tint = StatusCompletedText,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "To buy",
                                tint = TealPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(Dimens.SpaceMD))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = if (item.completed) TextDecoration.LineThrough
                            else TextDecoration.None,
                            color = if (item.completed) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Added by $addedByName",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(Modifier.width(Dimens.SpaceSM))

                Surface(
                    shape = RoomieShapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "x${item.quantity}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = Dimens.SpaceSM, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
