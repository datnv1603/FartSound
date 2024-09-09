package com.joya.pranksound.ui.component.sound

import android.content.Intent
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.joya.pranksound.R
import com.joya.pranksound.adapter.VerticalSoundAdapterTest
import com.joya.pranksound.databinding.ActivitySoundListBinding
import com.joya.pranksound.model.Sound
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.utils.ImageLoader
import com.joya.pranksound.utils.KeyClass
import com.joya.pranksound.utils.extention.invisible
import com.joya.pranksound.utils.extention.setOnSafeClick
import com.joya.pranksound.utils.extention.visible
import java.io.IOException

class SoundListActivity : BaseBindingActivity<ActivitySoundListBinding, SoundListViewModel>() {

    private var sound: Sound? = null

    private val listSound: MutableList<Sound> = ArrayList()
    private val verticalSoundAdapter = VerticalSoundAdapterTest(listSound) { sound ->
        binding.imgOK.visible()
        this.sound = sound
    }

    override val layoutId: Int
        get() = R.layout.activity_sound_list

    override fun getViewModel(): Class<SoundListViewModel> = SoundListViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

    }

    override fun setupData() {
        findView()
    }

    private fun findView() {
        val strCateName = intent.getStringExtra(KeyClass.cate_name)

        try {
            if (strCateName != null) {
                when (strCateName) {
                    KeyClass.air_horn -> {
                        binding.tvTitle.text = getString(R.string.air_horn)
                    }

                    KeyClass.hair_clipper -> {
                        binding.tvTitle.text = getString(R.string.hair_clipper)
                    }

                    KeyClass.fart -> {
                        binding.tvTitle.text = getString(R.string.fart)
                    }

                    KeyClass.count_down -> {
                        binding.tvTitle.text = getString(R.string.count_down)
                    }

                    KeyClass.gun -> {
                        binding.tvTitle.text = getString(R.string.gun)
                    }

                    KeyClass.ghost -> {
                        binding.tvTitle.text = getString(R.string.ghost)
                    }

                    KeyClass.halloween -> {
                        binding.tvTitle.text = getString(R.string.halloween)
                    }

                    KeyClass.snore -> {
                        binding.tvTitle.text = getString(R.string.snore)
                    }

                    KeyClass.test_sound -> {
                        binding.tvTitle.text = getString(R.string.test_sound)
                    }
                }

                val images = assets.list("prank_sound/$strCateName")


                val imageList = ImageLoader.getImageListFromAssets(this, "prank_image/$strCateName")
                if (images != null) {
                    for (i in images.indices) {
                        val sound = if (i >= 10) {
                            Sound(strCateName, imageList[i], images[i], 0, true, isNew = false)
                        } else {
                            Sound(strCateName, imageList[i], images[i], 0, false, isNew = false)
                        }
                        listSound.add(sound)
                    }
                }

                binding.rvSound.setAdapter(verticalSoundAdapter)
                binding.imgOK.setOnSafeClick {
                    openSound()
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        binding.imgBack.setOnSafeClick {
            finish()
        }
    }

    private fun openSound() {
        this.sound?.let {
            intent = Intent(this, SoundDetailActivity::class.java)
            intent.putExtra(KeyClass.is_fav, false)
            intent.putExtra(KeyClass.music_name, soundName(it.name))
            intent.putExtra(KeyClass.cate_name, it.typeSound)
            intent.putExtra(KeyClass.image_sound, it.image)
            this.startActivity(intent)
        }
    }

    private fun soundName(sound: String): String {
        val soundName = sound.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return soundName[0]
    }


    override fun onPause() {
        super.onPause()
        Adjust.onPause()
        verticalSoundAdapter.onItemFocus(-1)
        binding.imgOK.invisible()
    }
}