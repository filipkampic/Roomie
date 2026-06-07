package com.roomie.app.features.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.notifications.QuickActionType

private fun QuickActionType.icon(): ImageVector = when (this) {
    QuickActionType.NEED_HELP -> Icons.Default.Help
    QuickActionType.GOING_SHOPPING -> Icons.Default.ShoppingCart
    QuickActionType.GUESTS_COMING -> Icons.Default.People
    QuickActionType.NEED_QUIET -> Icons.Default.MeetingRoom
}

@Composable
fun QuickActionButton(
    type: QuickActionType,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoomieCard(
        modifier = modifier.heightIn(min = 90.dp),
        onClick = if (isLoading) null else onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
        ) {
            Icon(
                imageVector = type.icon(),
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = type.label,
                style = RoomieTypography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
