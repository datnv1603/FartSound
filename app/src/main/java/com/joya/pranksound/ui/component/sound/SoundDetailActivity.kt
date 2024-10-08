package com.joya.pranksound.ui.component.sound

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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.room.Room.databaseBuilder
import com.adjust.sdk.Adjust
import com.bumptech.glide.Glide
import com.joya.pranksound.R
import com.joya.pranksound.adapter.HorizontalFavoriteSoundAdapter
import com.joya.pranksound.adapter.HorizontalSoundAdapterTest
import com.joya.pranksound.common.Constant.KEY_IS_FIRST_OPEN_SOUND
import com.joya.pranksound.data.SharedPreferenceHelper.Companion.getBoolean
import com.joya.pranksound.data.SharedPreferenceHelper.Companion.storeBoolean
import com.joya.pranksound.databinding.ActivitySoundDetailBinding
import com.joya.pranksound.databinding.MenuTimerBinding
import com.joya.pranksound.model.Sound
import com.joya.pranksound.room.AppDatabase
import com.joya.pranksound.room.InsertPrankSound
import com.joya.pranksound.room.QueryClass
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.utils.ImageLoader
import com.joya.pranksound.utils.KeyClass
import com.joya.pranksound.utils.Utils.getAudioList
import com.joya.pranksound.utils.Utils.getVibration
import com.joya.pranksound.utils.Utils.removeAfterDot
import com.joya.pranksound.utils.Utils.saveAudioList
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.invisible
import com.joya.pranksound.utils.extention.setOnSafeClick
import com.joya.pranksound.utils.extention.visible
import java.io.IOException
import java.util.Objects

