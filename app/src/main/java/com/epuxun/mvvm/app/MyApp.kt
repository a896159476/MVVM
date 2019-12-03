package com.epuxun.mvvm.app

import android.app.Application
import com.epuxun.mvvm.utli.initApplication

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initApplication(this)
    }
}