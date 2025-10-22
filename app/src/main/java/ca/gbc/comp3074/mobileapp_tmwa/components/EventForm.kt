package ca.gbc.comp3074.mobileapp_tmwa.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class EventFormData(
    val title: String,
    val description: String,
    val type: String,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val isRepeat: Boolean,
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventForm(
    onCreateEvent: (EventFormData) -> Unit = {},
) {
    val templates = listOf("Default", "Task", "Schedule", "Meeting")
    var selectedTemplate by remember { mutableStateOf("Default") }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var startDateTime by remember { mutableStateOf<LocalDateTime?>(null) }
    var endDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    var isRepeat by remember { mutableStateOf(false) }
    var type by remember { mutableStateOf("") }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf<String?>(null) }
    var typeError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val dateTimerFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy\nhh:mm a")

    LaunchedEffect(selectedTemplate) {
        type = when (selectedTemplate) {
            "Default" -> ""
            else -> selectedTemplate
        }

        isRepeat = when (selectedTemplate) {
            "Schedule" -> true
            "Default" -> isRepeat
            else -> false
        }
    }

    LaunchedEffect(startDateTime) {
        if (endDateTime == null) {
            endDateTime = startDateTime
        }
    }

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
        CustomDateTimePicker(
            show = showStartDatePicker,
            onDismissRequest = { showStartDatePicker = false },
            onDateTimeSelected = { startDateTime = it }
        )

        CustomDateTimePicker(
            show = showEndDatePicker,
            onDismissRequest = { showEndDatePicker = false },
            onDateTimeSelected = { endDateTime = it }
        )

        Column {
            BadgeList(
                items = templates,
                selectedItem = selectedTemplate,
                onChange = { selectedTemplate = it },
                displayText = { it },
            )

            val transparentTextFieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )

            Column {
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    isError = titleError != null,
                    placeholder = { Text("Title...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Create, contentDescription = "Title") },
                    colors = transparentTextFieldColors
                )

                if (titleError != null) {
                    Text(
                        text = titleError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Menu, contentDescription = "Description") },
                    colors = transparentTextFieldColors
                )

                OutlinedTextField(
                    value = type,
                    onValueChange = {
                        type = it
                        typeError = null
                    },
                    isError = typeError != null,
                    placeholder = { Text("Event Type...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Event Type") },
                    colors = transparentTextFieldColors
                )

                if (typeError != null) {
                    Text(
                        text = typeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showStartDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(startDateTime?.format(dateTimerFormatter) ?: "Start Date")
                    }
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "To",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 4.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                    TextButton(onClick = { showEndDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Calendar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(endDateTime?.format(dateTimerFormatter) ?: "End Date")
                    }
                }
                if (dateError != null) {
                    Text(
                        text = dateError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                    )
                }

                HorizontalDivider()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Switch(
                        checked = isRepeat,
                        onCheckedChange = { isRepeat = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Repeat Weekly")

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            var valid = true
                            titleError = null
                            typeError = null
                            dateError = null

                            if (title.isBlank()) {
                                titleError = "Title is required"
                                valid = false
                            }
                            if (type.isBlank()) {
                                typeError = "Event type is required"
                                valid = false
                            }
                            if (startDateTime == null || endDateTime == null) {
                                dateError = "Please select both start and end dates"
                                valid = false
                            } else if (endDateTime!!.isBefore(startDateTime)) {
                                dateError = "End date must be after start date"
                                valid = false
                            } else {
                                dateError = null
                            }

                            if (valid && startDateTime != null && endDateTime != null) {
                                onCreateEvent(
                                    EventFormData(
                                        title = title,
                                        description = description,
                                        type = type,
                                        startDateTime = startDateTime!!,
                                        endDateTime = endDateTime!!,
                                        isRepeat = isRepeat,
                                    )
                                )
                            }
                        },
                    ) {
                        Text("Create Event")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                    HomeHeader(
                        onProfileClick = {},
                        onCalendarClick = {}
                    )
                }

                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    EventForm()
                }
            }
        }
    }
}
