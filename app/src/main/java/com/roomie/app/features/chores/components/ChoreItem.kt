package com.roomie.app.features.chores.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.components.DeleteConfirmDialog
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.components.StatusBadge
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.data.model.Chore
import com.roomie.app.features.chores.resolveStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ChoreItem(
    chore: Chore,
    members: List<Pair<String, String>>,
    onToggle: (Chore) -> Unit,
    onDelete: (Chore) -> Unit,
    onEdit: (Chore) -> Unit
) {
    val showDeleteDialog = remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart && !showDeleteDialog.value) {
                showDeleteDialog.value = true
            }
            false
        },
        positionalThreshold = { it * 0.4f }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            SwipeDeleteBackground()
        }
    ) {
        ChoreItemContent(chore, members, onToggle, onEdit)
    }

    if (showDeleteDialog.value) {
        DeleteConfirmDialog(
            title = "Delete chore?",
            message = "This action cannot be undone. The chore will be permanently removed.",
            onConfirm = {
                showDeleteDialog.value = false
                onDelete(chore)
            },
            onDismiss = {
                showDeleteDialog.value = false
            }
        )
    }
}

@Composable
private fun SwipeDeleteBackground() {
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

@Composable
private fun ChoreItemContent (
    chore: Chore,
    members: List<Pair<String, String>>,
    onToggle: (Chore) -> Unit,
    onEdit: (Chore) -> Unit
) {
    val status = chore.resolveStatus()
    val assignedName = members.find { it.first == chore.assignedTo }?.second ?: chore.assignedTo
    val deadlineText = formatDeadline(chore.deadline)

    RoomieCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(chore) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onToggle(chore) },
                modifier = Modifier.size(Dimens.IconSizeMD + 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Toggle complete",
                    tint = if (status == ChoreStatus.COMPLETED) StatusCompletedText
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceSM))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chore.title,
                    style = RoomieTypography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (status == ChoreStatus.COMPLETED)
                        TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(Dimens.SpaceXS))
                Text(
                    text = if (status == ChoreStatus.COMPLETED)
                        "Done • $deadlineText"
                    else
                        "Assigned to $assignedName • $deadlineText",
                    style = RoomieTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceSM))

            StatusBadge(status = status)
        }
    }
}


fun formatDeadline(deadline: Long): String {
    if (deadline == 0L) return "No deadline"
    val now = Calendar.getInstance()
    val choreDay = Calendar.getInstance().apply { timeInMillis = deadline }
    val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(deadline))
    return when {
        now.get(Calendar.DAY_OF_YEAR) == choreDay.get(Calendar.DAY_OF_YEAR) &&
                now.get(Calendar.YEAR) == choreDay.get(Calendar.YEAR) -> "Today • $timeStr"
        now.get(Calendar.DAY_OF_YEAR) + 1 == choreDay.get(Calendar.DAY_OF_YEAR) &&
                now.get(Calendar.YEAR) == choreDay.get(Calendar.YEAR) -> "Tomorrow • $timeStr"
        choreDay.before(now) -> {
            val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(deadline))
            "$dateStr • $timeStr"
        }
        else -> {
            val dateStr = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(deadline))
            "$dateStr • $timeStr"
        }
    }
}
