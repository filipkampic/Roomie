package com.roomie.app.features.chores.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.features.chores.ChoreFilter

@Composable
fun ChoresEmptyState(filter: ChoreFilter) {
    val message = when (filter) {
        ChoreFilter.ALL -> "No chores yet.\nTap + to add the first one."
        ChoreFilter.PENDING -> "No pending chores."
        ChoreFilter.COMPLETED -> "No completed chores yet."
        ChoreFilter.OVERDUE -> "No overdue chores. Nice work!"
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = RoomieTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}