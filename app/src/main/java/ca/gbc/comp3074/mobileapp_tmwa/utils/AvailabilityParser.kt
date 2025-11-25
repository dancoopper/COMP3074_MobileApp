package ca.gbc.comp3074.mobileapp_tmwa.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

data class ParsedAvailability(
    val date: LocalDate,
    val timeSlots: List<Pair<LocalTime, LocalTime>>
)

object AvailabilityParser {
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseAvailability(text: String): ParsedAvailability? {
        try {
            // Expected format: "I'm available on Tuesday, Nov 25 at:"
            // or just "Nov 25" somewhere in the first line
            
            val lines = text.lines()
            if (lines.isEmpty()) return null
            
            val header = lines.first()
            
            // Regex to find "MMM d" or "MMM dd"
            val dateRegex = Regex("([a-zA-Z]{3})\\s+(\\d{1,2})")
            val dateMatch = dateRegex.find(header) ?: return null
            
            val (monthStr, dayStr) = dateMatch.destructured
            
            // Parse date assuming current year
            val currentYear = LocalDate.now().year
            val dateStr = "$monthStr $dayStr $currentYear"
            val dateFormatter = DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)
            val date = LocalDate.parse(dateStr, dateFormatter)
            
            val timeSlots = mutableListOf<Pair<LocalTime, LocalTime>>()
            val timeRegex = Regex("(\\d{1,2}:\\d{2})\\s+to\\s+(\\d{1,2}:\\d{2})")
            
            // Process subsequent lines for time slots
            for (i in 1 until lines.size) {
                val line = lines[i].trim()
                val timeMatch = timeRegex.find(line)
                if (timeMatch != null) {
                    val (startStr, endStr) = timeMatch.destructured
                    val startTime = LocalTime.parse(startStr)
                    val endTime = LocalTime.parse(endStr)
                    timeSlots.add(Pair(startTime, endTime))
                }
            }
            
            if (timeSlots.isEmpty()) return null
            
            return ParsedAvailability(date, timeSlots)
            
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
