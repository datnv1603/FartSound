package com.wa.pranksound.ui.component.multilang

import android.content.Intent
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.wa.pranksound.R
import com.wa.pranksound.ui.component.activity.MainActivity
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivityMultiLangBinding
import com.wa.pranksound.ui.adapter.MultiLangAdapter
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.ui.component.intro.IntroActivity
import com.wa.pranksound.utils.SystemUtil
import com.wa.pranksound.utils.extention.setOnSafeClick
import com.wa.pranksound.utils.extention.visible

class MultiLangActivity : BaseBindingActivity<ActivityMultiLangBinding, MultiLangViewModel>() {

    private var type: Int = 0
    private var currentPosLanguage = 0
    private var oldCode = "en"
    private var code = ""

    private val multiLangAdapter: MultiLangAdapter by lazy {
        MultiLangAdapter().apply {
            callBack = { pos, item ->
                binding.btnChooseLang.visible()
                code = item.code ?: code
                currentPosLanguage = pos
            }
            binding.rcvLangs.adapter = this
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_multi_lang

    override fun getViewModel(): Class<MultiLangViewModel> = MultiLangViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {

        type = intent.getIntExtra(Constant.TYPE_LANG, 0)
        oldCode = SystemUtil.getPreLanguage(this)
        code = oldCode
        updateUIForType(type)
    }

    override fun setupData() {
        //loadAds()
        viewModel.getListLanguage()
        viewModel.languageLiveData.observe(this) {
            multiLangAdapter.submitList(it)
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



    private fun updateUIForType(type: Int) {

        when (type) {
            Constant.TYPE_LANGUAGE_SPLASH -> {
//                val isFirstRun = SharedPreferenceHelper.getBoolean("isFirstRun", true)
                val isFirstRun = true
                binding.btnChooseLang.setOnClickListener {
                    SystemUtil.changeLang(code.ifEmpty { oldCode }, this)
                    if (isFirstRun) {
                        gotoIntroActivity()
                    } else {
                        gotoMainActivity()
                    }
                    finish()
                }
            }

            Constant.TYPE_LANGUAGE_SETTING -> {
                binding.btnChooseLang.setOnSafeClick {
                    if (oldCode != code) {
                        SystemUtil.changeLang(code.ifEmpty { oldCode }, this)
                        val i = Intent(this, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                    }
                    finish()
                }
            }
        }
    }

    private fun gotoIntroActivity() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

//    private fun loadAds() {
//        SplashActivity.adNativeLanguage?.let {
//            val adContainer = binding.frNativeAds
//            if (it.parent != null) {
//                (it.parent as ViewGroup).removeView(it)
//            }
//            adContainer.removeAllViews()
//            adContainer.addView(it)
//        } ?: run {
//            binding.frNativeAds.gone()
//        }
//    }
}

