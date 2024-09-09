package com.joya.pranksound.ui.component.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.databinding.ActivitySplashBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.multilang.MultiLangActivity
import com.joya.pranksound.utils.extention.setStatusBarColor

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseBindingActivity<ActivitySplashBinding, SplashViewModel>() {

    val bundle = Bundle()
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun getViewModel(): Class<SplashViewModel> = SplashViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        viewModel.starTimeCount(5000)
    }


    private fun init() {
        viewModel.isCompleteLiveData.observe(this) {
            openChooseLanguageActivity()
            finish()
        }
        val countDownTimer = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                viewModel.starTimeCount(1000)
            }
        }
        countDownTimer.start()
    }

    override fun setupView(savedInstanceState: Bundle?) {
        setStatusBarColor("#11141A")
    }

    override fun setupData() {
    }

    private fun openChooseLanguageActivity() {
        val intent = Intent(this@SplashActivity, MultiLangActivity::class.java)
        intent.putExtra(Constant.TYPE_LANG, Constant.TYPE_LANGUAGE_SPLASH)
        startActivity(intent)
        finish()
    }
}