package com.roomie.app.features.chores.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.StatusOverdueText
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
    onDelete: (Chore) -> Unit
) {
    val todayChores = chores.filter { !it.completed && it.resolveStatus() != ChoreStatus.OVERDUE && it.isToday() }
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
                ChoreItem(chore, members, onToggle, onDelete)
            }
        }
        if (overdueChores.isNotEmpty()) {
            item { SectionHeader("Overdue", StatusOverdueText) }
            items(overdueChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete)
            }
        }
        if (upcomingChores.isNotEmpty()) {
            item { SectionHeader("Upcoming", NavySecondary) }
            items(upcomingChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete)
            }
        }
        if (completedChores.isNotEmpty()) {
            item { SectionHeader("Completed", StatusCompletedText) }
            items(completedChores, key = { it.id }) { chore ->
                ChoreItem(chore, members, onToggle, onDelete)
            }
        }
    }
}
