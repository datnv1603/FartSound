package com.joya.pranksound.ui.component.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.ActivitySettingsBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.dialog.DialogRating
import com.joya.pranksound.ui.component.multilang.MultiLangActivity
import com.joya.pranksound.ui.component.policy.PolicyActivity
import com.joya.pranksound.utils.Utils
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.setOnSafeClick

class SettingsActivity : BaseBindingActivity<ActivitySettingsBinding, SettingsViewModel>() {

    private val ratingDialog: DialogRating by lazy {
        DialogRating().apply {
            onRating = {
                toast(getString(R.string.thank_you))
                ratingDialog.dismiss()
            }
            onClickFiveStar = {
                binding.rate.gone()
                toast(getString(R.string.thank_you))
                ratingDialog.dismiss()
            }

            onDismiss = {

            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_settings

    override fun getViewModel(): Class<SettingsViewModel> = SettingsViewModel::class.java

    override fun setupData() {

    }

    override fun setupView(savedInstanceState: Bundle?) {

        binding.imgBack.setOnSafeClick {
            finish()
        }

        binding.swVibrate.isChecked = Utils.getVibration(this)

        binding.swVibrate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utils.setVibration(this, true)
            } else {
                Utils.setVibration(this, false)
            }

        }

        binding.language.setOnSafeClick {
            val intent = Intent(this, MultiLangActivity::class.java)
            intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SETTING)
            startActivity(intent)
        }

        if (SharedPreferenceHelper.getBoolean(Constant.KEY_IS_RATE, false)) {
            binding.rate.gone()
        }
        binding.rate.setOnSafeClick {
            if (!ratingDialog.isAdded)
                ratingDialog.show(supportFragmentManager, null)
        }

        binding.policy.setOnSafeClick {
            startActivity(Intent(this, PolicyActivity::class.java))
        }

        binding.shareApp.setOnSafeClick {
            shareApp()
        }

        binding.moreApp.setOnSafeClick {
            openGooglePlayStore()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        val shareMessage =
            "${getString(R.string.app_name)} \n https://play.google.com/store/apps/details?id=${packageName}"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)))
    }

    private fun openGooglePlayStore() {
        val uri =
            Uri.parse("https://play.google.com/store/apps/dev?id=6851346720901755210")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    uri
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }


}