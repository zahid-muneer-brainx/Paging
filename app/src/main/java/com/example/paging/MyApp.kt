package com.example.paging

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApp : Application() {
    companion object{
        private lateinit var mApp: MyApp
        fun getApplication(): MyApp = mApp
        fun getTmContext(): Context = mApp.applicationContext
    }
    override fun onCreate() { mApp = this
        super.onCreate()
        setUpWorker()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun setUpWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()
        val networkRequest=PeriodicWorkRequest.Builder(MyWorker::class.java,15,TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueue(networkRequest)
    }
}