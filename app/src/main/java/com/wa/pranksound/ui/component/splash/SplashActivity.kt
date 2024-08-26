package com.wa.pranksound.ui.component.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.pranksound.R
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivitySplashBinding
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.ui.component.multilang.MultiLangActivity
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.Utils
import com.wa.pranksound.utils.ads.AdsConsentManager
import com.wa.pranksound.utils.extention.setStatusBarColor
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    private val isAdsInitializeCalled = AtomicBoolean(false)
    private var mInterstitialAd: InterstitialAd? = null
    private var adsConsentManager: AdsConsentManager? = null
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

    val bundle = Bundle()
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAdsManager()
        init()
        if (!Utils.checkInternetConnection(this)) {
            viewModel.starTimeCount(5000)
        }
    }

    private fun init() {
        viewModel.isCompleteLiveData.observe(this) {
            openChooseLanguageActivity()
            kotlin.runCatching {
                showInterstitial {}
            }
            finish()
        }
        val countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                viewModel.starTimeCount(1000)
            }
        }
        countDownTimer.start()
    }

    private fun initAdsManager() {
        adsConsentManager = AdsConsentManager.getInstance(applicationContext)
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
        kotlin.runCatching {
            MobileAds.initialize(this) {}
        }

        loadInterAd()
    }

    private fun showInterstitial(onAdDismissedAction: () -> Unit) {
        if (!Utils.checkInternetConnection(this)) {
            onAdDismissedAction.invoke()
            return
        }

        if (mInterstitialAd == null) {
            if (adsConsentManager?.canRequestAds == false) {
                onAdDismissedAction.invoke()
                return
            }
            onAdDismissedAction.invoke()
            return
        }

        mInterstitialAd?.show(this)
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                SharedPreferenceHelper.storeLong(
                    Constant.TIME_LOAD_NEW_INTER_ADS,
                    Date().time
                )
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                mInterstitialAd = null
                onAdDismissedAction.invoke()
            }

            override fun onAdShowedFullScreenContent() {
                onAdDismissedAction.invoke()
            }
        }
    }

    private var loadInterCount = 0

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
    }

    override fun setupData() {
        viewModel.loadAds(baseContext)
        viewModel.nativeAdHome.observe(this) {
            adNativeHome = it
        }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun openChooseLanguageActivity() {
        val intent = Intent(this@SplashActivity, MultiLangActivity::class.java)
        intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
        startActivity(intent)
        finish()
    }

    private fun loadInterAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_SPLASH)
        ) {
            val keyAdInterAllPrice =
                FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.INTER_SPLASH)
            loadInterAdsSplash(keyAdInterAllPrice)
        } else {
            viewModel.starTimeCount(5000)
        }
    }

    private fun loadInterAdsSplash(keyAdInter: String) {
        InterstitialAd.load(
            this,
            keyAdInter,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mFirebaseAnalytics.logEvent("e_load_inter_splash", null)
                    mInterstitialAd = null
                    loadInterCount++
                    if (loadInterCount >= 3) {
                        viewModel.starTimeCount(5000)
                    } else {
                        loadInterAdsSplash(keyAdInter)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics.logEvent("d_load_inter_splash", null)
                    mInterstitialAd = ad
                    loadInterCount = 0
                    viewModel.starTimeCount(5000)
                    mInterstitialAd?.onPaidEventListener =
                        OnPaidEventListener { adValue ->
                            val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                mInterstitialAd?.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros.toDouble() / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@SplashActivity)
                            val params = Bundle().apply {
                                putString(
                                    FirebaseAnalytics.Param.AD_PLATFORM,
                                    "admob mediation"
                                )
                                putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                                putString(FirebaseAnalytics.Param.AD_FORMAT, "Interstitial")
                                putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                                putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            }
                            analytics.logEvent("ad_impression_2", params)
                        }
                }
            }
        )
    }

    @SuppressLint("StaticFieldLeak")
    companion object {
        var adNativeHome: NativeAdView? = null
    }
}