package com.joya.pranksound.ui.component.record

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
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adjust.sdk.Adjust
import com.joya.pranksound.R
import com.joya.pranksound.databinding.ActivityRecordBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.invisible
import com.joya.pranksound.utils.extention.setOnSafeClick
import com.joya.pranksound.utils.extention.visible
import java.io.File
import java.io.IOException

class RecordActivity : BaseBindingActivity<ActivityRecordBinding, RecordViewModel>() {

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
    }

    override fun setupView(savedInstanceState: Bundle?) {
        initAction()

        fileName = File(this.filesDir, "audioRecord.mp3").toString()
    }

    private fun initAction() {
        binding.imgBack.setOnSafeClick {
            finish()
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

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

}