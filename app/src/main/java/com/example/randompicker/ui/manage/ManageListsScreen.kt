package com.example.randompicker.ui.manage

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.randompicker.data.local.Item

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageListsScreen(viewModel: ManageViewModel) {
    val items by viewModel.allItems.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(value = false) }
    var showImportDialog by remember { mutableStateOf(value = false) }
    var itemToEdit by remember { mutableStateOf<Item?>(value = null) }
    var expandedCategories by remember { mutableStateOf(value = setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Lists") },
                actions = {
                    IconButton(
                        onClick = {
                            val exported = viewModel.exportData()
                            if (exported.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, exported)
                                }
                                context.startActivity(Intent.createChooser(intent, "Export Lists"))
                            }
                        },
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = "Export items")
                    }
                    IconButton(onClick = { showImportDialog = true }) {
                        Icon(Icons.Filled.UploadFile, contentDescription = "Import items")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add item")
            }
        },
    ) { innerPadding ->
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("No items yet. Tap + to add one, or import a batch.")
            }
        } else {
            val grouped = items.groupBy { it.listCategory }
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                grouped.forEach { (category, categoryItems) ->
                    val isExpanded = category in expandedCategories
                    item(key = "header_$category") {
                        CategoryHeader(
                            category = category,
                            count = categoryItems.size,
                            isCollapsed = !isExpanded,
                            onToggle = {
                                expandedCategories = if (isExpanded) {
                                    expandedCategories - category
                                } else {
                                    expandedCategories + category
                                }
                            },
                        ) {
                            viewModel.deleteCategory(category)
                        }
                    }
                    if (isExpanded) {
                        items(categoryItems, key = { it.id }) { item ->
                            ItemRow(
                                item = item,
                                onEdit = { itemToEdit = item },
                            ) {
                                viewModel.deleteItem(item)
                            }
                        }
                    }
                    item(key = "spacer_$category") { Spacer(Modifier.height(12.dp)) }
                }
            }
        }
    }

    if (showAddDialog) {
        ItemDialog(
            existingCategories = categories,
            onDismiss = { showAddDialog = false },
        ) { name, category, tags ->
            viewModel.addItem(name, category, tags)
            showAddDialog = false
        }
    }

    itemToEdit?.let { item ->
        ItemDialog(
            initialItem = item,
            existingCategories = categories,
            onDismiss = { itemToEdit = null },
        ) { name, category, tags ->
            viewModel.updateItem(item, name, category, tags)
            itemToEdit = null
        }
    }

    if (showImportDialog) {
        ImportDialog(
            existingCategories = categories,
            onDismiss = { showImportDialog = false },
        ) { text, category, useCsv ->
            viewModel.importBulkText(text, category, useCsv)
            showImportDialog = false
        }
    }
}

@Composable
private fun CategoryHeader(
    category: String,
    count: Int,
    isCollapsed: Boolean,
    onToggle: () -> Unit,
    onDeleteCategory: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                if (isCollapsed) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                contentDescription = if (isCollapsed) "Expand" else "Collapse",
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "$category ($count)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onDeleteCategory,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete category $category",
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun ItemRow(item: Item, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyMedium)
                if (item.tags.isNotEmpty()) {
                    Text(
                        item.tags.joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit ${item.name}",
                        modifier = Modifier.size(18.dp),
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete ${item.name}",
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
