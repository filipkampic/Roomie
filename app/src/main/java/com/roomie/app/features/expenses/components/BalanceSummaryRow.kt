package com.roomie.app.features.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.roomie.app.core.ui.components.RoomieCard
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.ExpenseRed
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.StatusCompletedText

@Composable
fun BalanceSummaryRow(
    youOwe: Double,
    youAreOwed: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceMD)
    ) {
        BalanceCard(
            label = "You owe",
            amount = youOwe,
            amountColor = ExpenseRed,
            modifier = Modifier.weight(1f)
        )
        BalanceCard(
            label = "You are owed",
            amount = youAreOwed,
            amountColor = StatusCompletedText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BalanceCard(
    label: String,
    amount: Double,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    RoomieCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = RoomieTypography.bodySmall,
                    color = amountColor
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceXS))
                Text(
                    text = "€${"%.2f".format(amount)}",
                    style = RoomieTypography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
