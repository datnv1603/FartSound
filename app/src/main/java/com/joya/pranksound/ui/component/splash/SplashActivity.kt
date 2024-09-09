package com.joya.pranksound.ui.component.splash

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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.ActivitySplashBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.multilang.MultiLangActivity
import com.joya.pranksound.utils.RemoteConfigKey
import com.joya.pranksound.utils.Utils
import com.joya.pranksound.utils.extention.setStatusBarColor
import timber.log.Timber
import java.util.Date
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

//        if (!Utils.checkInternetConnection(this)) {
//            viewModel.starTimeCount(5000)
//        }
        viewModel.starTimeCount(5000)
    }


    private fun init() {
        viewModel.isCompleteLiveData.observe(this) {
            openChooseLanguageActivity()
            finish()
        }
        val countDownTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                viewModel.starTimeCount(1000)
            }
        }
        countDownTimer.start()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
    }

    override fun setupData() {
    }

    private fun openChooseLanguageActivity() {
        val intent = Intent(this@SplashActivity, MultiLangActivity::class.java)
        intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
        startActivity(intent)
        finish()
    }
}