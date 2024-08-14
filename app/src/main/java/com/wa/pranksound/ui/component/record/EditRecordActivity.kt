package com.wa.pranksound.ui.component.record

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.pranksound.R
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivityEditRecordBinding
import com.wa.pranksound.model.Record
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.ui.component.main.MainActivity
import com.wa.pranksound.utils.RemoteConfigKey
import com.wa.pranksound.utils.Utils
import com.wa.pranksound.utils.ads.AdsConsentManager
import com.wa.pranksound.utils.ads.BannerUtils
import com.wa.pranksound.utils.extention.gone
import com.wa.pranksound.utils.extention.invisible
import com.wa.pranksound.utils.extention.isNetworkAvailable
import com.wa.pranksound.utils.extention.visible
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class EditRecordActivity : BaseBindingActivity<ActivityEditRecordBinding, EditRecordViewModel>() {

    private var adsConsentManager: AdsConsentManager? = null
    private val isAdsInitializeCalled = AtomicBoolean(false)
    private val mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mInterstitialAd: InterstitialAd? = null

    var bannerReload: Long =
        FirebaseRemoteConfig.getInstance().getLong(RemoteConfigKey.BANNER_RELOAD)
    private var keyAdBanner: String =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.BANNER_RECORD)

    private var fileName: String = ""
    private var fileNameNew: String = ""

    private var fileNameSave: String = ""

    private var isEffectAddedOnce = false

    private var recorder: MediaRecorder? = null

    private var player: MediaPlayer? = null

    private var audioFileList: MutableList<Record> = mutableListOf()

    private var runnable: Runnable? = null
    private var handler = Handler(Looper.getMainLooper())
    private var isPlaying = true

    private var recordName: String = "Original"
    private var recordImage: Int = R.drawable.ic_original

    override val layoutId: Int
        get() = R.layout.activity_edit_record

    override fun getViewModel(): Class<EditRecordViewModel> = EditRecordViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        fileName = File(this.filesDir, "audioRecord.mp3").toString()

        fileNameNew = File(this.filesDir, "audioRecordNew.mp3").toString()

        audioFileList = Utils.getAudioList(this).toMutableList()

        // startRecord()
        playOriginal(fileName, fileNameNew)

        //setting seekbar
        var length: Int = 0
        player?.apply {
            length = duration
        }

        val durationText =
            DateUtils.formatElapsedTime((length / 1000).toLong())

        binding.tvTimeEnd.text = durationText
        binding.seekBar.setMax(length)

        runnable = Runnable {
            if (player != null) {
                val currentTime =
                    DateUtils.formatElapsedTime((player!!.currentPosition / 1000).toLong())
                val totalDuration = DateUtils.formatElapsedTime((player!!.duration / 1000).toLong())
                binding.seekBar.progress = player!!.currentPosition
                binding.seekBar.max = player!!.duration
                binding.tvTimeStart.text = currentTime
                binding.tvTimeEnd.text = totalDuration
                handler.postDelayed(runnable!!, 5) // Update per second
            }
        }
        handler.postDelayed(runnable!!, 5)

        player!!.setOnCompletionListener {
            if (player != null) {
                binding.seekBar.progress = player!!.duration
            }
            player!!.start()
            Log.d("Check_sound_replay", "sound in main:")
        }

        binding.original.setOnClickListener {
            playOriginal(fileName, fileNameNew)
            recordName = getString(R.string.original)
            recordImage = R.drawable.ic_original
            binding.ivCheckedOriginal.visible()
            binding.ivCheckedHelium.invisible()
            binding.ivCheckedRobot.invisible()
            binding.ivCheckedRadio.invisible()
            binding.ivCheckedBackWard.invisible()
            binding.ivCheckedCave.invisible()
            showInterstitial {  }
        }

        binding.ivChipmunk.setOnClickListener {
            playChipmunk(fileName, fileNameNew)
            recordName = getString(R.string.helium)
            recordImage = R.drawable.ic_helium
            binding.ivCheckedOriginal.invisible()
            binding.ivCheckedHelium.visible()
            binding.ivCheckedRobot.invisible()
            binding.ivCheckedRadio.invisible()
            binding.ivCheckedBackWard.invisible()
            binding.ivCheckedCave.invisible()
            showInterstitial {  }
        }

        binding.ivRobot.setOnClickListener {
            playRobot(fileName, fileNameNew)
            recordName = getString(R.string.robot)
            recordImage = R.drawable.ic_robot
            binding.ivCheckedOriginal.invisible()
            binding.ivCheckedHelium.invisible()
            binding.ivCheckedRobot.visible()
            binding.ivCheckedRadio.invisible()
            binding.ivCheckedBackWard.invisible()
            binding.ivCheckedCave.invisible()
            showInterstitial {  }
        }

        binding.ivRadio.setOnClickListener {
            playRadio(fileName, fileNameNew)
            recordName = getString(R.string.radio)
            recordImage = R.drawable.ic_radio
            binding.ivCheckedOriginal.invisible()
            binding.ivCheckedHelium.invisible()
            binding.ivCheckedRobot.invisible()
            binding.ivCheckedRadio.visible()
            binding.ivCheckedBackWard.invisible()
            binding.ivCheckedCave.invisible()
            showInterstitial {  }
        }

        binding.backward.setOnClickListener {
            playBackward(fileName, fileNameNew)
            recordName = getString(R.string.backward)
            recordImage = R.drawable.ic_backward
            binding.ivCheckedOriginal.invisible()
            binding.ivCheckedHelium.invisible()
            binding.ivCheckedRobot.invisible()
            binding.ivCheckedRadio.invisible()
            binding.ivCheckedBackWard.visible()
            binding.ivCheckedCave.invisible()
            showInterstitial {  }
        }

        binding.ivCave.setOnClickListener {
            playCave(fileName, fileNameNew)
            recordName = getString(R.string.indoor)
            recordImage = R.drawable.ic_indoor
            binding.ivCheckedOriginal.invisible()
            binding.ivCheckedHelium.invisible()
            binding.ivCheckedRobot.invisible()
            binding.ivCheckedRadio.invisible()
            binding.ivCheckedBackWard.invisible()
            binding.ivCheckedCave.visible()
            showInterstitial {  }
        }

        binding.save.setOnClickListener {
//            saveAudio(fileNameNew)
            pauseAudio()
            binding.imgPause.setImageResource(R.drawable.ic_start)
            isPlaying = false
            showSaveDialog(this)
            showInterstitial {  }
        }

        binding.imgReplay.setOnClickListener {
            //  fileName = fileNameNew
            rePlayAudio()
            Log.d("play_record", "replay")
        }
        binding.imgCut.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }

        binding.imgPause.setOnClickListener {
            if (isPlaying) {
                binding.imgPause.setImageResource(R.drawable.ic_start)
                pauseAudio()
                isPlaying = false
            } else {
                binding.imgPause.setImageResource(R.drawable.ic_small_pause)
                resumeAudio()
                isPlaying = true
            }
        }

        binding.imgBack.setOnClickListener {
            pauseAudio()
            showExitDialog()
        }
    }

    override fun setupData() {
        loadAds()
        initAdsManager()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isPlaying", isPlaying)
        outState.putString("recordName", recordName)
        outState.putInt("recordImage", recordImage)
        outState.putString("fileName", fileName)
        outState.putString("fileNameNew", fileNameNew)
        outState.putString("fileNameSave", fileNameSave)
        outState.putBoolean("isEffectAddedOnce", isEffectAddedOnce)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isPlaying = savedInstanceState.getBoolean("isPlaying")
        recordName = savedInstanceState.getString("recordName")!!
        recordImage = savedInstanceState.getInt("recordImage")
        fileName = savedInstanceState.getString("fileName")!!
        fileNameNew = savedInstanceState.getString("fileNameNew")!!
        fileNameSave = savedInstanceState.getString("fileNameSave")!!
        isEffectAddedOnce = savedInstanceState.getBoolean("isEffectAddedOnce")
    }

    private fun start() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileNameNew)
                prepare()
                start()
                setOnCompletionListener {
                    // Bắt sự kiện khi phát lại kết thúc
                    if (player != null) {
                        binding.seekBar.progress = player!!.duration
                    }
                    start() // Bắt đầu phát lại từ đầu
                    Log.d("Check_sound_replay", "sound in effect:")
                }
                Log.d("Check_file", "File name New: $fileNameNew")
            } catch (e: IOException) {
                Log.e("tag", "prepare() failed")
            }
        }
    }

    private fun startRecord() {
        player?.stop()
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                Log.d("Check_file", "File name in start record: $fileName")
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("tag", "prepare() failed")
            }
        }
    }

    private fun showSaveDialog(context: Context) {
        val dialog = Dialog(this@EditRecordActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.setContentView(R.layout.dialog_save_file)
        dialog.setCancelable(true)
        dialog.show()
        dialog.window!!.setAttributes(lp)

        val editTextFileName = dialog.findViewById<TextInputEditText>(R.id.editTextFileName)
        val textInputLayoutFileName =
            dialog.findViewById<TextInputLayout>(R.id.textInputLayoutFileName)
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        recordName = "$recordName-$timestamp"
        editTextFileName.setText(recordName)

        val btnNegative = dialog.findViewById<TextView>(R.id.btnCancel)
        val btnPositive = dialog.findViewById<TextView>(R.id.btnSave)
        btnPositive.setOnClickListener {
            val fileName = editTextFileName.text.toString()
            if (fileName.isNotEmpty()) {

                // Perform save operation with the file name
                saveAudio(fileNameNew, fileName)
                Toast.makeText(context, "File saved as: $fileName", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Dismiss the dialog after saving
            } else {
                textInputLayoutFileName.error = "Please enter file name"
            }
        }
        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    /**
     * Function to execute FFMPEG Query
     */
    private fun exceuteFFMPEG(cmd: Array<String>) {
        val rc = FFmpeg.execute(cmd) // Chạy FFmpeg và lấy mã trả về
        val output = Config.getLastCommandOutput()
        when (rc) {
            RETURN_CODE_SUCCESS -> {
                Log.i("GetInfo", "Command execution completed successfully.")
                hideProgress()
                isEffectAddedOnce = true
                start()
            }

            RETURN_CODE_CANCEL -> {
                Log.i("GetInfo", "Command execution cancelled by user.")
            }

            else -> {
                Log.i(
                    "GetInfo",
                    String.format(
                        "Command execution failed with rc=%d and output=%s.",
                        rc,
                        output
                    )
                )
            }
        }
    }

    // * Function used to play the audio like a Radio
    private fun playRadio(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "atempo=1",
            fileName2
        )//Radio
        exceuteFFMPEG(cmd)
    }

    /**
     * Function used to play the audio like a Chipmunk
     */
    private fun playChipmunk(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "asetrate=22100,atempo=1/2",
            fileName2
        )//Chipmunk
        exceuteFFMPEG(cmd)
    }

    /**
     * Function used to play the audio like a Robot
     */
    private fun playRobot(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "asetrate=11100,atempo=4/3,atempo=1/2,atempo=3/4",
            fileName2
        )//Robot
        exceuteFFMPEG(cmd)
    }

    /**
     * Function used to play the audio like a Cave
     */
    private fun playCave(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "aecho=0.8:0.9:1000:0.3",
            fileName2
        )//Cave
        Log.d("Check_file", "CMD cave : ${cmd.joinToString()}")
        exceuteFFMPEG(cmd)
    }

    private fun playOriginal(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            fileName2
        )

        Log.d("Check_file", "CMD original : ${cmd.joinToString()}")

        exceuteFFMPEG(cmd)
    }


    private fun playBackward(fileName1: String, fileName2: String) {
        showProgress()
        player?.stop()
        val cmd = arrayOf(
            "-y",
            "-i",
            fileName1,
            "-af",
            "asetrate=32100,atempo=4/3,atempo=1/2",
            fileName2
        )
        exceuteFFMPEG(cmd)
    }

    override fun onPause() {
        super.onPause()
        pauseAudio()
        isPlaying = false
    }

    override fun onResume() {
        super.onResume()
        resumeAudio()
        isPlaying = true
    }

    private fun showProgress() {
        binding.progressCircular.visible()
    }

    private fun hideProgress() {
        binding.progressCircular.gone()
    }

    private fun saveAudio(file: String, fileName: String) {
        //convert time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val date = inputFormat.parse(timestamp)
        val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US)
        val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
        val formattedDate = dateFormatter.format(date!!)
        val formattedTime = timeFormatter.format(date)

        Log.d("List_record", "$formattedDate | $formattedTime")

        val strDateTime = "$formattedDate | $formattedTime"
        fileNameSave = "audio_$timestamp.mp3" // create new file with recent time
        val newFile = File(filesDir, fileNameSave)
        val inputStream = FileInputStream(File(file))
        val outputStream = FileOutputStream(newFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        val record = Record(
            fileName,
            recordImage,
            strDateTime,
            newFile.path
        )

        audioFileList.add(record)
        Log.d("List_record", "size: " + audioFileList.size.toString())
        Utils.saveAudioList(this, audioFileList)

        val list = Utils.getAudioList(this)
        Log.d("List_record", "size sau khi get: " + list.size.toString())
        Log.d("List_record", "input_Stream: $fileName")
        Log.d("List_record", "output_stream: $newFile")
        Log.d("List_record", "file_path: ${record.filePath}")

        val intent = Intent(this@EditRecordActivity, MainActivity::class.java)
        intent.putExtra("from_record", "record")
        startActivity(intent)
        finish()
    }

    private fun playAudio() {

        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                Log.d("Check_file", "File name in play audio: $fileName")
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("tag", "prepare() failed")
            }
        }
    }

    private fun rePlayAudio() {
        player?.stop()
        player = MediaPlayer().apply {
            try {
                setDataSource(fileNameNew)
                Log.d("Check_file", "File name in Replay audio: $fileNameNew")
                prepare()
                start()
                setOnCompletionListener {
                    // Bắt sự kiện khi phát lại kết thúc
                    if (player != null) {
                        binding.seekBar.progress = player!!.duration
                    }
                    start() // Bắt đầu phát lại từ đầu
                    Log.d("Check_sound_replay", "sound in replay:")
                }
            } catch (e: IOException) {
                Log.e("tag", "prepare() failed")
            }
        }
    }

    private fun stopAudio() {
        player?.apply {
            if (isPlaying) {
                stop()
            }
        }
    }

    private fun pauseAudio() {
        player?.apply {
            if (isPlaying) {
                pause()
            }
        }
    }

    private fun resumeAudio() {
        player?.apply {
            if (!isPlaying) {
                start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
        handler.removeCallbacks(runnable!!)
    }

    private fun showExitDialog() {
        val dialogCustomExit = Dialog(this@EditRecordActivity)
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
            val intent = Intent(this@EditRecordActivity, MainActivity::class.java)
            startActivity(intent)
            showInterstitial(false) {  }
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
            val keyAdInterAllPrice = FirebaseRemoteConfig.getInstance()
                .getString(RemoteConfigKey.INTER_RECORD)
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

                            val analytics = FirebaseAnalytics.getInstance(this@EditRecordActivity)
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