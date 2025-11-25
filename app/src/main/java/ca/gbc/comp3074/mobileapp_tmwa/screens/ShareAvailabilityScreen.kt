package ca.gbc.comp3074.mobileapp_tmwa.screens

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import ca.gbc.comp3074.mobileapp_tmwa.utils.ParsedAvailability
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Track selected available slots (by hour)
    var selectedSlots by remember { mutableStateOf(setOf<Int>()) }
    
    // Track imported availability for Overlap/Booking mode
    var importedAvailability by remember { mutableStateOf<ParsedAvailability?>(null) }

    LaunchedEffect(selectedDate) {
        eventsForTheDay = getEventInRange(eventDao, selectedDate)
    }

    fun shareAvailability() {
        if (selectedSlots.isEmpty()) return

        val sortedSlots = selectedSlots.sorted()
        val dateStr = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))
        val sb = StringBuilder()
        
        if (importedAvailability == null) {
            sb.append("I'm available on $dateStr at:\n")
        } else {
            sb.append("I'd like to book time on $dateStr at:\n")
        }
        
        sortedSlots.forEach { hour ->
            sb.append("- ${String.format("%02d:00", hour)} to ${String.format("%02d:00", hour + 1)}\n")
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val title = if (importedAvailability == null) "Share Availability" else "Request Booking"
        val shareIntent = Intent.createChooser(sendIntent, title)
        context.startActivity(shareIntent)
    }

    var showImportDialog by remember { mutableStateOf(false) }

    if (showImportDialog) {
        ca.gbc.comp3074.mobileapp_tmwa.components.ImportAvailabilityDialog(
            onDismiss = { showImportDialog = false },
            onAvailabilityParsed = { parsed ->
                importedAvailability = parsed
                selectedDate = parsed.date
                selectedSlots = emptySet() // Reset selection
                showImportDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (importedAvailability == null) "Share Availability" else "Book Time") 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (importedAvailability != null) {
                            // Exit booking mode
                            importedAvailability = null
                            selectedDate = LocalDate.now()
                            selectedSlots = emptySet()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (importedAvailability == null) {
                        androidx.compose.material3.TextButton(onClick = { showImportDialog = true }) {
                            Text("Import")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedSlots.isNotEmpty()) {
                FloatingActionButton(onClick = { shareAvailability() }) {
                    if (importedAvailability == null) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    } else {
                        Icon(Icons.Default.Check, contentDescription = "Request Booking")
                    }
                }
            }
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
            
            Text(
                text = if (importedAvailability == null) 
                    "Tap available slots to select them for sharing."
                else 
                    "Select a time slot to request a booking.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timeline visualization
            val startHour = 8
            val endHour = 20
            
            for (hour in startHour..endHour) {
                // Determine availability based on mode
                var isImportedAvailable = true
                if (importedAvailability != null) {
                    // Check if this hour is in the imported list
                    val hourStart = LocalTime.of(hour, 0)
                    isImportedAvailable = importedAvailability!!.timeSlots.any { (start, end) ->
                        !hourStart.isBefore(start) && hourStart.isBefore(end)
                    }
                }

                AvailabilityHourSlot(
                    hour = hour,
                    events = eventsForTheDay,
                    date = selectedDate,
                    isSelected = selectedSlots.contains(hour),
                    isImportedAvailable = isImportedAvailable,
                    isBookingMode = importedAvailability != null,
                    onSlotClick = {
                        selectedSlots = if (selectedSlots.contains(hour)) {
                            selectedSlots - hour
                        } else {
                            selectedSlots + hour
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AvailabilityHourSlot(
    hour: Int,
    events: List<EventEntity>,
    date: LocalDate,
    isSelected: Boolean,
    isImportedAvailable: Boolean = true, // Default to true for Share mode
    isBookingMode: Boolean = false,
    onSlotClick: () -> Unit
) {
    val hourStart = date.atTime(hour, 0)
    val hourEnd = date.atTime(hour + 1, 0)
    
    // Check if any event overlaps with this hour
    val overlappingEvents = events.filter { event ->
        val eventStart = if (event.isRepeat) {
            date.atTime(event.startDateTime.toLocalTime())
        } else {
            event.startDateTime
        }
        
        val eventEnd = if (event.isRepeat) {
            // Calculate duration to correctly handle end time
            val duration = java.time.Duration.between(event.startDateTime, event.endDateTime)
            eventStart.plus(duration)
        } else {
            event.endDateTime
        }
        
        (eventStart.isBefore(hourEnd) && eventEnd.isAfter(hourStart))
    }
    
    val isLocalBusy = overlappingEvents.isNotEmpty()
    
    // In Booking Mode (isImportedAvailable is relevant):
    // - Available if: isImportedAvailable AND !isLocalBusy
    // - Unavailable if: !isImportedAvailable OR isLocalBusy
    
    val isSlotAvailable = isImportedAvailable && !isLocalBusy
    val isClickable = isSlotAvailable

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                    color = when {
                        isBookingMode && !isImportedAvailable -> MaterialTheme.colorScheme.surfaceVariant // Booking: Unavailable (Grey)
                        isLocalBusy -> if (isBookingMode) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) // Busy/Conflict
                        isSelected -> if (isBookingMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer // Selected
                        else -> if (isBookingMode) Color.White else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) // Available
                    },
                    shape = MaterialTheme.shapes.small
                )
                .clickable(enabled = isClickable) { onSlotClick() },
            contentAlignment = Alignment.CenterStart
        ) {
            if (!isImportedAvailable) {
                 Text(
                    text = "Unavailable",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (isLocalBusy) {
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = if (isBookingMode) "Conflict" else "Busy",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = overlappingEvents.first().title,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSelected) "Selected" else "Available",
                        color = when {
                            isSelected -> if (isBookingMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                            isBookingMode -> Color.Black // White background -> Black text
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        modifier = Modifier.padding(start = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
