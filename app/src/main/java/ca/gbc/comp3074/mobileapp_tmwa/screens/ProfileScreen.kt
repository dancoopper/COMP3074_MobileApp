package ca.gbc.comp3074.mobileapp_tmwa.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.mobileapp_tmwa.components.EditSection
import ca.gbc.comp3074.mobileapp_tmwa.components.InfoSectionCompact
import ca.gbc.comp3074.mobileapp_tmwa.components.ProfileHeader
import ca.gbc.comp3074.mobileapp_tmwa.data.ProfileRepository
import ca.gbc.comp3074.mobileapp_tmwa.data.ProfileUi
import com.example.compose.AppTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateHome: () -> Unit = {}) {
    val context = LocalContext.current
    val repo = remember { ProfileRepository(context) } // Persisted storage repo
    val scope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Load persisted profile (emits defaults first, then stored values)
    val persistedProfile by repo.profileFlow.collectAsState(initial = ProfileUi())

    // Saved user profile state
    var savedFullName by remember { mutableStateOf(persistedProfile.fullName) }
    var savedUsername by remember { mutableStateOf(persistedProfile.username) }
    var savedDateOfBirth by remember { mutableStateOf(persistedProfile.dateOfBirth) }
    var savedGender by remember { mutableStateOf(persistedProfile.gender) }
    var savedProfession by remember { mutableStateOf(persistedProfile.profession) }
    var savedEmailAddress by remember { mutableStateOf(persistedProfile.email) }
    var savedPhoneNumber by remember { mutableStateOf(persistedProfile.phone) }
    var savedProfileImageUri by remember { mutableStateOf<Uri?>(persistedProfile.imageUri?.let(Uri::parse)) }

    // Temporary editing state (declare BEFORE LaunchedEffect so it can be referenced there)
    var editingFullName by remember { mutableStateOf(savedFullName) }
    var editingUsername by remember { mutableStateOf(savedUsername) }
    var editingDateOfBirth by remember { mutableStateOf(savedDateOfBirth) }
    var editingGender by remember { mutableStateOf(savedGender) }
    var editingProfession by remember { mutableStateOf(savedProfession) }
    var editingEmailAddress by remember { mutableStateOf(savedEmailAddress) }
    var editingPhoneNumber by remember { mutableStateOf(savedPhoneNumber) }
    var editingProfileImageUri by remember { mutableStateOf(savedProfileImageUri) }

    // Keep saved in sync when DataStore emits using StoreData
    LaunchedEffect(persistedProfile) {
        savedFullName = persistedProfile.fullName
        savedUsername = persistedProfile.username
        savedDateOfBirth = persistedProfile.dateOfBirth
        savedGender = persistedProfile.gender
        savedProfession = persistedProfile.profession
        savedEmailAddress = persistedProfile.email
        savedPhoneNumber = persistedProfile.phone
        savedProfileImageUri = persistedProfile.imageUri?.let(Uri::parse)
        if (!isEditing) {
            editingProfileImageUri = savedProfileImageUri
        }
    }

    // Date picker state with constraint to only allow past dates
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Only allow dates up to today no future dates because how can people be born in the future
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            editingProfileImageUri = it
        }
    }

    // Save function
    fun saveChanges() {
        savedFullName = editingFullName
        savedUsername = editingUsername
        savedDateOfBirth = editingDateOfBirth
        savedGender = editingGender
        savedProfession = editingProfession
        savedEmailAddress = editingEmailAddress
        savedPhoneNumber = editingPhoneNumber
        savedProfileImageUri = editingProfileImageUri

        // Persist to DataStore
        scope.launch {
            repo.saveProfile(
                ProfileUi(
                    fullName = savedFullName,
                    username = savedUsername,
                    dateOfBirth = savedDateOfBirth,
                    gender = savedGender,
                    profession = savedProfession,
                    email = savedEmailAddress,
                    phone = savedPhoneNumber,
                    imageUri = savedProfileImageUri?.toString()
                )
            )
        }
        isEditing = false
    }

    // Cancel function
    fun cancelChanges() {
        editingFullName = savedFullName
        editingUsername = savedUsername
        editingDateOfBirth = savedDateOfBirth
        editingGender = savedGender
        editingProfession = savedProfession
        editingEmailAddress = savedEmailAddress
        editingPhoneNumber = savedPhoneNumber
        editingProfileImageUri = savedProfileImageUri
        isEditing = false
    }

    // Start editing function
    fun startEditing() {
        editingFullName = savedFullName
        editingUsername = savedUsername
        editingDateOfBirth = savedDateOfBirth
        editingGender = savedGender
        editingProfession = savedProfession
        editingEmailAddress = savedEmailAddress
        editingPhoneNumber = savedPhoneNumber
        editingProfileImageUri = savedProfileImageUri
        isEditing = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { startEditing() }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateHome,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home"
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile header with avatar
            ProfileHeader(
                profileImageUri = if (isEditing) editingProfileImageUri else savedProfileImageUri,
                isEditing = isEditing,
                onImageClick = {
                    imagePickerLauncher.launch("image/*")
                },
                onRemoveImage = {
                    editingProfileImageUri = null
                }
            )

            Spacer(Modifier.height(32.dp))

            // Personal information section (edit or display mode)
            if (isEditing) {
                EditSection(
                    fullName = editingFullName,
                    username = editingUsername,
                    dateOfBirth = editingDateOfBirth,
                    gender = editingGender,
                    profession = editingProfession,
                    emailAddress = editingEmailAddress,
                    phoneNumber = editingPhoneNumber,
                    onFullNameChange = { editingFullName = it },
                    onUsernameChange = { editingUsername = it },
                    onDateOfBirthChange = { editingDateOfBirth = it },
                    onGenderChange = { editingGender = it },
                    onProfessionChange = { editingProfession = it },
                    onEmailAddressChange = { editingEmailAddress = it },
                    onPhoneNumberChange = { editingPhoneNumber = it },
                    onSave = { saveChanges() },
                    onCancel = { cancelChanges() },
                    onDatePickerClick = { showDatePicker = true }
                )
            } else {
                InfoSectionCompact(
                    title = "Personal Information",
                    information = listOf(
                        "Full Name" to savedFullName,
                        "Username" to savedUsername,
                        "Date of Birth" to savedDateOfBirth,
                        "Gender" to savedGender,
                        "Profession" to savedProfession,
                        "Email Address" to savedEmailAddress,
                        "Phone Number" to savedPhoneNumber
                    )
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    // Date Picker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            editingDateOfBirth = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
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
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        ProfileScreen()
    }
}