package com.example.randompicker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.randompicker.data.repository.ItemRepository
import com.example.randompicker.di.ViewModelFactory
import com.example.randompicker.ui.manage.ManageListsScreen
import com.example.randompicker.ui.manage.ManageViewModel
import com.example.randompicker.ui.navigation.Screen
import com.example.randompicker.ui.navigation.bottomNavItems
import com.example.randompicker.ui.pick.PickScreen
import com.example.randompicker.ui.pick.PickViewModel

@Composable
fun RandomPickerApp(repository: ItemRepository) {
    val navController = rememberNavController()
    val factory = remember { ViewModelFactory(repository) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Pick.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Pick.route) {
                val pickViewModel: PickViewModel = viewModel(factory = factory)
                PickScreen(viewModel = pickViewModel)
            }
            composable(Screen.Manage.route) {
                val manageViewModel: ManageViewModel = viewModel(factory = factory)
                ManageListsScreen(viewModel = manageViewModel)
            }
        }
    }
}
