package com.roomie.app.features.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.model.HouseholdNotification
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotificationHistoryItem(
    notification: HouseholdNotification
) {
    RoomieCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.senderName,
                    style = RoomieTypography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary
                )
                Text(
                    text = formatTimestamp(notification.timestamp),
                    style = RoomieTypography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
            Text(
                text = notification.message,
                style = RoomieTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
