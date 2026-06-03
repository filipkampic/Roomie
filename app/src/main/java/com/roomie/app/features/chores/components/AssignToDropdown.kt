package com.roomie.app.features.chores.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.components.RoomieTextField
import com.roomie.app.core.ui.theme.RoomieTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignToDropdown(
    members: List<Pair<String, String>>,
    selected: Pair<String, String>?,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSelect: (Pair<String, String>) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandChange
    ) {
        RoomieTextField(
            value = selected?.second ?: "",
            onValueChange = {},
            placeholder = "Select member",
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            members.forEach { member ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = member.second,
                            style = RoomieTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { onSelect(member) }
                )
            }
        }
    }
}
