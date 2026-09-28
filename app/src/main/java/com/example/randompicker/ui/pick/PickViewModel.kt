package com.example.randompicker.ui.pick

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randompicker.data.local.Item
import com.example.randompicker.data.repository.ItemRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/** Everything the Pick screen needs to render at any moment. */
sealed interface PickUiState {
    data object Idle : PickUiState
    data object Shuffling : PickUiState
    data object EmptyPool : PickUiState
    data class Result(val item: Item) : PickUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class PickViewModel(private val repository: ItemRepository) : ViewModel() {

    val categories: StateFlow<List<String>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags.asStateFlow()

    // All items in the currently selected category, unfiltered by tags.
    private val itemsInCategory: StateFlow<List<Item>> = _selectedCategory
        .flatMapLatest { category ->
            if (category.isNullOrBlank()) flowOf(emptyList()) else repository.getItemsByCategory(category)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tag chips are built dynamically from whatever tags actually exist in this category.
    val availableTags: StateFlow<List<String>> = itemsInCategory
        .map { items -> items.asSequence().flatMap { it.tags }.distinct().sorted().toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // The AND-filtered pool: an item must carry every selected tag to qualify.
    val filteredItems: StateFlow<List<Item>> = combine(itemsInCategory, _selectedTags) { items, tags ->
        if (tags.isEmpty()) items else items.filter { item -> tags.all { it in item.tags } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow<PickUiState>(PickUiState.Idle)
    val uiState: StateFlow<PickUiState> = _uiState.asStateFlow()

    fun selectCategory(category: String) {
        if (category == _selectedCategory.value) return
        _selectedCategory.value = category
        _selectedTags.value = emptySet()
        _uiState.value = PickUiState.Idle
    }

    fun toggleTag(tag: String) {
        _selectedTags.update { current -> if (tag in current) current - tag else current + tag }
        _uiState.value = PickUiState.Idle
    }

    fun pickRandom() {
        val pool = filteredItems.value
        if (pool.isEmpty()) {
            _uiState.value = PickUiState.EmptyPool
            return
        }
        viewModelScope.launch {
            _uiState.value = PickUiState.Shuffling
            delay(1100.milliseconds) // lets the shuffle animation play before revealing the winner
            _uiState.value = PickUiState.Result(pool.random())
        }
    }
}