class SoundDetailActivity :
    BaseBindingActivity<ActivitySoundDetailBinding, SoundDetailViewModel>() {

    var isPlaying: Boolean = false
    private var isLoop: Boolean = false
    private var strMusicName: String? = null
    private var systemService: Vibrator? = null
    var mediaPlayer: MediaPlayer? = MediaPlayer()

    var handler: Handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    private var queryClass: QueryClass? = null
    private var strCateName: String? = null
    private var arrFavPrankSound: List<InsertPrankSound?> = ArrayList()
    private var horizontalSoundAdapter: HorizontalFavoriteSoundAdapter? = null
    private var isFav: Boolean = false
    private var countDownTimer: CountDownTimer? = null
    private var cate: List<String>? = null
    private var images: Array<String>? = null
    private var string_img_sound: String? = null
    private var int_img_sound: Int? = null
    private var soundName: String? = null
    private var soundPath: String? = null

    private var audioManager: AudioManager? = null
    private var isRecord: Boolean = false

    override val layoutId: Int
        get() = R.layout.activity_sound_detail

    override fun getViewModel(): Class<SoundDetailViewModel> = SoundDetailViewModel::class.java
    override fun setupView(savedInstanceState: Bundle?) {
        val db =
            databaseBuilder(this, AppDatabase::class.java, "prank_sound").allowMainThreadQueries()
                .fallbackToDestructiveMigration().build()
        queryClass = db.queryClass()
        data
        clickEvent()

        setUpVolume()
    }

    override fun setupData() {
    }

    private fun setUpVolume() {
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

        binding.volumeSeekBar.max = maxVolume
        binding.volumeSeekBar.progress = currentVolume
        binding.btnVolumeSmall.setOnClickListener {
            binding.volumeSeekBar.progress = maxVolume / 5
        }
        binding.btnVolumeLoud.setOnClickListener {
            binding.volumeSeekBar.progress = 4 * maxVolume / 5
        }
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
                val vibratorManager =
                    getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
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
                }
                binding.rvSound.adapter = verticalSoundAdapter
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
        if (isRecord) {
            binding.llMoreSounds.gone()
            binding.btnDelete.visible()
            binding.imgHeart.gone()
        } else {
            binding.btnDelete.gone()
            binding.imgHeart.visible()
            binding.imgHeart.isSelected = insertPrankSound1 != null
        }


        if (strMusicName != null) {
            binding.tvTitle.text = strMusicName
        }

        if (strCateName != null) {
            //load ảnh từ file asset

            if (int_img_sound != 0) {
                Glide.with(this).load(int_img_sound).into(binding.imgItem)
            } else {
                if (!string_img_sound!!.contains("png")) {
                    val bitmap = BitmapFactory.decodeResource(resources, string_img_sound!!.toInt())
                    Glide.with(this).load(bitmap).into(binding.imgItem)
                } else {
                    Glide.with(this).load("file:///android_asset/$string_img_sound").into(
                        binding.imgItem
                    )
                }
            }

            try {
                if (mediaPlayer != null) {
                    mediaPlayer!!.release()
                    stopVibrate()
                    binding.animation.invisible()
                }
                mediaPlayer = MediaPlayer()
                if (strCateName == "record") {
                    soundPath = strMusicName
                }
                //set sound from asset or record
                if (soundPath != null) {
                    try {
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
                runnable = Runnable {
                    if (mediaPlayer != null) {
                        handler.postDelayed(runnable!!, 5)
                    }
                }

                mediaPlayer!!.setOnCompletionListener {
                    if (!isLoop) {
                        isPlaying = false
                        //set anim
                        binding.animation.invisible()
                        stopVibrate()
                    } else {

                        mediaPlayer!!.start()
                        startVibrate()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun startCountDownTimer(i: Int, txtCountTime: TextView?) {
        binding.animation.visibility = View.INVISIBLE
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
                binding.tvOff.setText(R.string._off)
                val text = ""
                txtCountTime!!.text = text
                isPlaying = true
                binding.animation.visibility = View.VISIBLE
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
            binding.animGuide.visible()
            binding.viewBlur.visible()
        } else {
            binding.animGuide.gone()
            binding.viewBlur.gone()
        }

        binding.animGuide.setOnTouchListener { _: View?, _: MotionEvent? ->
            storeBoolean(KEY_IS_FIRST_OPEN_SOUND, false)
            binding.animGuide.gone()
            binding.viewBlur.gone()
            true
        }

        binding.btnBack.setOnSafeClick {
            finish()
        }

        binding.llBtnOff.setOnSafeClick { v: View? ->
            val view = MenuTimerBinding.inflate(layoutInflater)
            val popupWindow = PopupWindow(
                view.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            view.btnOff.setOnSafeClick {
                if (countDownTimer != null) {
                    countDownTimer!!.cancel()
                }
                val text = ""
                binding.tvCountTime.text = text
                binding.tvOff.setText(R.string._off)
                popupWindow.dismiss()
            }
            view.btn5s.setOnSafeClick {
                startCountDownTimer(5000, binding.tvCountTime)
                binding.tvOff.setText(R.string._5s)
                popupWindow.dismiss()
            }
            view.btn10s.setOnSafeClick {
                startCountDownTimer(10000, binding.tvCountTime)
                binding.tvOff.setText(R.string._10s)
                popupWindow.dismiss()
            }
            view.btn30s.setOnSafeClick {
                startCountDownTimer(30000, binding.tvCountTime)
                binding.tvOff.setText(R.string._30s)
                popupWindow.dismiss()
            }
            view.btn1m.setOnSafeClick {
                startCountDownTimer(60000, binding.tvCountTime)
                binding.tvOff.setText(R.string._1m)
                popupWindow.dismiss()
            }
            view.btn5m.setOnSafeClick {
                startCountDownTimer(300000, binding.tvCountTime)
                binding.tvOff.setText(R.string._5m)
                popupWindow.dismiss()
            }

            popupWindow.isOutsideTouchable = true
            popupWindow.isFocusable = true
            popupWindow.setBackgroundDrawable(null)
            popupWindow.showAsDropDown(v)
        }

        binding.imgItem.setOnTouchListener { _: View?, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (mediaPlayer != null) {
                        if (!mediaPlayer!!.isPlaying) {
                            binding.animation.visibility = View.VISIBLE
                            binding.animation.playAnimation()
                            mediaPlayer!!.seekTo(0)
                            mediaPlayer!!.isLooping = true
                            mediaPlayer!!.start()
                            startVibrate()
                            val params = Bundle()
                            if (isRecord) params.putString("Sound_name", "record")
                            else params.putString("Sound_name", soundName)
                        } else {
                            if (isLoop) {
                                mediaPlayer!!.pause()
                                stopVibrate()
                                binding.animation.visibility = View.INVISIBLE
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
                                binding.animation.visibility = View.INVISIBLE
                            }
                        }
                    }
                    false
                }

                else -> false
            }
        }

        //test favourite
        binding.imgHeart.setOnClickListener {
            val insertPrankSound1 = queryClass!!.getFavSound(strCateName, strMusicName)
            if (insertPrankSound1 != null) {
                queryClass!!.getUnFavSound(strCateName, strMusicName)
                binding.imgHeart.isSelected = false
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
                binding.imgHeart.isSelected = true
            }
            if (isFav) {
                arrFavPrankSound = queryClass!!.allFavSound
                if (horizontalSoundAdapter != null) {
                    arrFavPrankSound.reversed()
                    horizontalSoundAdapter!!.notifyDataSetChangedAd(arrFavPrankSound)
                }
            }
        }

        binding.swLoop.setOnClickListener {
            binding.swLoop.isSelected = !binding.swLoop.isSelected
            isLoop = binding.swLoop.isSelected
        }

        binding.btnDelete.setOnClickListener {
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
                binding.animation.visibility = View.INVISIBLE
            }
        }
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        handler.removeCallbacksAndMessages(null)
        handler.removeCallbacks(runnable!!)

        Adjust.onPause()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
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
}