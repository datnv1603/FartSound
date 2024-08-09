package com.wa.pranksound.ui.component.policy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.wa.pranksound.R

import com.wa.pranksound.BuildConfig
import com.wa.pranksound.databinding.ActivityPolicyBinding
import com.wa.pranksound.ui.base.BaseBindingActivity
import timber.log.Timber

class PolicyActivity : BaseBindingActivity<ActivityPolicyBinding, PolicyViewModel>() {

    override val layoutId: Int
        get() = R.layout.activity_policy

    override fun getViewModel(): Class<PolicyViewModel> = PolicyViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        val text = getString(R.string.version) + " " + BuildConfig.VERSION_NAME
        binding.tvVersion.text = text

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvTerm.setOnClickListener {
            openUrl(getString(R.string.privacy_policy_link))
        }
    }
    override fun setupData() {

    }


    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }

    private fun openUrl(strUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUrl))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        try {
            startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            Timber.tag("").d(e.toString())
        }
    }
}