package com.joya.pranksound.utils

import android.app.Application
import android.content.res.Configuration
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.joya.pranksound.R
import com.joya.pranksound.data.SharedPreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceHelper(this)
        loadConfig()
        initTrackingAdjust()
    }

    private fun loadConfig() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build()
                firebaseRemoteConfig.setConfigSettingsAsync(configSettings).await()
                firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults).await()
                firebaseRemoteConfig.fetchAndActivate().await()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initTrackingAdjust() {
        val appToken = "mrqc3c5bkow0"
        val environment = AdjustConfig.ENVIRONMENT_PRODUCTION
        val config = AdjustConfig(this, appToken, environment)
        config.setLogLevel(LogLevel.WARN)
        Adjust.onCreate(config)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        SystemUtil.setLocale(this)
    }
}