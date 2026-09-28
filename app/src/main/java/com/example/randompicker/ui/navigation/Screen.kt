package com.example.randompicker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Casino
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Pick : Screen("pick", "Pick", Icons.Filled.Casino)
    data object Manage : Screen("manage", "Manage Lists", Icons.AutoMirrored.Filled.List)
}

val bottomNavItems = listOf(Screen.Pick, Screen.Manage)
