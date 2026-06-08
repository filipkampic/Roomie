package com.roomie.app.features.chores.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.StatusOverdueText
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.Chore
import com.roomie.app.features.chores.resolveStatus
import java.util.Calendar
import kotlin.collections.filter

private fun Chore.isToday(): Boolean {
    val now = Calendar.getInstance()
    val choreDay = Calendar.getInstance().apply { timeInMillis = deadline }
    return now.get(Calendar.YEAR) == choreDay.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == choreDay.get(Calendar.DAY_OF_YEAR)
}

private fun Chore.isUpcoming(): Boolean {
    val startOfTomorrow = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }.timeInMillis
    return !completed && deadline >= startOfTomorrow
}

@Composable
fun ChoresList(
    chores: List<Chore>,
    members: List<Pair<String, String>>,
    onToggle: (Chore) -> Unit,
    onDelete: (Chore) -> Unit,
    onEdit: (Chore) -> Unit
) {
    if (members.isEmpty()) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TealPrimary)
        }
    }

    val todayChores = chores.filter { !it.completed && it.resolveStatus() != ChoreStatus.OVERDUE && (it.isToday() || it.deadline == 0L) }
    val overdueChores = chores.filter { it.resolveStatus() == ChoreStatus.OVERDUE }
    val upcomingChores = chores.filter { it.isUpcoming() }
    val completedChores = chores.filter { it.completed }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = Dimens.ScreenPadding,
            end = Dimens.ScreenPadding,
            bottom = Dimens.SpaceXXL
        ),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
    ) {
        if (todayChores.isNotEmpty()) {
            item { SectionHeader("Today's Tasks", MaterialTheme.colorScheme.onBackground) }
            items(todayChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete, onEdit)
            }
        }
        if (overdueChores.isNotEmpty()) {
            item { SectionHeader("Overdue", StatusOverdueText) }
            items(overdueChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete, onEdit)
            }
        }
        if (upcomingChores.isNotEmpty()) {
            item { SectionHeader("Upcoming", NavySecondary) }
            items(upcomingChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete, onEdit)
            }
        }
        if (completedChores.isNotEmpty()) {
            item { SectionHeader("Completed", StatusCompletedText) }
            items(completedChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete, onEdit)
            }
        }
    }
}
