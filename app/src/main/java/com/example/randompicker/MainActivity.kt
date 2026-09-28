package com.example.randompicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.randompicker.ui.RandomPickerApp
import com.example.randompicker.ui.theme.RandomPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as RandomPickerApplication).container.repository

        setContent {
            RandomPickerTheme {
                RandomPickerApp(repository = repository)
            }
        }
    }
}
