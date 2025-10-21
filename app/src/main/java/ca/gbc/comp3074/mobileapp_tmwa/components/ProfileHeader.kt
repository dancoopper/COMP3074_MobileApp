package ca.gbc.comp3074.mobileapp_tmwa.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.compose.AppTheme

@Composable
fun ProfileHeader(
    profileImageUri: Uri?,
    isEditing: Boolean,
    onImageClick: () -> Unit,
    onRemoveImage: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box {
            // Profile picture  with subtle border
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .clickable(enabled = isEditing) { showDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
            }

            // Edit icon when editing
            if (isEditing) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-8).dp, y = (-8).dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { showDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Picture",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    // Dialog for image options
    if (showDialog) {
        ProfileImageDialog(
            onDismiss = { showDialog = false },
            onSelectImage = {
                onImageClick()
                showDialog = false
            },
            onRemoveImage = {
                onRemoveImage()
                showDialog = false
            },
            hasImage = profileImageUri != null
        )
    }
}

@Composable
fun ProfileImageDialog(
    onDismiss: () -> Unit,
    onSelectImage: () -> Unit,
    onRemoveImage: () -> Unit,
    hasImage: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Profile Picture") },
        text = {
            Column {
                Text("Choose an option:")
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSelectImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose Image")
                }

                if (hasImage) {
                    OutlinedButton(
                        onClick = onRemoveImage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remove Image")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true, name = "Profile Header - No Image")
@Composable
fun ProfileHeaderPreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        ProfileHeader(
            profileImageUri = null,
            isEditing = false,
            onImageClick = {},
            onRemoveImage = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Header - Editing Mode")
@Composable
fun ProfileHeaderEditingPreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        ProfileHeader(
            profileImageUri = null,
            isEditing = true,
            onImageClick = {},
            onRemoveImage = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Image Dialog - No Image")
@Composable
fun ProfileImageDialogPreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        ProfileImageDialog(
            onDismiss = {},
            onSelectImage = {},
            onRemoveImage = {},
            hasImage = false
        )
    }
}

@Preview(showBackground = true, name = "Profile Image Dialog - With Image")
@Composable
fun ProfileImageDialogWithImagePreview() {
    AppTheme(darkTheme = false, dynamicColor = false) {
        ProfileImageDialog(
            onDismiss = {},
            onSelectImage = {},
            onRemoveImage = {},
            hasImage = true
        )
    }
}