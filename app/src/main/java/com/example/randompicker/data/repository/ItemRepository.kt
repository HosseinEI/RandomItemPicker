package com.example.randompicker.data.repository

import com.example.randompicker.data.local.Item
import com.example.randompicker.data.local.ItemDao
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for item data. Screens/ViewModels never talk to
 * ItemDao directly — everything goes through here.
 */
class ItemRepository(private val itemDao: ItemDao) {

    val allItems: Flow<List<Item>> = itemDao.getAllItems()
    val allCategories: Flow<List<String>> = itemDao.getAllCategories()

    fun getItemsByCategory(category: String): Flow<List<Item>> =
        itemDao.getItemsByCategory(category)

    suspend fun insertItem(item: Item) = itemDao.insertItem(item)
    suspend fun insertItems(items: List<Item>) = itemDao.insertItems(items)
    suspend fun updateItem(item: Item) = itemDao.updateItem(item)
    suspend fun deleteItem(item: Item) = itemDao.deleteItem(item)
    suspend fun deleteCategory(category: String) = itemDao.deleteCategory(category)
}
