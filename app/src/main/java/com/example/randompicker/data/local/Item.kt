package com.example.randompicker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single pickable item. `tags` is a free-form list (e.g. "Cheap", "Romantic")
 * used to build the dynamic filter chips on the Pick screen.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val listCategory: String,
    val tags: List<String> = emptyList()
)
