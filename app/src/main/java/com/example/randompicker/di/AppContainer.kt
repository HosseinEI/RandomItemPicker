package com.example.randompicker.di

import android.content.Context
import com.example.randompicker.data.local.AppDatabase
import com.example.randompicker.data.repository.ItemRepository

/**
 * Minimal manual dependency container — no Hilt/Koin needed for an app
 * this size. If it grows, this is the one place to swap in a DI framework.
 */
class AppContainer(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    val repository = ItemRepository(database.itemDao())
}
