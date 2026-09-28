package com.example.randompicker.data.local

import androidx.room.TypeConverter

/**
 * Room can't persist a List<String> natively, so we flatten it to a single
 * delimited string for storage and split it back out on read. The delimiter
 * is a rare Unicode character so it won't collide with real tag text.
 */
class Converters {
    private val delimiter = "‧"

    @TypeConverter
    fun fromTagList(tags: List<String>): String = tags.joinToString(delimiter)

    @TypeConverter
    fun toTagList(data: String): List<String> =
        if (data.isBlank()) emptyList() else data.split(delimiter)
}
