package com.roomie.app.features.chores.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.components.CategoryChip
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.features.chores.ChoreFilter

@Composable
fun FilterChipRow(
    activeFilter: ChoreFilter,
    onFilterSelected: (ChoreFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceSM),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
    ) {
        ChoreFilter.entries.forEach { filter ->
            CategoryChip(
                label = filter.label,
                selected = activeFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}