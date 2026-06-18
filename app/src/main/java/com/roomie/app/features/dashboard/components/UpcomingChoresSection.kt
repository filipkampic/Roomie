package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography

data class UpcomingChoreItem(
    val title: String,
    val deadlineText: String,
    val status: ChoreStatus
)

@Composable
fun UpcomingChoresSection(
    chores: List<UpcomingChoreItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Upcoming Chores",
            style = RoomieTypography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(Dimens.SpaceMD))

        if (chores.isEmpty()) {
            Text(
                text = "No upcoming chores",
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
            ) {
                if (chores.size == 1) {
                    UpcomingChoreCard(
                        chore = chores[0],
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    chores.take(2).forEach { chore ->
                        UpcomingChoreCard(
                            chore = chore,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}