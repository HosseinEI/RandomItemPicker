package com.example.randompicker.ui.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ImportDialog(
    existingCategories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (text: String, category: String, useCsv: Boolean) -> Unit,
) {
    var category by remember { mutableStateOf(existingCategories.firstOrNull().orEmpty()) }
    var bulkText by remember { mutableStateOf("") }
    var useCsv by remember { mutableStateOf(value = false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bulk Import") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("List category") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = useCsv, onCheckedChange = { useCsv = it })
                    Spacer(Modifier.width(8.dp))
                    Text(if (useCsv) "CSV mode (name,tag1,tag2)" else "Plain mode (name | tag1, tag2)")
                }

                OutlinedTextField(
                    value = bulkText,
                    onValueChange = { bulkText = it },
                    label = { Text("Paste items, one per line") },
                    placeholder = {
                        Text(
                            if (useCsv) "Pizza,Fast Food,Cheap\nSushi,Fancy"
                            else "Pizza | Fast Food, Cheap\nSushi | Fancy",
                        )
                    },
                    minLines = 5,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(bulkText, category, useCsv) },
                enabled = category.isNotBlank() && bulkText.isNotBlank(),
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
