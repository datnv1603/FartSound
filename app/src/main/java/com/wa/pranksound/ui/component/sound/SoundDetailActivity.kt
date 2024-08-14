package com.wa.pranksound.ui.component.sound

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.text.format.DateUtils
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room.databaseBuilder
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
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
import com.wa.pranksound.adapter.HorizontalFavoriteSoundAdapter
import com.wa.pranksound.adapter.HorizontalSoundAdapterTest
import com.wa.pranksound.common.Constant
import com.wa.pranksound.common.Constant.KEY_IS_FIRST_OPEN_SOUND
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.data.SharedPreferenceHelper.Companion.getBoolean
import com.wa.pranksound.data.SharedPreferenceHelper.Companion.storeBoolean
import com.wa.pranksound.databinding.ActivitySoundDetailBinding
import com.wa.pranksound.model.Sound
import com.wa.pranksound.room.AppDatabase
import com.wa.pranksound.room.InsertPrankSound
import com.wa.pranksound.room.QueryClass
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.ui.component.main.MainActivity
import com.wa.pranksound.utils.BaseActivity
import com.wa.pranksound.utils.ImageLoader
import com.wa.pranksound.utils.KeyClass
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.Utils.getAudioList
import com.wa.pranksound.utils.Utils.getVibration
import com.wa.pranksound.utils.Utils.removeAfterDot
import com.wa.pranksound.utils.Utils.saveAudioList
import com.wa.pranksound.utils.ads.AdsConsentManager
import com.wa.pranksound.utils.ads.BannerUtils
import com.wa.pranksound.utils.extention.gone
import com.wa.pranksound.utils.extention.isNetworkAvailable
import com.wa.pranksound.utils.extention.setOnSafeClick
import java.io.IOException
import java.util.Arrays
import java.util.Collections
import java.util.Date
import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean

