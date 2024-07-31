package com.wa.pranksound.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wa.pranksound.R
import com.wa.pranksound.databinding.ActivityRecordBinding
import com.wa.pranksound.utils.BaseActivity
import java.io.File
import java.io.IOException

class RecordActivity : BaseActivity() {
    private lateinit var binding: ActivityRecordBinding
    private val LOG_TAG = "AudioRecordTest"
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private var fileName: String = ""
    private var playerBackground: MediaPlayer? = null
    private var playerEffects: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private lateinit var countDownTimer: CountDownTimer

    private var permissions: Array<String> =
        arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = File(this.filesDir, "audioRecord.mp3").toString()

        binding.imgMicro.setOnClickListener {
            if (!checkPermissions()) {
                checkPer()
            } else {

                startRecording()
                binding.imgRecording.visibility = View.VISIBLE
                binding.imgRecord.visibility = View.INVISIBLE
                binding.llStartRecord.visibility = View.VISIBLE
                binding.llRecordHere.visibility = View.GONE
                Log.d(LOG_TAG, "da co quyen va bat dau ghi am")
            }
        }

        binding.imgReload.setOnClickListener {
            stopRecording()
            countDownTimer.cancel()
            binding.txtCountTime.text = getString(R.string.time_record)
            binding.imgStartRecord.visibility = View.VISIBLE
            binding.imgPauseRecord.visibility = View.GONE
            binding.imgStartRecord.isEnabled = true
        }

        binding.imgStartRecord.setOnClickListener {
            startRecording()
            binding.imgStartRecord.visibility = View.GONE
            binding.imgPauseRecord.visibility = View.VISIBLE
            binding.imgRecording.visibility = View.VISIBLE
            binding.imgRecord.visibility = View.INVISIBLE
        }

        binding.imgPauseRecord.setOnClickListener {
            stopRecording()
            countDownTimer.cancel()
            Log.d("LOG_TAG", "click pause")
            binding.imgStartRecord.visibility = View.VISIBLE
            binding.imgPauseRecord.visibility = View.GONE
            binding.imgStartRecord.isEnabled = false
        }

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgNext.setOnClickListener {
            stopRecording()
            val intent = Intent(this@RecordActivity, EditRecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkPer() {
        // Kiểm tra xem quyền đã được cấp hay chưa
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
            // Yêu cầu quyền nếu chưa được cấp
            Log.d(LOG_TAG, "Yêu cầu quyền nếu chưa được cấp")
            ActivityCompat.requestPermissions(
                this@RecordActivity,
                permissions,
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            // Quyền đã được cấp, tiếp tục thực hiện các hành động cần thiết
            Log.d(LOG_TAG, "Quyền đã được cấp, tiếp tục thực hiện các hành động cần thiết")
//           binding.llRecordHere.visibility = View.GONE
//           binding.llStartRecord.visibility = View.VISIBLE
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
                // Kiểm tra kết quả của yêu cầu quyền ghi âm
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Quyền ghi âm được cấp, tiếp tục thực hiện các hành động cần thiết
                    Log.d(
                        LOG_TAG,
                        " Quyền ghi âm được cấp, tiếp tục thực hiện các hành động cần thiết"
                    )
//                    binding.llRecordHere.visibility = View.GONE
//                    binding.llStartRecord.visibility = View.VISIBLE
                } else {
                    // Quyền ghi âm không được cấp, xử lý khi quyền bị từ chối
                    Log.d(LOG_TAG, "Quyền ghi âm không được cấp, xử lý khi quyền bị từ chối")
                }
            }
            // Xử lý cho các quyền khác (nếu cần)
        }
    }

    private fun checkPermissions(): Boolean {
        val permission = Manifest.permission.RECORD_AUDIO
        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }


    fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun startRecording() {
        var time: String
        countDownTimer = object : CountDownTimer(30000, 1000) {
            var secondsLeft = 0
            override fun onTick(millisUntilFinished: Long) {
                secondsLeft++
                val timeString = formatTime(secondsLeft)
                time = "$timeString/00:30"
                binding.txtCountTime.text = time
            }
            override fun onFinish() {
                stopRecording()
                countDownTimer.cancel()
                val intent = Intent(this@RecordActivity, EditRecordActivity::class.java)
                startActivity(intent)
            }
        }
        countDownTimer.start()

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        binding.imgRecording.visibility = View.INVISIBLE
        binding.imgRecord.visibility = View.VISIBLE
        playerEffects?.stop()
        playerBackground?.stop()

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        playerBackground?.release()
        playerEffects?.release()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        playerBackground = null
        playerEffects = null

    }
}