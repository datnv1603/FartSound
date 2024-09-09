package com.joya.pranksound.ui.component.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.ActivityMainBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.fragment.CreateFragment
import com.joya.pranksound.ui.component.fragment.FavoritesFragment
import com.joya.pranksound.ui.component.fragment.HomeFragment
import com.joya.pranksound.ui.component.fragment.LeaderBoardFragment
import com.joya.pranksound.ui.component.settings.SettingsActivity
import com.joya.pranksound.utils.Gdpr
import com.joya.pranksound.utils.RemoteConfigKey
import com.joya.pranksound.utils.Utils
import com.joya.pranksound.utils.ads.AdsConsentManager
import com.joya.pranksound.utils.ads.BannerUtils
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.setOnSafeClick
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private var mInterstitialAd: InterstitialAd? = null

    private var retryAttempt = 0.0

    private var bannerReload: Long =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private var keyAdBanner: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.BANNER_MAIN)

    private var fragmentManager: FragmentManager? = null
    private var navTab: String = "home"

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun setupData() {
        loadAds()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        findView()

        fragmentManager = supportFragmentManager
        bottomNavigationClick()
        val getRecord = intent.getStringExtra("from_record")

        if (getRecord != null) {
            changeFragment("create")
        } else {
            changeFragment("home")
        }

        binding.btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SettingsActivity::class.java
                )
            )
        }
        initAdsManager()

    }

    private fun changeFragment(tab: String) {
        when (tab) {
            "home" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, HomeFragment())
                    .commit()
                binding.tvTitle.setText(R.string.home)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_selected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "create" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, CreateFragment())
                    .commit()
                binding.tvTitle.setText(R.string.create_sound)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_selected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "favorites" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, FavoritesFragment())
                    .commit()
                binding.tvTitle.setText(R.string.favorites)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_selected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "leaderboard" -> {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.fragmentMain, LeaderBoardFragment()).commit()
                binding.tvTitle.setText(R.string.leaderboard)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_selected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )
            }

            else -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, HomeFragment())
                    .commit()
                binding.tvTitle.setText(R.string.home)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_selected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }
        }
    }

    private fun bottomNavigationClick() {
        binding.apply {
            llHome.setOnSafeClick {
                navTab = "home"
                changeFragment(navTab)
            }

            llCreate.setOnSafeClick {
                navTab = "create"
                changeFragment(navTab)
            }

            llFavorites.setOnSafeClick {
                navTab = "favorites"
                changeFragment(navTab)
            }

            llLeaderboard.setOnSafeClick {
                navTab = "leaderboard"
                changeFragment(navTab)
            }
        }
    }

    private fun findView() {
        Gdpr().make(this)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    fun showExitDialog() {
        val dialogCustomExit = Dialog(this@MainActivity)
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCustomExit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogCustomExit.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogCustomExit.setContentView(R.layout.exit_dialog_layout)
        dialogCustomExit.setCancelable(true)
        dialogCustomExit.show()
        dialogCustomExit.window!!.attributes = lp

        val btnNegative = dialogCustomExit.findViewById<TextView>(R.id.btnNegative)
        val btnPositive = dialogCustomExit.findViewById<TextView>(R.id.btnPositive)

        btnPositive.setOnSafeClick {
            dialogCustomExit.dismiss()
            finishAffinity()
        }
        btnNegative.setOnSafeClick { dialogCustomExit.dismiss() }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_MAIN)
        ) {
            loadBanner()
        } else {
            binding.rlBanner.gone()
        }
        viewModel.loadBanner.observe(this) {
            loadBanner()
        }
    }

    private fun loadBanner() {
        viewModel.starTimeCountReloadBanner(bannerReload)
        BannerUtils.instance?.loadCollapsibleBanner(this, keyAdBanner) {}
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
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_HOME)
        ) {
            val keyAdInterAllPrice = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.INTER_HOME)
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
                    mFirebaseAnalytics.logEvent("e_load_inter_splash", null)
                    mInterstitialAd = null
                    retryAttempt++
                    if (retryAttempt < 3) {
                        val delayMillis = TimeUnit.SECONDS.toMillis(
                            2.0.pow(6.0.coerceAtMost(retryAttempt)).toLong()
                        )
                        Handler(Looper.getMainLooper()).postDelayed({ loadInterAd() }, delayMillis)
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    mFirebaseAnalytics.logEvent("d_load_inter_splash", null)
                    mInterstitialAd = ad
                    retryAttempt = 0.0
                    mInterstitialAd!!.onPaidEventListener =
                        OnPaidEventListener { adValue: AdValue ->
                            val loadedAdapterResponseInfo =
                                mInterstitialAd!!.responseInfo.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            val revenue = adValue.valueMicros / 1000000.0
                            adRevenue.setRevenue(revenue, adValue.currencyCode)
                            adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                            Adjust.trackAdRevenue(adRevenue)

                            val analytics = FirebaseAnalytics.getInstance(this@MainActivity)
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

    fun showInterstitial(onAdDismissedAction: () -> Unit) {
        if (!Utils.checkInternetConnection(this)) {
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
                loadInterAd()
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

    fun forceShowInterstitial(onAdDismissedAction: () -> Unit) {
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
            loadInterAd()
            return
        }
        mInterstitialAd?.show(this)

        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                loadInterAd()
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