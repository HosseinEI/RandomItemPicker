package com.example.randompicker.ui.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.randompicker.data.local.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDialog(
    initialItem: Item? = null,
    existingCategories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, tags: List<String>) -> Unit,
) {
    var name by remember { mutableStateOf(initialItem?.name ?: "") }
    var category by remember { 
        mutableStateOf(initialItem?.listCategory ?: existingCategories.firstOrNull().orEmpty()) 
    }
    var tagsText by remember { 
        mutableStateOf(initialItem?.tags?.joinToString(", ") ?: "") 
    }
    var categoryMenuExpanded by remember { mutableStateOf(value = false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialItem == null) "Add Item" else "Edit Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                ExposedDropdownMenuBox(
                    expanded = categoryMenuExpanded,
                    onExpandedChange = { categoryMenuExpanded = it },
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("List category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    if (existingCategories.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = categoryMenuExpanded,
                            onDismissRequest = { categoryMenuExpanded = false },
                        ) {
                            existingCategories.forEach { existing ->
                                DropdownMenuItem(
                                    text = { Text(existing) },
                                    onClick = {
                                        category = existing
                                        categoryMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma separated)") },
                    placeholder = { Text("e.g. Cheap, Romantic") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val tags = tagsText.split(",")
                        .asSequence()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .toList()
                    onConfirm(name, category, tags)
                },
                enabled = name.isNotBlank() && category.isNotBlank(),
            ) {
                Text(if (initialItem == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
