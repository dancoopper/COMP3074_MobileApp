package ca.gbc.comp3074.mobileapp_tmwa.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.mobileapp_tmwa.AppDatabase
import ca.gbc.comp3074.mobileapp_tmwa.domain.model.EventEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareAvailabilityScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val eventDao = db.eventDao()
    
    var eventsForTheDay by remember { mutableStateOf(listOf<EventEntity>()) }
    val selectedDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(selectedDate) {
        eventsForTheDay = getEventInRange(eventDao, selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Availability") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Timeline visualization
            // Simple 9 AM to 5 PM for prototype, or 24h? Let's do 8 AM to 8 PM for now.
            val startHour = 8
            val endHour = 20
            
            for (hour in startHour..endHour) {
                AvailabilityHourSlot(
                    hour = hour,
                    events = eventsForTheDay,
                    date = selectedDate
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AvailabilityHourSlot(
    hour: Int,
    events: List<EventEntity>,
    date: LocalDate
) {
    val hourStart = date.atTime(hour, 0)
    val hourEnd = date.atTime(hour + 1, 0)
    
    // Check if any event overlaps with this hour
    val isBusy = events.any { event ->
        val eventStart = event.startDateTime
        val eventEnd = event.endDateTime
        
        // Simple overlap check
        (eventStart.isBefore(hourEnd) && eventEnd.isAfter(hourStart))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = String.format("%02d:00", hour),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.width(50.dp)
        )
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .background(
                    color = if (isBusy) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) 
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isBusy) {
                Text(
                    text = "Busy",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Available",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
