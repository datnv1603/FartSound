package com.wa.pranksound.ui.component.splash

import android.content.Context
import android.os.CountDownTimer
import android.view.LayoutInflater
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.pranksound.R
import com.wa.pranksound.ui.base.BaseViewModel
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.ads.NativeAdsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {
    private var timer: CountDownTimer? = null
    private var _isCompleteMutableLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isCompleteLiveData: LiveData<Boolean>
        get() = _isCompleteMutableLiveData

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
        viewModelScope.cancel()
    }
    private fun createCountDownTimer(time : Long): CountDownTimer {
        return object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                _isCompleteMutableLiveData.postValue(true)
            }
        }
    }

    fun starTimeCount(time : Long) {
        kotlin.runCatching {
            timer?.cancel()
            timer = createCountDownTimer(time)
            timer?.start()
        }.onFailure {
            it.printStackTrace()
        }
    }
    fun loadAds(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loadNativeIntro(context)
            loadNativeHome(context)
        }
    }

    private val _nativeAdIntro: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdIntro: LiveData<NativeAdView>
        get() = _nativeAdIntro

    private val _nativeAdHome: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdHome: LiveData<NativeAdView>
        get() = _nativeAdHome

    private val _nativeAdDialog: MutableLiveData<NativeAdView> = MutableLiveData()
    val nativeAdDialog: LiveData<NativeAdView>
        get() = _nativeAdDialog

    private fun loadNativeHome(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_HOME)) {
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.NATIVE_HOME)
            loadNativeContent(context = context, keyAdNativeAllPrice, _nativeAdHome)
        }
    }

    private fun loadNativeIntro(context: Context) {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)) {
            val keyAdNativeAllPrice = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.NATIVE_INTRO)
            loadNativeVideo(context = context, keyAdNativeAllPrice, _nativeAdIntro)
        }
    }

    private fun loadNativeVideo(context: Context, keyAd : String, nativeAd: MutableLiveData<NativeAdView>) {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAds(
            context,
            keyAd
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_video, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
                nativeAd.postValue(adView)
            }
        }
    }

    private fun loadNativeContent(context: Context, keyAd : String, nativeAd: MutableLiveData<NativeAdView>) {
        val adView = NativeAdView(context)
        NativeAdsUtils.instance.loadNativeAds(
            context,
            keyAd
        ) { nativeAds ->
            if (nativeAds != null) {
                val adLayoutView =
                    LayoutInflater.from(context)
                        .inflate(R.layout.ad_native_content, adView, false) as NativeAdView
                NativeAdsUtils.instance.populateNativeAdVideoView(
                    nativeAds,
                    adLayoutView
                )
                adView.removeAllViews()
                adView.addView(adLayoutView)
                nativeAd.postValue(adView)
            }
        }
    }
}