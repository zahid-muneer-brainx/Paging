package com.example.paging

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    companion object{
        private lateinit var mApp: MyApp
        fun getApplication(): MyApp = mApp
        fun getTmContext(): Context = mApp.applicationContext
    }
    override fun onCreate() { mApp = this
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}