package com.roomie.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.InputBackground
import com.roomie.app.core.ui.theme.InputBorder
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary

@Preview(showBackground = false)
@Composable
fun CategoryChipPreview(modifier: Modifier = Modifier) {
    CategoryChip(
        label = "Category",
        selected = true,
        onClick = {},
        modifier = modifier
    )
}

@Composable
fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = RoomieTypography.labelMedium
            )
        },
        modifier = modifier,
        shape = RoomieShapes.small,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TealPrimary,
            selectedLabelColor = SurfaceWhite,
            containerColor = InputBackground,
            labelColor = NavySecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = InputBorder,
            selectedBorderColor = TealPrimary
        )
    )
}
