package com.roomie.app.core.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.SurfaceWhite

@Preview(showBackground = false)
@Composable
fun RoomieCardPreview() {
    RoomieCard(
        modifier = Modifier,
        containerColor = SurfaceWhite,
        content = {}
    )
}

@Composable
fun RoomieCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            modifier = modifier,
            shape = RoomieShapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            onClick = onClick,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = RoomieShapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = Dimens.CardElevation),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            content = content
        )
    }
}
