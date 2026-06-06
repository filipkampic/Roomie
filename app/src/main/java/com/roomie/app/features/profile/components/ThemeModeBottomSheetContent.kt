package com.roomie.app.features.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary
import com.roomie.app.data.repository.ThemeMode

@Composable
fun ThemeModeBottomSheetContent(
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit
) {
    data class ThemeOption(
        val mode: ThemeMode,
        val label: String,
        val subtitle: String,
        val icon: ImageVector
    )

    val options = listOf(
        ThemeOption(ThemeMode.LIGHT, "Light", "Always use light theme", Icons.Default.Brightness7),
        ThemeOption(ThemeMode.DARK, "Dark", "Always use dark theme", Icons.Default.Brightness4),
        ThemeOption(ThemeMode.SYSTEM, "System", "Follow device setting", Icons.Default.SettingsBrightness)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPadding)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Theme Mode",
            style = RoomieTypography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceMD))

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.SpaceSM),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = if (currentMode == option.mode) TealPrimary
                           else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.IconSizeMD)
                )
                Spacer(modifier = Modifier.width(Dimens.SpaceMD))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        style = RoomieTypography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = option.subtitle,
                        style = RoomieTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                RadioButton(
                    selected = currentMode == option.mode,
                    onClick = { onModeSelected(option.mode) },
                    colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                )
            }
        }
    }
}
