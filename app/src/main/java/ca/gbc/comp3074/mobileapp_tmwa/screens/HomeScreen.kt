package ca.gbc.comp3074.mobileapp_tmwa.screens

import android.R.attr.maxHeight
import android.R.attr.text
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.mobileapp_tmwa.components.CustomDatePicker
import ca.gbc.comp3074.mobileapp_tmwa.components.EventForm
import ca.gbc.comp3074.mobileapp_tmwa.components.HomeHeader
import ca.gbc.comp3074.mobileapp_tmwa.components.TimeTickIndicator
import com.example.compose.AppTheme
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onProfileClick: () -> Unit = {}) {
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEventForm by remember { mutableStateOf(false) }

    var currentTime by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalDateTime.now()
            delay(60_000L)
        }
    }

    Scaffold(
        topBar = {
            HomeHeader(
                onProfileClick = onProfileClick,
                onCalendarClick = { showDatePicker = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEventForm = !showEventForm }) {
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
                    selectedDate = date
                    showDatePicker = false
                }
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val totalHeight = maxHeight - 20.dp

                val secondsInDay = (24 * 60 * 60)
                val elapsedSeconds =
                    currentTime.hour * 3600 + currentTime.minute * 60 + currentTime.second
                val timeFraction = elapsedSeconds / secondsInDay.toFloat()

                val lineOffset: Dp = totalHeight * timeFraction + 10.dp

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
                        Text("$timeFraction")
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = lineOffset),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (showEventForm) {
                Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                    EventForm()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview(showBackground = true)
fun HomePreview() {
    AppTheme(darkTheme = true, dynamicColor = false) {
        HomeScreen()
    }
}
