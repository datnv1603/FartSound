package com.joya.pranksound.ui.component.record

import android.annotation.SuppressLint
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
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
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
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.ActivityEditRecordBinding
import com.joya.pranksound.model.Record
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.utils.RemoteConfigKey
import com.joya.pranksound.utils.Utils
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.invisible
import com.joya.pranksound.utils.extention.visible
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.pow

class EditRecordActivity : BaseBindingActivity<ActivityEditRecordBinding, EditRecordViewModel>() {

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
        var length = 0
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
        }

        binding.save.setOnClickListener {
//            saveAudio(fileNameNew)
            pauseAudio()
            binding.imgPause.setImageResource(R.drawable.ic_start)
            isPlaying = false
            showSaveDialog(this)

        }

        binding.imgReplay.setOnClickListener {
            //  fileName = fileNameNew
            rePlayAudio()
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
                }
            } catch (e: IOException) {
                Timber.tag("tag").e("prepare() failed")
            }
        }
    }

    private fun startRecord() {
        player?.stop()
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Timber.tag("tag").e("prepare() failed")
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
                hideProgress()
                isEffectAddedOnce = true
                start()
            }

            RETURN_CODE_CANCEL -> {
            }

            else -> {
                Timber.tag("GetInfo").i(
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

        Adjust.onPause()
    }

    override fun onResume() {
        super.onResume()
        resumeAudio()
        isPlaying = true

        Adjust.onResume()
    }

    private fun showProgress() {
        binding.progressCircular.visible()
    }

    private fun hideProgress() {
        binding.progressCircular.gone()
    }

    @SuppressLint("SimpleDateFormat")
    private fun saveAudio(file: String, fileName: String) {
        //convert time
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val inputFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        val date = inputFormat.parse(timestamp)
        val dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US)
        val timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US)
        val formattedDate = dateFormatter.format(date!!)
        val formattedTime = timeFormatter.format(date)


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
        Utils.saveAudioList(this, audioFileList)

        val intent = Intent(this@EditRecordActivity, MainActivity::class.java)
        intent.putExtra("from_record", "record")
        startActivity(intent)
        finish()
    }

    private fun playAudio() {

        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                start()
            } catch (e: IOException) {
                Timber.tag("tag").e("prepare() failed")
            }
        }
    }

    private fun rePlayAudio() {
        player?.stop()
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
                }
            } catch (e: IOException) {
                Timber.tag("tag").e("prepare() failed")
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
        }
        btnNegative.setOnClickListener {
            dialogCustomExit.dismiss()
        }
    }
}