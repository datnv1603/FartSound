package com.wa.pranksound.activity

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
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.wa.pranksound.R
import com.wa.pranksound.databinding.ActivityEditRecordBinding
import com.wa.pranksound.model.Record
import com.wa.pranksound.utils.AdsUtils
import com.wa.pranksound.utils.Utils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditRecordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditRecordBinding

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fileName = File(this.filesDir, "audioRecord.mp3").toString()

        fileNameNew = File(this.filesDir, "audioRecordNew.mp3").toString()

        audioFileList = Utils.getAudioList(this).toMutableList()

        // startRecord()
        playOriginal(fileName, fileNameNew)

        //setting seekbar
        val length: Int = player!!.duration // duration in time in millis


        val durationText =
            DateUtils.formatElapsedTime((length / 1000).toLong()) // converting time in millis to minutes:second format eg 14:15 min

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
                handler.postDelayed(runnable!!, 5) // Cập nhật mỗi giây
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


        binding.ivChipmunk.setOnClickListener {
            playChipmunk(fileName, fileNameNew)

            recordName = getString(R.string.helium)
            recordImage = R.drawable.ic_helium
        }

        binding.ivRobot.setOnClickListener {
            playRobot(fileName, fileNameNew)
            recordName = getString(R.string.robot)
            recordImage = R.drawable.ic_robot
        }

        binding.ivCave.setOnClickListener {
            playCave(fileName, fileNameNew)
            recordName = getString(R.string.indoor)
            recordImage = R.drawable.ic_indoor
        }

        binding.ivRadio.setOnClickListener {
            playRadio(fileName, fileNameNew)
            recordName = getString(R.string.radio)
            recordImage = R.drawable.ic_radio
        }
        binding.original.setOnClickListener {
            //  startRecord()
            playOriginal(fileName, fileNameNew)
            recordName = getString(R.string.original)
            recordImage = R.drawable.ic_original
        }

        binding.backward.setOnClickListener {
            playBackward(fileName, fileNameNew)
            recordName = getString(R.string.backward)
            recordImage = R.drawable.ic_backward
        }

        binding.save.setOnClickListener {
//            saveAudio(fileNameNew)
            pauseAudio()
            binding.imgPause.setImageResource(R.drawable.img_edit_record_play)
            isPlaying = false
            showSaveDialog(this)
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
                binding.imgPause.setImageResource(R.drawable.img_edit_record_play)
                pauseAudio()
                isPlaying = false
            } else {
                binding.imgPause.setImageResource(R.drawable.img_edit_record_pause)
                resumeAudio()
                isPlaying = true
            }
        }

        binding.imgBack.setOnClickListener {
            pauseAudio()
            showExitDialog()
        }


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

    fun showSaveDialog(context: Context) {

        val dialog = Dialog(context)
        // Set the layout for the dialog
        dialog.setContentView(R.layout.dialog_save_file)
        // Set width and height to match parent
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_item_language)
        // Find views in the dialog layout
        val editTextFileName = dialog.findViewById<TextInputEditText>(R.id.editTextFileName)
        val textInputLayoutFileName =
            dialog.findViewById<TextInputLayout>(R.id.textInputLayoutFileName)
        val timestamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        recordName = "$recordName-$timestamp"
        editTextFileName.setText(recordName)

        // Set up the buttons
        dialog.findViewById<TextView>(R.id.btnSave)?.setOnClickListener {
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
        dialog.findViewById<TextView>(R.id.btnCancel)?.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog when Cancel button is clicked
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


    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        player?.release()
        player = null
    }

    private fun showProgress() {
        binding.progressCircular.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressCircular.visibility = View.GONE
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
        fileNameSave = "audio_$timestamp.mp3" // Tạo tên tệp mới với thời gian hiện tại
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
            AdsUtils.loadAndShowInterstitialAd(
                this,
                AdsUtils.interAdHolder,
                object : AdsUtils.loadAndShow {
                    override fun onAdClose() {
                        val timestamp =
                            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
                        recordName = "$recordName-$timestamp"
                        saveAudio(fileNameNew, recordName)
                    }

                    override fun onAdFailed() {
                        val timestamp =
                            SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
                        recordName = "$recordName-$timestamp"
                        saveAudio(fileNameNew, recordName)
                    }

                })

        }
        btnNegative.setOnClickListener {
            AdsUtils.loadAndShowInterstitialAd(
                this,
                AdsUtils.interAdHolder,
                object : AdsUtils.loadAndShow {
                    override fun onAdClose() {
                        val intent = Intent(this@EditRecordActivity, MainActivity::class.java)
                        intent.putExtra("from_record", "record")
                        startActivity(intent)
                    }

                    override fun onAdFailed() {
                        val intent = Intent(this@EditRecordActivity, MainActivity::class.java)
                        intent.putExtra("from_record", "record")
                        startActivity(intent)
                    }
                })
        }
    }
}