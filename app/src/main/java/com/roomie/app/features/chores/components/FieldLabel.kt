package com.roomie.app.features.chores.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.roomie.app.core.ui.theme.RoomieTypography

@Composable
fun FieldLabel(text: String) {
    Text(
        text = text,
        style = RoomieTypography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
}
