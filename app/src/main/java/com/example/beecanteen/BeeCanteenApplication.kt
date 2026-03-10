package com.example.beecanteen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BeeCanteenApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}