package com.example.randompicker.ui.pick

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.randompicker.data.local.Item
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PickScreen(viewModel: PickViewModel) {
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val availableTags by viewModel.availableTags.collectAsState()
    val selectedTags by viewModel.selectedTags.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Auto-select the first category once data loads, so the screen isn't empty on first run.
    LaunchedEffect(categories) {
        if ((selectedCategory == null) && categories.isNotEmpty()) {
            viewModel.selectCategory(categories.first())
        }
    }

    if (categories.isEmpty()) {
        EmptyState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Pick a Category", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category) },
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        if (availableTags.isNotEmpty()) {
            Text("Filter by Tag", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                availableTags.forEach { tag ->
                    FilterChip(
                        selected = tag in selectedTags,
                        onClick = { viewModel.toggleTag(tag) },
                        label = { Text(tag) },
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Text(
            "${filteredItems.size} item${if (filteredItems.size == 1) "" else "s"} in pool",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            modifier = Modifier.weight(1f, fill = false).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            ResultArea(uiState = uiState, pool = filteredItems)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.pickRandom() },
            enabled = filteredItems.isNotEmpty() && (uiState !is PickUiState.Shuffling),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(Icons.Filled.Casino, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Pick Random", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "No lists yet.\nAdd items from the Manage Lists tab to get started.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp),
        )
    }
}

@Composable
private fun ResultArea(uiState: PickUiState, pool: List<Item>) {
    when (uiState) {
        is PickUiState.Idle -> MessageCard("Ready when you are")
        is PickUiState.EmptyPool -> MessageCard("No items match these filters.")
        is PickUiState.Shuffling -> ShufflingCard(pool)
        is PickUiState.Result -> ResultCard(uiState.item)
    }
}

@Composable
private fun MessageCard(message: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(message, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
    }
}

/** Rapidly cycles through candidate names before the winner is revealed — the "shuffle" effect. */
@Composable
private fun ShufflingCard(pool: List<Item>) {
    var displayName by remember(pool) { mutableStateOf(pool.randomOrNull()?.name.orEmpty()) }

    LaunchedEffect(pool) {
        while (isActive) {
            displayName = pool.random().name
            delay(70.milliseconds)
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.padding(32.dp).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ResultCard(item: Item) {
    var visible by remember(item) { mutableStateOf(value = false) }
    LaunchedEffect(item) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(initialScale = 0.75f) + fadeIn(),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    item.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                if (item.tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        item.tags.joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}
