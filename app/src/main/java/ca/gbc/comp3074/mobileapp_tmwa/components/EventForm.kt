package ca.gbc.comp3074.mobileapp_tmwa.components

import android.R.attr.checked
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun EventTemplateButton(
    text: String,
    onClick: () -> Unit,
    selected: Boolean = false,
) {
    Box(
        modifier = Modifier
            .background(
                color =
                    if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceDim,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick) // TODO: add this back
    ) {
        Text(
            text,
            color =
                if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
            modifier = Modifier.padding(10.dp, 4.dp),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventForm() {
    val templates = listOf("Default", "Task", "Deadline", "Meeting")
    var selectedTemplate by remember { mutableIntStateOf(0) }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Column {
            // header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    itemsIndexed(templates) { i, it ->
                        EventTemplateButton(
                            text = it,
                            onClick = { selectedTemplate = i },
                            selected = i == selectedTemplate
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.padding(end = 4.dp)
                )
            }

            // form fields
            val transparentTextFieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Create, contentDescription = "Date")
                    },
                    colors = transparentTextFieldColors
                )
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Menu, contentDescription = "Date")
                    },
                    colors = transparentTextFieldColors
                )

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TextButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Date")
                    }
                    Spacer(modifier = Modifier.width(64.dp))
                    TextButton(
                        onClick = { showDatePicker = true }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("End Date")
                    }
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Location") },
                        modifier = Modifier.weight(3f),
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = "Date")
                        },
                        colors = transparentTextFieldColors
                    )
                    var isRepeat by remember { mutableStateOf(false) }
                    Text("Repeat")
                    Switch(
                        modifier = Modifier.weight(1f),
                        checked = isRepeat,
                        onCheckedChange = { isRepeat = it }
                    )
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Create Event")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventFormPreview() {
    AppTheme(
        darkTheme = true,
        dynamicColor = false,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    HomeHeader()
                }

                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    EventForm()
                }
            }
        }
    }
}
