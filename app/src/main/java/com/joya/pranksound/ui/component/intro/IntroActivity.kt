package com.joya.pranksound.ui.component.intro

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.adjust.sdk.Adjust
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.joya.pranksound.R
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.ActivityIntroBinding
import com.joya.pranksound.databinding.AdNativeContentBinding
import com.joya.pranksound.databinding.AdNativeVideoBinding
import com.joya.pranksound.ui.adapter.IntroAdapter
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.utils.RemoteConfigKey
import com.joya.pranksound.utils.ads.NativeAdsUtils
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.setOnSafeClick
import com.joya.pranksound.utils.extention.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IntroActivity : BaseBindingActivity<ActivityIntroBinding, IntroViewModel>() {

    private val keyNative =
        FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.NATIVE_INTRO)

    private val introAdapter: IntroAdapter by lazy { IntroAdapter() }
    override val layoutId: Int
        get() = R.layout.activity_intro

    override fun getViewModel(): Class<IntroViewModel> = IntroViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        setUpViewPager()
        initListener()
    }

    override fun setupData() {
        loadAds()
        viewModel.getIntro(this)
        viewModel.introMutableLiveData.observe(this) {
            introAdapter.submitList(it.toMutableList())
        }
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

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun loadAds() {
        loadNativeAd()
    }

    private fun loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO)
        ) {
            val isSmallSize = FirebaseRemoteConfig.getInstance()
                .getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_INTRO_SMALL)
            loadNativeAds(keyNative, isSmallSize)

        } else {
            binding.rlNative.gone()
        }
    }

    private fun loadNativeAds(keyAds: String, smallSize: Boolean = true) {
        this.let {
            NativeAdsUtils.instance.loadNativeAds(
                applicationContext,
                keyAds,
                { nativeAds ->
                    if (nativeAds != null) {
                        binding.rlNative.visible()
                        if (smallSize) {
                            val adNativeVideoBinding = AdNativeContentBinding.inflate(layoutInflater)
                            NativeAdsUtils.instance.populateNativeAdVideoView(
                                nativeAds,
                                adNativeVideoBinding.root
                            )
                            binding.frNativeAds.removeAllViews()
                            binding.frNativeAds.addView(adNativeVideoBinding.root)
                        } else {
                            val adNativeVideoBinding = AdNativeVideoBinding.inflate(layoutInflater)
                            NativeAdsUtils.instance.populateNativeAdVideoView(
                                nativeAds,
                                adNativeVideoBinding.root as NativeAdView
                            )
                            binding.frNativeAds.removeAllViews()
                            binding.frNativeAds.addView(adNativeVideoBinding.root)
                        }
                    }
                },
                {
                    //On native ad clicked
                    startMainActivity()
                }
            )
        }
    }
}
