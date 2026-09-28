package com.example.randompicker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.randompicker.data.repository.ItemRepository
import com.example.randompicker.ui.manage.ManageViewModel
import com.example.randompicker.ui.pick.PickViewModel

class ViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(PickViewModel::class.java) -> PickViewModel(repository) as T
        modelClass.isAssignableFrom(ManageViewModel::class.java) -> ManageViewModel(repository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
