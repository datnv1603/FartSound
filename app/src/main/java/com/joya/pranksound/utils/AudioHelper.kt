package com.joya.pranksound.utils

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.provider.Settings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class AudioHelper(context: Context) {

    private var mContext: Context = context

    private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var maxVolume: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    fun setNotification(isTurnOff: Boolean) {
        val n = (mContext.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)) as NotificationManager
        if (n.isNotificationPolicyAccessGranted()) {
            if (isTurnOff && audioManager.ringerMode == AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE)
            } else if (!isTurnOff && audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            }
        } else {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            (mContext as Activity).startActivityForResult(intent, 111)
        }
    }

    fun setVolume(vol: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVolume * vol) / 100, 0)
    }



    suspend fun getVolumeFlow(volCallBack: (vol: Float)-> Unit) {
        mContext.musicVolumeFlow.collect {
            val volume = (it.toFloat() / maxVolume.toFloat()) * 100
            volCallBack.invoke(volume)
        }
    }

    val Context.musicVolumeFlow
        get() = callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    when (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 0)) {
                        AudioManager.STREAM_MUSIC -> trySend(
                            intent.getIntExtra(
                                "android.media.EXTRA_VOLUME_STREAM_VALUE",
                                0
                            )
                        )
                    }
                }
            }

            registerReceiver(receiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
            awaitClose { unregisterReceiver(receiver) }
        }


}