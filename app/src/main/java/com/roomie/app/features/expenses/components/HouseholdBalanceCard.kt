package com.roomie.app.features.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealDark
import com.roomie.app.core.ui.theme.TealPrimary

@Composable
fun HouseholdBalanceCard(
    totalExpenses: Double,
    userBalance: Double,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(listOf(TealPrimary, TealDark))
    val oweText = when {
        userBalance < 0 -> "You owe €${"%.2f".format(-userBalance)}"
        userBalance > 0 -> "You are owed €${"%.2f".format(userBalance)}"
        else -> "All settled up"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoomieShapes.large)
            .background(gradient)
            .padding(Dimens.CardPadding)
    ) {
        Column {
            Text(
                text = "Total Household Expenses",
                style = RoomieTypography.bodyMedium,
                color = SurfaceWhite.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceSM))
            Text(
                text = "€${"%.2f".format(totalExpenses)}",
                style = RoomieTypography.displaySmall,
                color = SurfaceWhite
            )
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
            Text(
                text = oweText,
                style = RoomieTypography.bodyMedium,
                color = SurfaceWhite.copy(alpha = 0.9f)
            )
        }
    }
}