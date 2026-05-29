package com.roomie.app.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.R
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.NavySecondary
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.TealPrimary

@Preview(showBackground = false)
@Composable
fun RoomieLogoPreview() {
    RoomieLogo()
}

@Composable
fun RoomieLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.ic_roomie_logo),
            contentDescription = null,
            modifier = Modifier.size(Dimens.IconSizeXL - Dimens.SpaceXS)
        )

        Spacer(modifier = Modifier.width(Dimens.SpaceMD - Dimens.SpaceXS))

        Column {
            Text(
                text = "Roomie",
                style = RoomieTypography.headlineLarge,
                color = TealPrimary
            )
            Text(
                text = "Smart Roommate\nManagement App",
                style = RoomieTypography.bodySmall,
                color = NavySecondary
            )
        }
    }
}
