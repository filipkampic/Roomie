package com.roomie.app.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.theme.InputBackground
import com.roomie.app.core.ui.theme.NavyPrimary
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.RoomieShapes
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary

@Preview(showBackground = false)
@Composable
fun RoomieTextFieldPreview() {
    RoomieTextField(
        value = "",
        onValueChange = {},
        placeholder = "Text...",
        modifier = Modifier
    )
}

@Composable
fun RoomieTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        placeholder = {
            Text(
                text = placeholder,
                style = RoomieTypography.bodyLarge,
                color = NavySecondary
            )
        },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        shape = RoomieShapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            disabledContainerColor = InputBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = NavyPrimary,
            unfocusedTextColor = NavyPrimary,
            disabledTextColor = NavyPrimary,
            cursorColor = TealPrimary,
            focusedPlaceholderColor = NavySecondary,
            unfocusedPlaceholderColor = NavySecondary,
            disabledPlaceholderColor = NavySecondary,
            disabledTrailingIconColor = TealPrimary
        ),
        textStyle = RoomieTypography.bodyLarge,
        modifier = modifier.fillMaxWidth()
    )
}
