package ca.gbc.comp3074.mobileapp_tmwa.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import ca.gbc.comp3074.mobileapp_tmwa.AppDatabase
import ca.gbc.comp3074.mobileapp_tmwa.components.CustomDatePicker
import ca.gbc.comp3074.mobileapp_tmwa.components.EventDetailDialog
import ca.gbc.comp3074.mobileapp_tmwa.components.EventForm
import ca.gbc.comp3074.mobileapp_tmwa.components.HomeHeader
import ca.gbc.comp3074.mobileapp_tmwa.components.TimeTickIndicator
import ca.gbc.comp3074.mobileapp_tmwa.dao.EventDao
import ca.gbc.comp3074.mobileapp_tmwa.domain.model.EventEntity
import com.example.compose.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(onProfileClick: () -> Unit = {}, onShareClick: () -> Unit = {}) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEventForm by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventEntity?>(null) }
    var eventToEdit by remember { mutableStateOf<EventEntity?>(null) }

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val eventDao = db.eventDao()
    val coroutineScope = rememberCoroutineScope()

    var eventsForTheDay by remember { mutableStateOf(listOf<EventEntity>()) }

    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            eventsForTheDay = getEventInRange(eventDao, date)
        }
    }

    LaunchedEffect(Unit) {
        selectedDate = LocalDate.now()
        while (true) {
            currentTime = LocalDateTime.now()
            delay(60_000L)
        }
    }

    Scaffold(
        topBar = {
            HomeHeader(
                date = selectedDate?.let {
                    if (it == LocalDate.now()) {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM d")
                        "Today, ${it.format(formatter)}"
                    } else {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMM d")
                        it.format(formatter)
                    }
                }.toString(),
                onProfileClick = onProfileClick,
                onCalendarClick = { showDatePicker = true },
                onShareClick = onShareClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { 
                if (showEventForm) {
                    showEventForm = false
                    eventToEdit = null
                } else {
                    showEventForm = true
                }
            }) {
                if (!showEventForm) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                } else {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Down Arrow")
                }
            }
        },
    ) { innerPadding ->
        Box {
            CustomDatePicker(
                show = showDatePicker,
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    Log.d("Date", "Date selected: $date")
                    selectedDate = date
                    showDatePicker = false
                    coroutineScope.launch {
                        eventsForTheDay = getEventInRange(eventDao, date)
                    }
                }
            )

            if (selectedEvent != null) {
                EventDetailDialog(
                    event = selectedEvent!!,
                    onDismiss = { selectedEvent = null },
                    onDelete = {
                        coroutineScope.launch {
                            eventDao.deleteEvent(selectedEvent!!)
                            selectedEvent = null
                            eventsForTheDay = getEventInRange(eventDao, selectedDate ?: LocalDate.now())
                        }
                    },
                    onEdit = {
                        eventToEdit = selectedEvent
                        selectedEvent = null
                        showEventForm = true
                    }
                )
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val totalHeight = maxHeight - 20.dp
                val lineOffset: Dp = timeToLineOffset(currentTime, totalHeight)

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                ) {
                    TimeTickIndicator(
                        currentHour = currentTime.hour
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {

                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = lineOffset),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha=0.5f)
                )

                eventsForTheDay.forEach { event ->
                    val startLineOffset = timeToLineOffset(event.startDateTime, totalHeight) - 10.dp
                    val endLineOffset = timeToLineOffset(event.endDateTime, totalHeight) - 10.dp
                    val height = max(endLineOffset - startLineOffset, 20.dp)
                    val startOffset = 100.dp
                    val rectangleWidth = 80.dp
                    Column(
                        modifier = Modifier
                            .offset(x = rectangleWidth + startOffset + 10.dp, y = startLineOffset),
                    ) {
                        Text(
                            text = event.title,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                        Text(
                            text = event.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .offset(x = startOffset, y = startLineOffset + 4.dp)
                            .padding(top = 2.dp)
                            .width(rectangleWidth)
                            .height(height - 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (event.type.lowercase()) {
                                    "meeting" -> MaterialTheme.colorScheme.primary
                                    "task" -> MaterialTheme.colorScheme.secondary
                                    "schedule" -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .clickable(onClick = {
                                selectedEvent = event
                            }),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(top=2.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = event.type.uppercase(),
                                color = MaterialTheme.colorScheme.background,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            if (showEventForm) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        .clickable {
                            showEventForm = false
                            eventToEdit = null
                        }
                )
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    EventForm(
                        initialEvent = eventToEdit,
                        onCreateEvent = { formData ->
                            val event = if (eventToEdit != null) {
                                eventToEdit!!.copy(
                                    title = formData.title,
                                    description = formData.description,
                                    type = formData.type,
                                    startDateTime = formData.startDateTime,
                                    endDateTime = formData.endDateTime,
                                    isRepeat = formData.isRepeat
                                )
                            } else {
                                EventEntity(
                                    title = formData.title,
                                    description = formData.description,
                                    type = formData.type,
                                    startDateTime = formData.startDateTime,
                                    endDateTime = formData.endDateTime,
                                    isRepeat = formData.isRepeat
                                )
                            }

                            coroutineScope.launch {
                                if (eventToEdit != null) {
                                    eventDao.updateEvent(event)
                                } else {
                                    eventDao.insertEvent(event)
                                }
                                showEventForm = false
                                eventToEdit = null
                                eventsForTheDay =
                                    getEventInRange(eventDao, selectedDate ?: LocalDate.now())
                            }
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun getEventInRange(eventDao: EventDao, targetDate: LocalDate): List<EventEntity> {
    val allEvents = eventDao.getAllEvents()

    return allEvents.filter { event ->
        val start = event.startDateTime.toLocalDate()
        val end = event.endDateTime.toLocalDate()

        if (event.isRepeat) {
            val daysBetween = ChronoUnit.DAYS.between(start, targetDate)
            daysBetween >= 0 && (daysBetween % 7).toInt() == 0
        } else {
            !targetDate.isBefore(start) && !targetDate.isAfter(end)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun timeToLineOffset(currentTime: LocalDateTime, totalHeight: Dp): Dp {
    val secondsInDay = (24 * 60 * 60)
    val elapsedSeconds = currentTime.hour * 3600 + currentTime.minute * 60 + currentTime.second
    val timeFraction = elapsedSeconds / secondsInDay.toFloat()
    return totalHeight * timeFraction + 10.dp
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun HomePreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        HomeScreen()
    }
}
