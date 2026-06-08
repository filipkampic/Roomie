package com.roomie.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite
import com.roomie.app.core.ui.theme.TealPrimary

@Preview(showBackground = false)
@Composable
fun RoomieButtonPreview() {
    RoomieButton(
        text = "Button",
        onClick = {},
        modifier = Modifier,
        enabled = true,
        isLoading = false
    )
}

@Composable
fun RoomieButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoomieShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary,
            contentColor = SurfaceWhite,
            disabledContainerColor = TealPrimary.copy(alpha = 0.5f),
            disabledContentColor = SurfaceWhite.copy(alpha = 0.7f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.ButtonHeight)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.IconSizeMD - 2.dp),
                strokeWidth = Dimens.SpaceXS / 2,
                color = SurfaceWhite
            )
        } else {
            Text(
                text = text,
                style = RoomieTypography.labelLarge
            )
        }
    }
}