class SoundDetailActivity : BaseBindingActivity<ActivitySoundDetailBinding, SoundDetailViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mInterstitialAd: InterstitialAd? = null

    private var bannerReload: Long =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private var keyAdBanner: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.BANNER_SOUND)


    var imgBack: ImageView? = null
    var imgItem: ImageView? = null
    var imgPlayPause: ImageView? = null
    var imgHeart: ImageView? = null
    var imgAnimation: LottieAnimationView? = null

    var isPlaying: Boolean = false
    var isLoop: Boolean = false
    var strMusicName: String? = null
    var txtTitle: TextView? = null
    var endTime: TextView? = null
    var txtCountTime: TextView? = null
    var tvOff: TextView? = null
    var systemService: Vibrator? = null
    var mediaPlayer: MediaPlayer? = MediaPlayer()
    var seekBar: ProgressBar? = null
    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var swLoop: ImageButton? = null
    var rvSound: RecyclerView? = null
    var queryClass: QueryClass? = null
    var strCateName: String? = null
    var arrFavPrankSound: List<InsertPrankSound?> = ArrayList()
    var horizontalSoundAdapter: HorizontalFavoriteSoundAdapter? = null
    var isFav: Boolean = false
    var countDownTimer: CountDownTimer? = null
    var cate: List<String>? = null
    var images: Array<String>? = null
    var string_img_sound: String? = null
    var int_img_sound: Int? = null
    var soundName: String? = null
    var soundPath: String? = null
    var llBtnOff: LinearLayout? = null
    var llMoreSound: LinearLayout? = null
    var fl_banner: FrameLayout? = null
    var view_line: View? = null
    var animGuide: LottieAnimationView? = null
    var viewBlur: View? = null

    var btnVolumeLoud: ImageButton? = null
    var btnVolumeSmall: ImageButton? = null
    var btnDelete: ImageButton? = null
    var volume: SeekBar? = null
    private var audioManager: AudioManager? = null
    var isRecord: Boolean? = null

    override val layoutId: Int
        get() = R.layout.activity_sound_detail

    override fun getViewModel(): Class<SoundDetailViewModel> = SoundDetailViewModel::class.java
    override fun setupView(savedInstanceState: Bundle?) {
        val db =
            databaseBuilder(this, AppDatabase::class.java, "prank_sound").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        queryClass = db.queryClass()

        findView()
        data
        clickEvent()

        setUpVolume()
    }

    override fun setupData() {
        loadAds()
        initAdsManager()
        binding.btnBack.setOnSafeClick {
            finish()
            showInterstitial {  }
        }
    }
    private fun setUpVolume() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

        volume!!.max = maxVolume
        volume!!.progress = currentVolume
        btnVolumeSmall!!.setOnClickListener { v: View? -> volume!!.progress = maxVolume / 5 }
        btnVolumeLoud!!.setOnClickListener { v: View? -> volume!!.progress = 4 * maxVolume / 5 }
        volume!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    private val data: Unit
        get() {
            val intent = intent
            soundName = intent.getStringExtra("sound_name")

            systemService = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            strMusicName = getIntent().getStringExtra(KeyClass.music_name)

            isFav = getIntent().getBooleanExtra(KeyClass.is_fav, false)
            strCateName = getIntent().getStringExtra(KeyClass.cate_name)

            //get music name from sound list
            string_img_sound = getIntent().getStringExtra(KeyClass.image_sound)
            int_img_sound = getIntent().getIntExtra(KeyClass.image_sound, 0)
            isRecord = getIntent().getBooleanExtra(KeyClass.is_record, false)

            soundPath = getIntent().getStringExtra(KeyClass.sound_path)

            setUpSound()

            try {
                cate = listOf(
                    *Objects.requireNonNull(
                        assets.list("prank_sound")
                    )
                )
                images = assets.list("prank_sound/$strCateName")

                val images = assets.list("prank_sound/$strCateName")
                val listSound: MutableList<Sound> = ArrayList()
                checkNotNull(images)

                val imageList = ImageLoader.getImageListFromAssets(this, "prank_image/$strCateName")

                for (i in images.indices) {
                    var sound: Sound
                    val imageName = removeAfterDot(images[i])
                    sound = Sound(strCateName!!, imageList[i], imageName, 0, true, isNew = false)
                    listSound.add(sound)
                }
                val verticalSoundAdapter = HorizontalSoundAdapterTest(listSound) { position: Int ->
                    val sound = listSound[position]
                    getData(sound)
                    showInterstitial {  }
                }
                rvSound!!.adapter = verticalSoundAdapter
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

    private fun getData(sound: Sound?) {
        if (sound != null) {
            soundName = sound.name
            strMusicName = sound.name
            strCateName = sound.typeSound
            string_img_sound = sound.image
            int_img_sound = 0
            isRecord = false
            soundPath = null
            setUpSound()
        }
    }

    private fun setUpSound() {
        val insertPrankSound1 = queryClass!!.getFavSound(strCateName, strMusicName)
        if (isRecord!!) {
            llMoreSound!!.visibility = View.GONE
            btnDelete!!.visibility = View.VISIBLE
            imgHeart!!.visibility = View.GONE
        } else {
            btnDelete!!.visibility = View.GONE
            imgHeart!!.visibility = View.VISIBLE
            imgHeart!!.isSelected = insertPrankSound1 != null
        }


        if (strMusicName != null) {
            txtTitle!!.text = strMusicName
        }

        if (strCateName != null) {
            //load ảnh từ file asset

            if (int_img_sound != 0) {
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), int_img_sound);
                Glide.with(this).load(int_img_sound).into(imgItem!!)
            } else {
                if (!string_img_sound!!.contains("png")) {
                    val bitmap = BitmapFactory.decodeResource(resources, string_img_sound!!.toInt())
                    Glide.with(this).load(bitmap).into(imgItem!!)
                } else {
                    Glide.with(this).load("file:///android_asset/$string_img_sound").into(
                        imgItem!!
                    )
                }
            }

            try {
                if (mediaPlayer != null) {
                    mediaPlayer!!.release()
                }
                mediaPlayer = MediaPlayer()
                if (strCateName == "record") {
                    soundPath = strMusicName
                }
                //set sound from asset or record
                if (soundPath != null) {
                    try {
                        // Đặt nguồn dữ liệu cho MediaPlayer từ File
                        mediaPlayer!!.setDataSource(soundPath)
                    } catch (ignored: IOException) {
                    }
                } else {
                    val descriptor = assets.openFd("prank_sound/$strCateName/$strMusicName.ogg")
                    mediaPlayer!!.setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                    descriptor.close()
                }
                mediaPlayer!!.prepare()
                val length = mediaPlayer!!.duration // lấy giá trị độ dài của sound

                val durationText =
                    DateUtils.formatElapsedTime((length / 1000).toLong()) // converting time in millis to minutes:second format eg 14:15 min
                endTime!!.text = durationText
                seekBar!!.max = length

                runnable = Runnable {
                    if (mediaPlayer != null) {
                        seekBar!!.progress = mediaPlayer!!.currentPosition
                        handler.postDelayed(runnable!!, 5)
                    }
                }

                mediaPlayer!!.setOnCompletionListener { mp: MediaPlayer? ->
                    if (!isLoop) {
                        if (mediaPlayer != null) {
                            seekBar!!.progress = mediaPlayer!!.duration
                        }
                        isPlaying = false
                        //set anim
                        imgAnimation!!.visibility = View.INVISIBLE
                        stopVibrate()
                    } else {
                        seekBar!!.progress = 0
                        mediaPlayer!!.start()
                        startVibrate()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun findView() {
        imgBack = findViewById(R.id.btnBack)
        imgItem = findViewById(R.id.imgItem)
        txtTitle = findViewById(R.id.tvTitle)
        imgPlayPause = findViewById(R.id.imgPlayPause)
        endTime = findViewById(R.id.endTime)
        seekBar = findViewById(R.id.seekBar)
        swLoop = findViewById(R.id.swLoop)
        rvSound = findViewById(R.id.rvSound)
        imgHeart = findViewById(R.id.imgHeart)
        txtCountTime = findViewById(R.id.txtCountTime)
        tvOff = findViewById(R.id.tvOff)
        volume = findViewById(R.id.volumeSeekBar)
        btnVolumeLoud = findViewById(R.id.btnVolumeLoud)
        btnVolumeSmall = findViewById(R.id.btnVolumeSmall)
        btnDelete = findViewById(R.id.btnDelete)

        imgAnimation = findViewById(R.id.animation)
        viewBlur = findViewById(R.id.viewBlur)

        view_line = findViewById(R.id.line)
        llBtnOff = findViewById(R.id.llBtnOff)
        llMoreSound = findViewById(R.id.llMoreSounds)
        animGuide = findViewById(R.id.animGuide)
    }

    private fun startCountDownTimer(i: Int, txtCountTime: TextView?) {
        imgPlayPause!!.isEnabled = false
        imgAnimation!!.visibility = View.INVISIBLE
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(i.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val j2 = millisUntilFinished / (1000L)
                val j3 = 60L
                val j4 = j2 / j3
                val j5 = j2 % j3
                @SuppressLint("DefaultLocale") val format =
                    String.format("%02d:%02d", *arrayOf<Any>(j4, j5).copyOf(2))
                txtCountTime!!.text = format
            }

            override fun onFinish() {
                tvOff!!.setText(R.string._off)
                txtCountTime!!.text = ""
                isPlaying = true
                imgAnimation!!.visibility = View.VISIBLE
                if (mediaPlayer != null) {
                    if (!mediaPlayer!!.isPlaying) {
                        mediaPlayer!!.start()
                        mediaPlayer!!.isLooping = false
                        startVibrate()
                        handler.postDelayed(runnable!!, 5)
                    }
                }
            }
        }.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickEvent() {
        val isFirsTime = getBoolean(KEY_IS_FIRST_OPEN_SOUND, true)
        if (isFirsTime) {
            animGuide!!.visibility = View.VISIBLE
            viewBlur!!.visibility = View.VISIBLE
        } else {
            animGuide!!.visibility = View.GONE
            viewBlur!!.visibility = View.GONE
        }

        animGuide!!.setOnTouchListener { _: View?, _: MotionEvent? ->
            storeBoolean(KEY_IS_FIRST_OPEN_SOUND, false)
            animGuide!!.visibility = View.GONE
            viewBlur!!.visibility = View.GONE
            true
        }

        imgBack!!.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        llBtnOff!!.setOnClickListener { v: View? ->
            val popupMenu = PopupMenu(
                ContextThemeWrapper(
                    this@SoundDetailActivity, R.style.myPopupMenu
                ), v!!
            )
            popupMenu.menuInflater.inflate(R.menu.set_time, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                if (item.itemId == R.id.five) {
                    startCountDownTimer(5000, txtCountTime)
                    tvOff!!.setText(R.string._5s)
                    return@setOnMenuItemClickListener true
                } else if (item.itemId == R.id.fiveMinute) {
                    startCountDownTimer(300000, txtCountTime)
                    tvOff!!.setText(R.string._5m)
                    return@setOnMenuItemClickListener true
                } else if (item.itemId == R.id.off) {
                    if (countDownTimer != null) {
                        countDownTimer!!.cancel()
                    }
                    imgPlayPause!!.isEnabled = true
                    txtCountTime!!.text = ""
                    tvOff!!.setText(R.string._off)
                    return@setOnMenuItemClickListener true
                } else if (item.itemId == R.id.oneMinute) {
                    startCountDownTimer(60000, txtCountTime)
                    tvOff!!.setText(R.string._1m)
                    return@setOnMenuItemClickListener true
                } else if (item.itemId == R.id.ten) {
                    startCountDownTimer(10000, txtCountTime)
                    tvOff!!.setText(R.string._10s)
                    return@setOnMenuItemClickListener true
                } else if (item.itemId == R.id.thirty) {
                    startCountDownTimer(30000, txtCountTime)
                    tvOff!!.setText(R.string._30s)
                    return@setOnMenuItemClickListener true
                } else {
                    return@setOnMenuItemClickListener false
                }
            }
            popupMenu.show()
        }

        imgItem!!.setOnTouchListener { _: View?, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mediaPlayer != null) {
                        if (!mediaPlayer!!.isPlaying) {
                            imgAnimation!!.visibility = View.VISIBLE
                            imgAnimation!!.playAnimation()
                            mediaPlayer!!.seekTo(0)
                            mediaPlayer!!.isLooping = true
                            mediaPlayer!!.start()
                            startVibrate()
                        } else {
                            if (isLoop) {
                                mediaPlayer!!.pause()
                                stopVibrate()
                                imgAnimation!!.visibility = View.INVISIBLE
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!isLoop) {
                        if (mediaPlayer != null) {
                            if (mediaPlayer!!.isPlaying) {
                                mediaPlayer!!.pause()
                                stopVibrate()
                                imgAnimation!!.visibility = View.INVISIBLE
                            }
                        }
                    }
                    false
                }

                else -> false
            }
        }

        //test favories
        imgHeart!!.setOnClickListener { v: View? ->
            val insertPrankSound1 = queryClass!!.getFavSound(strCateName, strMusicName)
            if (insertPrankSound1 != null) {
                queryClass!!.getUnFavSound(strCateName, strMusicName)
                imgHeart!!.isSelected = false
            } else {
                val insertPrankSound = InsertPrankSound()
                insertPrankSound.folder_name = strCateName
                insertPrankSound.sound_name = strMusicName

                if (int_img_sound != 0) {
                    insertPrankSound.image_path = int_img_sound.toString()
                    insertPrankSound.sound_path = soundPath
                } else {
                    insertPrankSound.image_path = string_img_sound
                }
                queryClass!!.insertPrankSound(insertPrankSound)
                imgHeart!!.isSelected = true
            }
            if (isFav) {
                arrFavPrankSound = queryClass!!.allFavSound
                if (horizontalSoundAdapter != null) {
                    arrFavPrankSound.reversed()
                    horizontalSoundAdapter!!.notifyDataSetChangedAd(arrFavPrankSound)
                }
            }
        }

        swLoop!!.setOnClickListener {
            swLoop!!.isSelected = !swLoop!!.isSelected
            isLoop = swLoop!!.isSelected
        }

        btnDelete!!.setOnClickListener {
            showDiscardDialog {
                deleteRecord(strMusicName)
                startActivity(Intent(this@SoundDetailActivity, MainActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                stopVibrate()
                mediaPlayer!!.reset()
                mediaPlayer!!.stop()
                mediaPlayer!!.release()
                mediaPlayer = null
            }
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        handler.removeCallbacksAndMessages(null)
        handler.removeCallbacks(runnable!!)
    }


    override fun onPause() {
        super.onPause()
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                stopVibrate()
                imgAnimation!!.visibility = View.INVISIBLE
            }
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        handler.removeCallbacksAndMessages(null)
        handler.removeCallbacks(runnable!!)
    }


    fun startVibrate() {
        if (getVibration(this)) {
            if (systemService != null && systemService!!.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(0, 1000), 0)
                    systemService!!.vibrate(vibrationEffect)
                } else {
                    systemService!!.vibrate(mediaPlayer!!.duration.toLong())
                }
            }
        }
    }


    private fun stopVibrate() {
        if (systemService != null) {
            systemService!!.cancel()
        }
    }

    private fun deleteRecord(fileName: String?) {
        val audioFileList = getAudioList(this).toMutableList()
        for (i in audioFileList.indices) {
            if (audioFileList[i].name == fileName) {
                audioFileList.removeAt(i)
                break
            }
        }
        saveAudioList(this, audioFileList)
    }

    private fun showDiscardDialog(callback: Runnable) {
        val dialogCustomExit = Dialog(this@SoundDetailActivity)
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCustomExit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogCustomExit.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        dialogCustomExit.setContentView(R.layout.dialog_delete_record)
        dialogCustomExit.setCancelable(true)
        dialogCustomExit.show()
        dialogCustomExit.window!!.attributes = lp

        val btnNegative = dialogCustomExit.findViewById<TextView>(R.id.btnNegative)
        val btnPositive = dialogCustomExit.findViewById<TextView>(R.id.btnPositive)

        btnPositive.setOnClickListener {
            dialogCustomExit.dismiss()
            callback.run()
        }

        btnNegative.setOnClickListener { dialogCustomExit.dismiss() }
    }

    private fun loadAds() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_BANNER_SOUND)
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

                            val analytics = FirebaseAnalytics.getInstance(this@SoundDetailActivity)
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