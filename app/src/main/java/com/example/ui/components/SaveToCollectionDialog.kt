package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionEntity

@Composable
fun SaveToCollectionDialog(
    collections: List<CollectionEntity>,
    referenceLabel: String,
    onSaveToExisting: (collectionId: Long, note: String) -> Unit,
    onCreateAndSave: (name: String, note: String, colorHex: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCollectionId by remember {
        mutableLongStateOf(collections.firstOrNull()?.id ?: 1L)
    }
    var personalNote by remember { mutableStateOf("") }
    var isCreatingNew by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }
    var newCollectionColor by remember { mutableStateOf("#2563EB") }

    val presetColors = listOf("#E11D48", "#2563EB", "#059669", "#D97706", "#7C3AED", "#DB2777")

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("save_to_collection_dialog"),
        title = {
            Column {
                Text(
                    text = "Save to Collection",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = referenceLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isCreatingNew) {
                    Text(
                        text = "Choose a collection:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        items(collections, key = { it.id }) { col ->
                            val isSelected = col.id == selectedCollectionId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { selectedCollectionId = col.id }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedCollectionId = col.id }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = col.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { isCreatingNew = true },
                        modifier = Modifier.testTag("create_new_collection_inline")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Create New Collection")
                    }
                } else {
                    // Create New Collection Form
                    Text(
                        text = "New Collection Name:",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newCollectionName,
                        onValueChange = { newCollectionName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_collection_name_input"),
                        placeholder = { Text("e.g. Promises for Healing") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Theme Color:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        presetColors.forEach { hex ->
                            val isChosen = newCollectionColor == hex
                            val color = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Blue)
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        if (isChosen) 3.dp else 1.dp,
                                        if (isChosen) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { newCollectionColor = hex },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChosen) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    TextButton(onClick = { isCreatingNew = false }) {
                        Text("← Back to existing collections")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                // Personal Note input
                OutlinedTextField(
                    value = personalNote,
                    onValueChange = { personalNote = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("personal_note_input"),
                    label = { Text("Reflection / Note (optional)") },
                    placeholder = { Text("What did God speak to you through this?") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isCreatingNew) {
                        if (newCollectionName.isNotBlank()) {
                            onCreateAndSave(newCollectionName.trim(), personalNote.trim(), newCollectionColor)
                            onDismiss()
                        }
                    } else {
                        onSaveToExisting(selectedCollectionId, personalNote.trim())
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("confirm_save_to_collection")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
