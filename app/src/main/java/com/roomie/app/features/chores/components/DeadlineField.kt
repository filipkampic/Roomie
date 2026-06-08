package com.roomie.app.features.chores.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.TealPrimary

@Composable
fun DeadlineField(
    deadlineMillis: Long?,
    onClick: () -> Unit
) {
    val displayText = deadlineMillis?.let { formatDeadline(it) } ?: "Select date & time"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
    ) {
        RoomieTextField(
            value = displayText,
            onValueChange = {},
            placeholder = "Select date & time",
            readOnly = true,
            enabled = false,
            trailingIcon = {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Pick deadline",
                    tint = TealPrimary
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
