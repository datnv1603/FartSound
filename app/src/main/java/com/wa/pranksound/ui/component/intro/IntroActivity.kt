package com.wa.pranksound.ui.component.intro

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.adjust.sdk.Adjust
import com.wa.pranksound.R
import com.wa.pranksound.activity.MainActivity
import com.wa.pranksound.common.Constant
import com.wa.pranksound.data.SharedPreferenceHelper
import com.wa.pranksound.databinding.ActivityIntroBinding
import com.wa.pranksound.ui.adapter.IntroAdapter
import com.wa.pranksound.ui.base.BaseBindingActivity
import com.wa.pranksound.utils.extention.setOnSafeClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroActivity : BaseBindingActivity<ActivityIntroBinding, IntroViewModel>() {

    private val introAdapter: IntroAdapter by lazy { IntroAdapter() }
    override val layoutId: Int
        get() = R.layout.activity_intro

    override fun getViewModel(): Class<IntroViewModel> = IntroViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setUpViewPager()
        initListener()
    }

    override fun setupData() {
        //loadAds()
        viewModel.getIntro(this)
        viewModel.introMutableLiveData.observe(this) {
            introAdapter.submitList(it.toMutableList())
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



    private fun startMainActivity() {
        SharedPreferenceHelper.storeBoolean("isFirstRun", false)
        Intent(this@IntroActivity, MainActivity::class.java).apply {
            startActivity(this)
            finish()
        }
    }

    private fun initListener() {
        binding.btnNext.setOnSafeClick(1200) {
            viewModel.introMutableLiveData.value?.size?.let { size ->
                with(binding.viewPager.currentItem) {
                    if (this < size - 1) {
                        binding.viewPager.currentItem = this + 1
                    } else if (this == size - 1) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            SharedPreferenceHelper.storeInt(
                                Constant.KEY_FIRST_SHOW_INTRO,
                                Constant.TYPE_SHOW_LANGUAGE_ACT
                            )

                            withContext(Dispatchers.Main) {
                                startMainActivity()
                                finish()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun setUpViewPager() {
        binding.viewPager.apply {
            adapter = introAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == introAdapter.currentList.size - 1) {
                        binding.btnNext.text = getString(R.string.start)
                    } else {
                        binding.btnNext.text = getString(R.string.next)
                    }
                }
            })
            binding.dotsIndicator.attachTo(this)
        }
    }

/*    private fun loadAds() {
        SplashActivity.adNativeLanguage?.let {
            val adContainer = binding.frNativeAds
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            adContainer.removeAllViews()
            adContainer.addView(it)
        } ?: run {
            binding.frNativeAds.gone()
        }
    }*/
}
