package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.components.ChoreStatus
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.components.StatusBadge
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography

@Preview(showBackground = false)
@Composable
fun UpcomingChoreCardPreview(modifier: Modifier = Modifier) {
    UpcomingChoreCard(
        chore = UpcomingChoreItem(
            title = "Chore Title",
            deadlineText = "Deadline Text",
            status = ChoreStatus.PENDING
        ),
        modifier = modifier
    )
}

@Composable
fun UpcomingChoreCard(
    chore: UpcomingChoreItem,
    modifier: Modifier = Modifier
) {
    RoomieCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = chore.title,
                style = RoomieTypography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            Text(
                text = chore.deadlineText,
                style = RoomieTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            StatusBadge(status = chore.status)
        }
    }
}