package com.wa.pranksound.ui.component.record

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.wa.pranksound.R
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivityRecordBinding
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.Utils
import com.wa.pranksound.utils.ads.AdsConsentManager
import com.wa.pranksound.utils.ads.BannerUtils
import com.wa.pranksound.utils.extention.gone
import com.wa.pranksound.utils.extention.invisible
import com.wa.pranksound.utils.extention.setOnSafeClick
import com.wa.pranksound.utils.extention.visible
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class RecordActivity : BaseBindingActivity<ActivityRecordBinding, RecordViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private var mInterstitialAd: InterstitialAd? = null

    private val keyAdInterAllPrice = FirebaseRemoteConfig.getInstance()
        .getString(RemoteConfigKey.INTER_RECORD)

    private var retryAttempt = 0.0

    private var bannerReload: Long =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private var keyAdBanner: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.BANNER_RECORD)

    private var fileName: String = ""
    private var playerBackground: MediaPlayer? = null
    private var playerEffects: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private var countDownTimer: CountDownTimer? = null

    private var timeRecord = 0

    private var permissions: Array<String> =
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override val layoutId: Int
        get() = R.layout.activity_record

    override fun getViewModel(): Class<RecordViewModel> = RecordViewModel::class.java
    override fun setupData() {
        loadAds()
        initAdsManager()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        initAction()

        fileName = File(this.filesDir, "audioRecord.mp3").toString()
    }

    private fun initAction() {
        binding.imgBack.setOnSafeClick {
            forceShowInterstitial {
                finish()
            }
        }

        binding.btnMicro.setOnSafeClick {
            if (!checkPermissions()) {
                checkPer()
            } else {
                startRecording()
            }
        }

        binding.btnDiscard.setOnSafeClick {
            showDiscardDialog {
                stopRecording()
            }
        }

        binding.btnPause.setOnSafeClick {
            pauseRecording()
            countDownTimer?.cancel()
        }

        binding.btnStart.setOnSafeClick {
            resumeRecording()
        }


        binding.imgNext.setOnSafeClick {
            stopRecording()
            val intent = Intent(this@RecordActivity, EditRecordActivity::class.java)
            startActivity(intent)
            showInterstitial(false) {}
        }
    }

    private fun checkPer() {
        if (ContextCompat.checkSelfPermission(
                this@RecordActivity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this@RecordActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this@RecordActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this@RecordActivity,
                permissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            // Permissions are already granted, proceed with recording
            // binding.llRecordHere.visibility = View.GONE
            // binding.llStartRecord.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {

            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permission = Manifest.permission.RECORD_AUDIO
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("DefaultLocale")
    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun createCountDownTimer(time: Long): CountDownTimer {
        return object : CountDownTimer(time, 200) {
            override fun onTick(millisUntilFinished: Long) {
                timeRecord += 200
                val timeString = formatTime(timeRecord/1000)
                val mTime = "$timeString/00:30"
                binding.txtCountTime.text = mTime
            }

            override fun onFinish() {
                stopRecording()
                val intent = Intent(this@RecordActivity, EditRecordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun startRecording() {
        onRecording()
        countDownTimer = createCountDownTimer(30000 - timeRecord.toLong())
        countDownTimer?.start()

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)

            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            start()
        }
    }

    private fun pauseRecording() {
        onPauseRecording()
        playerEffects?.pause()
        playerBackground?.pause()
        recorder?.apply {
            pause()
        }
    }

    private fun resumeRecording() {
        onResumeRecording()
        countDownTimer = createCountDownTimer(30000 - timeRecord.toLong())
        countDownTimer?.start()
        playerEffects?.start()
        playerBackground?.start()
        recorder?.apply {
            resume()
        }
    }

    private fun stopRecording() {
        onStopRecording()
        timeRecord = 0
        playerEffects?.stop()
        playerBackground?.stop()

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        countDownTimer?.cancel()
        binding.txtCountTime.text = getString(R.string.time_record)
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        playerBackground?.release()
        playerEffects?.release()
            countDownTimer?.cancel()
        playerBackground = null
        playerEffects = null
    }

    override fun onPause() {
        super.onPause()
        stopRecording()

        Adjust.onPause()
    }

    override fun onResume() {
        super.onResume()

        Adjust.onResume()
    }

    override fun finish() {
        recorder?.apply {
            showDiscardDialog {
                super.finish()
            }
        } ?: run {
            super.finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("timeRecord", timeRecord)
        outState.putString("fileName", fileName)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        timeRecord = savedInstanceState.getInt("timeRecord")
        fileName = savedInstanceState.getString("fileName")!!
    }

    private fun onPrepareRecording() {
        binding.llRecordHere.visible()
        binding.llStartRecord.gone()
        binding.btnStart.invisible()
        binding.btnPause.visible()
        binding.animRecording.cancelAnimation()
    }

    private fun onRecording() {
        binding.llRecordHere.gone()
        binding.llStartRecord.visible()
        binding.animRecording.playAnimation()
    }

    private fun onPauseRecording() {
        binding.btnStart.visible()
        binding.btnPause.invisible()
        binding.animRecording.pauseAnimation()
    }

    private fun onResumeRecording() {
        binding.btnStart.invisible()
        binding.btnPause.visible()
        binding.animRecording.resumeAnimation()
    }

    private fun onStopRecording() {
        onPrepareRecording()
    }

    private fun showDiscardDialog(callback: () -> Unit) {
        val dialogCustomExit = Dialog(this@RecordActivity)
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCustomExit.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogCustomExit.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogCustomExit.setContentView(R.layout.exit_record_dialog)
        dialogCustomExit.setCancelable(true)
        dialogCustomExit.show()
        dialogCustomExit.window!!.setAttributes(lp)
        val btnNegative = dialogCustomExit.findViewById<TextView>(R.id.btnNegative)
        val btnPositive = dialogCustomExit.findViewById<TextView>(R.id.btnPositive)
        btnPositive.setOnClickListener {
            dialogCustomExit.dismiss()
            callback()
        }
        btnNegative.setOnClickListener {
            dialogCustomExit.dismiss()
        }
    }

    private fun loadAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_RECORD)
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
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_INTER_RECORD)
        ) {

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
                    if (retryAttempt < 4) {
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

                            val analytics = FirebaseAnalytics.getInstance(this@RecordActivity)
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

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

}