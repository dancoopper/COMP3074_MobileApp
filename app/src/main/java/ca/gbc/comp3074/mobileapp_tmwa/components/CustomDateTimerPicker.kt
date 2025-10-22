package ca.gbc.comp3074.mobileapp_tmwa.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import java.time.*

enum class StageType {
    DATE,
    TIME
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDateTimePicker(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    if (!show) return

    var step by remember { mutableStateOf<StageType>(StageType.DATE) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    if (step == StageType.DATE) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    if (datePickerState.selectedDateMillis != null) {
                        step = StageType.TIME
                    } else {
                        onDismissRequest()
                    }
                }) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (step == StageType.TIME) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val selectedTime = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        val dateTime = LocalDateTime.of(selectedDate, selectedTime)
                        onDateTimeSelected(dateTime)
                    }
                    onDismissRequest()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    step = StageType.DATE
                }) {
                    Text("Back")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Time", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}
