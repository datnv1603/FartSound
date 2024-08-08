package com.wa.pranksound.ui.component.settings

import android.os.Bundle
import com.wa.pranksound.R
import com.wa.pranksound.databinding.ActivitySettingsBinding
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.utils.Utils

class SettingsActivity : BaseBindingActivity<ActivitySettingsBinding, SettingsViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_settings

    override fun getViewModel(): Class<SettingsViewModel> = SettingsViewModel::class.java

    override fun setupData() {

    }

    override fun setupView(savedInstanceState: Bundle?) {
        binding.swVibrate.isChecked = Utils.getVibration(this)

        binding.swVibrate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utils.setVibration(this, true)
            } else {
                Utils.setVibration(this, false)
            }

        }
    }
}