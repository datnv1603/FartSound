package com.wa.pranksound.activity

import android.os.Bundle
import android.widget.Toast
import com.wa.pranksound.databinding.ActivityFeedbackBinding
import com.wa.pranksound.utils.AdsUtils
import com.wa.pranksound.utils.BaseActivity

class FeedbackActivity : BaseActivity() {
    private lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            AdsUtils.loadAndShowInterstitialAd(
                this,
                AdsUtils.interAdHolder,
                object : AdsUtils.loadAndShow {
                    override fun onAdClose() {
                        finish()
                    }

                    override fun onAdFailed() {
                        finish()
                    }

                })

        }



        binding.llBtnSend.setOnClickListener {
            if(binding.edFeedback.text.isNullOrBlank()){
                Toast.makeText(this,"Please enter your feedback",Toast.LENGTH_LONG).show()
            }else{
                finish()
            }

        }
    }
}