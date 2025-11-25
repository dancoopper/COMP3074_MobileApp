package ca.gbc.comp3074.mobileapp_tmwa.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme

@Composable
fun InfoSectionCompact(
    title: String,
    information: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(24.dp))

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                information.forEach { (label, value) ->
                    InfoRow(label = label, value = value)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon based on label
        val icon = getIconForLabel(label)
        
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun getIconForLabel(label: String): ImageVector {
    return when (label) {
        "Full Name" -> Icons.Default.Person
        "Username" -> Icons.Default.AccountCircle
        "Date of Birth" -> Icons.Default.DateRange
        "Gender" -> Icons.Default.Face
        "Profession" -> Icons.Default.AccountBox
        "Email Address" -> Icons.Default.Email
        "Phone Number" -> Icons.Default.Phone
        else -> Icons.Default.Info
    }
}

@Preview(showBackground = true)
@Composable
fun InfoSectionCompactPreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        InfoSectionCompact(
            title = "Personal Information",
            information = listOf(
                "Full Name" to "John Doe",
                "Username" to "@john_doe",
                "Date of Birth" to "12 Mar 1995",
                "Gender" to "Male",
                "Profession" to "UI/UX Designer",
                "Email Address" to "john.doe@email.com",
                "Phone Number" to "+1 416 555 7842"
            )
        )
    }
}