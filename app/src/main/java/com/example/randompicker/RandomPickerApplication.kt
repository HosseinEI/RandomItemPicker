package com.example.randompicker

import android.app.Application
import com.example.randompicker.di.AppContainer

class RandomPickerApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
