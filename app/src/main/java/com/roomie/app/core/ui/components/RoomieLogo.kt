package com.roomie.app.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.roomie.app.R
import com.roomie.app.core.ui.theme.NavySecondary

@Composable
fun RoomieLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.ic_roomie_logo),
            contentDescription = null,
            modifier = Modifier.size(52.dp)
        )


        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Roomie",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Smart Roommate\nManagement App",
                style = MaterialTheme.typography.bodySmall,
                color = NavySecondary
            )
        }
    }
}