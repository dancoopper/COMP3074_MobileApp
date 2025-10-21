package ca.gbc.comp3074.mobileapp_tmwa.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun TimeTickIndicator(
    currentHour: Int
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.End,
    ) {
        for (hour in 0..23) {
            Text(
                text = String.format(
                    "%d%s",
                    if (hour % 12 == 0) 12 else hour % 12,
                    if (hour < 12) " AM" else " PM"
                ),
                modifier = Modifier
                    .weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color =
                        if (currentHour == hour) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
        }
    }
}
