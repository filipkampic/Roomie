package com.roomie.app.features.chores.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        style = RoomieTypography.titleMedium,
        color = color,
        modifier = Modifier.padding(top = Dimens.SpaceMD, bottom = Dimens.SpaceXS)
    )
}
