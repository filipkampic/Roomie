package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography

data class SummaryCardData(
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTint: Color,
    val value: String,
    val label: String,
    val valueColor: Color
)

@Composable
fun SummaryCard(data: SummaryCardData) {
    RoomieCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(Dimens.CardPadding)
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.IconSizeLG)
                    .clip(CircleShape)
                    .background(data.iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = data.iconTint,
                    modifier = Modifier.size(Dimens.IconSizeMD)
                )
            }
            Spacer(modifier = Modifier.height(Dimens.SpaceMD))
            Text(
                text = data.value,
                style = RoomieTypography.headlineMedium,
                color = data.valueColor
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
            Text(
                text = data.label,
                style = RoomieTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}