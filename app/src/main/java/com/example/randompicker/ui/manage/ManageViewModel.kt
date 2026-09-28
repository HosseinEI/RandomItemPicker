package com.example.randompicker.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randompicker.data.local.Item
import com.example.randompicker.data.repository.ItemRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageViewModel(private val repository: ItemRepository) : ViewModel() {

    val allItems: StateFlow<List<Item>> = repository.allItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addItem(name: String, category: String, tags: List<String>) {
        if (name.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            repository.insertItem(Item(name = name.trim(), listCategory = category.trim(), tags = tags))
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch { repository.deleteItem(item) }
    }

    fun deleteCategory(category: String) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    fun updateItem(item: Item, name: String, category: String, tags: List<String>) {
        if (name.isBlank() || category.isBlank()) return
        viewModelScope.launch {
            repository.updateItem(item.copy(name = name.trim(), listCategory = category.trim(), tags = tags))
        }
    }

    /**
     * Two supported line formats, one item per line:
     *   Plain: "Item Name | tag1, tag2"   (the "| tags" part is optional)
     *   CSV:   "Item Name,tag1,tag2"
     */
    fun importBulkText(rawText: String, category: String, useCsv: Boolean) {
        if (category.isBlank()) return
        val trimmedCategory = category.trim()
        val lines = rawText.lines().asSequence().map { it.trim() }.filter { it.isNotEmpty() }.toList()

        val newItems = lines.mapNotNull { line ->
            if (useCsv) {
                val parts = line.split(",").map { it.trim() }
                val name = parts.firstOrNull()
                if (name.isNullOrBlank()) return@mapNotNull null
                Item(name = name, listCategory = trimmedCategory, tags = parts.asSequence().drop(1).filter { it.isNotEmpty() }.toList())
            } else {
                val segments = line.split("|")
                val name = segments.getOrNull(0)?.trim()
                if (name.isNullOrBlank()) return@mapNotNull null
                val tags = segments.getOrNull(1)
                    ?.split(",")
                    ?.asSequence()
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.toList()
                    ?: emptyList()
                Item(name = name, listCategory = trimmedCategory, tags = tags)
            }
        }

        if (newItems.isNotEmpty()) {
            viewModelScope.launch { repository.insertItems(newItems) }
        }
    }

    fun exportData(): String {
        val items = allItems.value
        if (items.isEmpty()) return ""
        
        return items.joinToString("\n") { item ->
            val tagsPart = if (item.tags.isNotEmpty()) " | ${item.tags.joinToString(", ")}" else ""
            "${item.listCategory}: ${item.name}$tagsPart"
        }
    }
}
