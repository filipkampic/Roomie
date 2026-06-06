package com.roomie.app.features.shopping.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roomie.app.core.ui.theme.NavyPrimary
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary

private val StepperButtonSize = 44.dp
private val QuantityDisplaySize = 72.dp

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StepperButton(
            onClick = onDecrement,
            enabled = quantity > 1
        ) {
            Text(
                text = "−",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = if (quantity > 1) SurfaceWhite
                else SurfaceWhite.copy(alpha = 0.5f)
            )
        }

        Surface(
            shape = RoomieShapes.medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(width = QuantityDisplaySize, height = StepperButtonSize)
        ) {
            Text(
                text = quantity.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
            )
        }

        StepperButton(onClick = onIncrement, enabled = true) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Increase",
                tint = SurfaceWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun StepperButton(
    onClick: () -> Unit,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (enabled) TealPrimary else TealPrimary.copy(alpha = 0.4f),
        modifier = Modifier.size(StepperButtonSize),
        onClick = onClick,
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}
