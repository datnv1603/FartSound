package com.wa.pranksound.utils

import android.app.Application
import com.wa.pranksound.data.SharedPreferenceHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceHelper(this)
    }
}