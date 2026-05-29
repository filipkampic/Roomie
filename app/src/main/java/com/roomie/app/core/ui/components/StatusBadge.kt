package com.roomie.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.roomie.app.core.ui.theme.Dimens
import com.roomie.app.core.ui.theme.RoomieTypography
import com.roomie.app.core.ui.theme.StatusCompletedBg
import com.roomie.app.core.ui.theme.StatusCompletedText
import com.roomie.app.core.ui.theme.StatusOverdueBg
import com.roomie.app.core.ui.theme.StatusOverdueText
import com.roomie.app.core.ui.theme.StatusPendingBg
import com.roomie.app.core.ui.theme.StatusPendingText

enum class ChoreStatus { PENDING, OVERDUE, COMPLETED }

@Preview(showBackground = false)
@Composable
fun StatusBadgePreview() {
    Column {
        StatusBadge(status = ChoreStatus.PENDING)
        StatusBadge(status = ChoreStatus.OVERDUE)
        StatusBadge(status = ChoreStatus.COMPLETED)
    }

}

@Composable
fun StatusBadge(status: ChoreStatus) {
    val (bg, text, label) = when (status) {
        ChoreStatus.PENDING   -> Triple(StatusPendingBg, StatusPendingText, "Pending")
        ChoreStatus.OVERDUE   -> Triple(StatusOverdueBg, StatusOverdueText, "Overdue")
        ChoreStatus.COMPLETED -> Triple(StatusCompletedBg, StatusCompletedText, "Completed")
    }
    Box(
        modifier = Modifier
            .background(
                color = bg,
                shape = RoundedCornerShape(Dimens.BadgeCornerRadius)
            )
            .padding(
                horizontal = Dimens.BadgePaddingHorizontal,
                vertical = Dimens.BadgePaddingVertical
            )
    ) {
        Text(
            text = label,
            style = RoomieTypography.labelSmall,
            color = text
        )
    }
}
