package com.wa.pranksound.utils

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.wa.pranksound.R
import kotlin.math.roundToInt

class RemoteConfig {

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    private val KEY_INTER = "inter_frequency"
    private var default_value_inter = 10000

    private val KEY_BANNER = "active_collapsible"
    private var default_value_banner = false

    private val KEY_AOA = "active_app_open"
    private var default_value_aoa = true


    fun initRemoteConfig() {
        val configSettings: FirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(3600)
            .build()

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    }
    fun isEnableAOA(): Boolean{
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                default_value_aoa = mFirebaseRemoteConfig.getBoolean(KEY_AOA)
                Log.d("test firebase", default_value_aoa.toString())
            }
        }
        return default_value_aoa
    }

    fun isEnableBanner(): Boolean{
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                default_value_banner = mFirebaseRemoteConfig.getBoolean(KEY_BANNER)
            }
        }
        return default_value_banner
    }

    fun timeDelayInter(): Int{
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful){
                default_value_inter = mFirebaseRemoteConfig.getDouble(KEY_INTER).roundToInt()
            }
        }
        return default_value_inter
    }

}