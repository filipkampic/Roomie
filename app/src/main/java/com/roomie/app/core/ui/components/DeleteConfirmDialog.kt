package com.roomie.app.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.theme.DestructiveRed
import com.roomie.app.core.ui.theme.DestructiveRedLight
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.SurfaceWhite

@Preview(showBackground = false)
@Composable
fun DeleteConfirmDialogPreview() {
    DeleteConfirmDialog(
        title = "Delete Item",
        message = "Are you sure you want to delete this item?",
        onConfirm = {},
        onDismiss = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoomieShapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = Dimens.CardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.CardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(Dimens.IconSizeXL),
                    shape = CircleShape,
                    color = DestructiveRedLight
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = DestructiveRed,
                        modifier = Modifier
                            .padding(Dimens.SpaceMD)
                            .size(Dimens.IconSizeMD)
                    )
                }

                Spacer(modifier = Modifier.height(Dimens.SpaceMD))

                Text(
                    text = title,
                    style = RoomieTypography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceSM))

                Text(
                    text = message,
                    style = RoomieTypography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceLG))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSM)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        shape = RoomieShapes.medium
                    ) {
                        Text(
                            text = "Cancel",
                            style = RoomieTypography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(Dimens.ButtonHeight),
                        shape = RoomieShapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DestructiveRed,
                            contentColor = SurfaceWhite
                        )
                    ) {
                        Text(
                            text = "Delete",
                            style = RoomieTypography.labelLarge
                        )
                    }
                }
            }
        }
    }
}
