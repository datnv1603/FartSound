package com.wa.pranksound.ui.component.sound

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.pranksound.R
import com.wa.pranksound.adapter.VerticalSoundAdapterTest
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivitySoundListBinding
import com.wa.pranksound.databinding.AdNativeVideoBinding
import com.wa.pranksound.model.Sound
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.utils.ImageLoader
import com.wa.pranksound.utils.KeyClass
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.ads.AdsConsentManager
import com.wa.pranksound.utils.ads.NativeAdsUtils.Companion.instance
import com.wa.pranksound.utils.extention.isNetworkAvailable
import java.io.IOException
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

class SoundListActivity : BaseBindingActivity<ActivitySoundListBinding, SoundListViewModel>() {
    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mInterstitialAd: InterstitialAd? = null

    private var keyNative: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.NATIVE_SOUND)

    override val layoutId: Int
        get() = R.layout.activity_sound_list

    override fun getViewModel(): Class<SoundListViewModel> = SoundListViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupData() {
        initAdsManager()
        findView()
        loadAds()
    }

    private fun findView() {
        val strCateName = intent.getStringExtra(KeyClass.cate_name)

        try {
            if (strCateName != null) {
                when (strCateName) {
                    KeyClass.air_horn -> {
                        binding.tvTitle.text = getString(R.string.air_horn)
                    }
                    KeyClass.hair_clipper -> {
                        binding.tvTitle.text = getString(R.string.hair_clipper)
                    }
                    KeyClass.fart -> {
                        binding.tvTitle.text = getString(R.string.fart)
                    }
                    KeyClass.count_down -> {
                        binding.tvTitle.text = getString(R.string.count_down)
                    }
                    KeyClass.gun -> {
                        binding.tvTitle.text = getString(R.string.gun)
                    }
                    KeyClass.ghost -> {
                        binding.tvTitle.text = getString(R.string.ghost)
                    }
                    KeyClass.halloween -> {
                        binding.tvTitle.text = getString(R.string.halloween)
                    }
                    KeyClass.snore -> {
                        binding.tvTitle.text = getString(R.string.snore)
                    }
                    KeyClass.test_sound -> {
                        binding.tvTitle.text = getString(R.string.test_sound)
                    }
                }

                val images = assets.list("prank_sound/$strCateName")
                val listSound: MutableList<Sound> = ArrayList()
                checkNotNull(images)

                val imageList = ImageLoader.getImageListFromAssets(this, "prank_image/$strCateName")

                for (i in images.indices) {
                    val sound = if (i >= 10) {
                        Sound(strCateName, imageList[i], images[i], 0, true, isNew = false)
                    } else {
                        Sound(strCateName, imageList[i], images[i], 0, false, isNew = false)
                    }
                    listSound.add(sound)
                }
                val verticalSoundAdapter = VerticalSoundAdapterTest(listSound) {
                    showInterstitial {  }
                }
                binding.rvSound.setAdapter(verticalSoundAdapter)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        binding.imgBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun loadAds() {
        loadNativeAd()
    }

    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_SOUND)
        ) {
            loadNativeAds(keyNative)
        } else {
            binding.rlNative.visibility = View.GONE
        }
    }

    private fun loadNativeAds(keyAds: String) {
        instance.loadNativeAds(
            applicationContext,
            keyAds
        ) { nativeAds: NativeAd? ->
            if (nativeAds != null) {
                val adNativeVideoBinding = AdNativeVideoBinding.inflate(
                    layoutInflater
                )
                instance.populateNativeAdVideoView(
                    nativeAds,
                    (adNativeVideoBinding.root as NativeAdView),
                    true
                )
                binding.frNativeAds.removeAllViews()
                binding.frNativeAds.addView(adNativeVideoBinding.root)
            }
        }
    }

    private fun initAdsManager() {
        adsConsentManager = AdsConsentManager.getInstance(this)
        adsConsentManager?.gatherConsent(this) { consentError ->
            if (consentError != null) {

                initializeMobileAdsSdk()
            }

            if (adsConsentManager?.canRequestAds == true) {
                initializeMobileAdsSdk()
            }
        }

        if (adsConsentManager?.canRequestAds == true) {
            initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        if (isAdsInitializeCalled.getAndSet(true)) {
            return
        }
        try {
            MobileAds.initialize(this) { }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        loadInterAd()
    }

    private fun loadInterAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SOUND)
        ) {
            val keyAdInterAllPrice = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.INTER_SOUND)
            loadInterAdsMain(keyAdInterAllPrice)
        }
    }

    private fun loadInterAdsMain(keyAdInter: String) {
        InterstitialAd.load(
            this,
            keyAdInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mFirebaseAnalytics?.logEvent("e_load_inter_splash", null)
                    mInterstitialAd = null

                    Handler(Looper.getMainLooper()).postDelayed(
                        { loadInterAdsMain(keyAdInter) },
                        2000
                    )
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics?.logEvent("d_load_inter_splash", null)
                    mInterstitialAd = ad
                    mInterstitialAd!!.onPaidEventListener =
                        OnPaidEventListener { adValue: AdValue ->
                            val loadedAdapterResponseInfo =
                                mInterstitialAd!!.responseInfo.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@SoundListActivity)
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.AD_PLATFORM, "admob mediation")
                            params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                            params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                            params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            analytics.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }

    fun showInterstitial(isReload: Boolean = true, onAdDismissedAction: () -> Unit) {
        if (!isNetworkAvailable()) {
            onAdDismissedAction.invoke()
            return
        }
        val timeLoad = FirebaseRemoteConfig.getInstance()
            .getLong(RemoteConfigKey.INTER_DELAY)

        val timeSubtraction =
            Date().time - SharedPreferenceHelper.getLong(Constant.TIME_LOAD_NEW_INTER_ADS)
        if (timeSubtraction <= timeLoad) {
            onAdDismissedAction.invoke()
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                onAdDismissedAction.invoke()
                return
            }
            onAdDismissedAction.invoke()
            loadInterAd()
            return
        }
        mInterstitialAd?.show(this)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                if (isReload) loadInterAd()
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }

            override fun onAdShowedFullScreenContent() {
                kotlin.runCatching {
                    onAdDismissedAction.invoke()
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }
    }
}