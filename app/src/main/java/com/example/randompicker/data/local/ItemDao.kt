package com.example.randompicker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items ORDER BY listCategory, name")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT DISTINCT listCategory FROM items ORDER BY listCategory")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT * FROM items WHERE listCategory = :category ORDER BY name")
    fun getItemsByCategory(category: String): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("DELETE FROM items WHERE listCategory = :category")
    suspend fun deleteCategory(category: String)
}
