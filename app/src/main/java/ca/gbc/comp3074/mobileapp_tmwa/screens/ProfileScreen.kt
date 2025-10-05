package ca.gbc.comp3074.mobileapp_tmwa.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AppTheme
import ca.gbc.comp3074.mobileapp_tmwa.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProfileScreen(onNavigateHome: () -> Unit = {}) {
    var isEditing by remember { mutableStateOf(false) }

    // Hardcoded dummy info
    var name by remember { mutableStateOf("John Doe") }
    var username by remember { mutableStateOf("@john_doe") }
    var dob by remember { mutableStateOf("12 Mar 1995") }
    var gender by remember { mutableStateOf("Male") }
    var profession by remember { mutableStateOf("UI/UX Designer") }
    var email by remember { mutableStateOf("john.doe@email.com") }
    var phone by remember { mutableStateOf("+1 416 555 7842") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) { //Pencil Icon button for edit
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateHome,
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                text = { Text("Back to Home") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //  background + avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background), // <---- Can be edited
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberVectorPainter(Icons.Default.Person),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") }
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") }
                        )
                    } else {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = username,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Personal Info
            if (isEditing) {
                EditSection(
                    dob, gender, profession, email, phone,
                    onDob = { dob = it },
                    onGender = { gender = it },
                    onProfession = { profession = it },
                    onEmail = { email = it },
                    onPhone = { phone = it }
                )
            } else {
                InfoSectionCompact(
                    title = "Personal Info",
                    info = listOf(
                        "Date of Birth" to dob,
                        "Gender" to gender,
                        "Profession" to profession,
                        "Email" to email,
                        "Phone" to phone
                    )
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditSection(
    dob: String,
    gender: String,
    profession: String,
    email: String,
    phone: String,
    onDob: (String) -> Unit,
    onGender: (String) -> Unit,
    onProfession: (String) -> Unit,
    onEmail: (String) -> Unit,
    onPhone: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Personal Info", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = dob, onValueChange = onDob, label = { Text("Date of Birth") })
        OutlinedTextField(value = gender, onValueChange = onGender, label = { Text("Gender") })
        OutlinedTextField(value = profession, onValueChange = onProfession, label = { Text("Profession") })
        OutlinedTextField(value = email, onValueChange = onEmail, label = { Text("Email") })
        OutlinedTextField(value = phone, onValueChange = onPhone, label = { Text("Phone Number") })
    }
}

@Composable //Personal Info Listing with spacing
fun InfoSectionCompact(title: String, info: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(title, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            info.forEach { (label, value) ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Preview(showBackground = true) //Routing
@Composable
fun EditableProfilePreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        EditableProfileScreen()
    }
}