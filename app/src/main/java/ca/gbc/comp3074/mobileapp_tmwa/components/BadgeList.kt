package ca.gbc.comp3074.mobileapp_tmwa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> BadgeList(
    items: List<T>,
    selectedItem: T,
    onChange: (T) -> Unit,
    displayText: (T) -> String = { it.toString() }
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                Box(
                    modifier = Modifier
                        .background(
                            color =
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceDim,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(onClick = { onChange(item) })
                ) {
                    Text(
                        displayText(item),
                        modifier = Modifier.padding(10.dp, 4.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color =
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}