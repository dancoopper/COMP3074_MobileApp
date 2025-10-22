package ca.gbc.comp3074.mobileapp_tmwa.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
            items(items) {
                EventTemplateButton(
                    text = displayText(it),
                    onClick = { onChange(it) },
                    selected = it == selectedItem
                )
            }
        }
    }
}