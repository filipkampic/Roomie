package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.DestructiveRedLight
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.ExpenseRed
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealLight
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.features.dashboard.DashboardSummaryState

@Composable
fun DashboardSummaryGrid(summaryState: DashboardSummaryState) {
    val cards = listOf(
        SummaryCardData(
            icon = Icons.Outlined.CheckCircle,
            iconBgColor = TealLight,
            iconTint = TealPrimary,
            value = summaryState.pendingChores.toString(),
            label = "Pending Chores",
            valueColor = MaterialTheme.colorScheme.onSurface
        ),
        SummaryCardData(
            icon = Icons.Outlined.Wallet,
            iconBgColor = TealPrimary,
            iconTint = SurfaceWhite,
            value = when {
                summaryState.netBalance > 0 -> "€${"%.0f".format(summaryState.netBalance)}"
                summaryState.netBalance < 0 -> "€${"%.0f".format(-summaryState.netBalance)}"
                else -> "€0"
            },
            label = when {
                summaryState.netBalance > 0 -> "You are owed"
                summaryState.netBalance < 0 -> "You owe"
                else -> "All settled"
            },
            valueColor = when {
                summaryState.netBalance < 0 -> ExpenseRed
                summaryState.netBalance > 0 -> StatusCompletedText
                else -> MaterialTheme.colorScheme.onSurface
            }
        ),
        SummaryCardData(
            icon = Icons.Outlined.ShoppingBag,
            iconBgColor = Color.Transparent,
            iconTint = TealPrimary,
            value = "${summaryState.shoppingItems} Items",
            label = "Shopping List",
            valueColor = MaterialTheme.colorScheme.onSurface
        ),
        SummaryCardData(
            icon = Icons.Outlined.Warning,
            iconBgColor = DestructiveRedLight,
            iconTint = DestructiveRed,
            value = "${summaryState.overdueChores} Overdue",
            label = "Delayed Tasks",
            valueColor = DestructiveRed
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMD),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceMD),
        userScrollEnabled = false
    ) {
        items(cards) { card ->
            SummaryCard(data = card)
        }
    }
}
