package com.roomie.app.features.dashboard.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import java.util.Calendar

@Composable
fun DashboardHeader(
    userName: String,
    householdName: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Good ${timeOfDayGreeting()}, $userName",
            style = RoomieTypography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (householdName.isNotBlank()) {
            Spacer(modifier = Modifier.height(Dimens.SpaceXS))
            Text(
                text = householdName,
                style = RoomieTypography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun timeOfDayGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "morning"
        in 12..17 -> "afternoon"
        else -> "evening"
    }
}
