package com.wa.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wa.pranksound.ui.component.activity.FeedbackActivity
import com.wa.pranksound.databinding.FragmentSettingBinding
import com.wa.pranksound.utils.Utils


class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swVibrate.isChecked = Utils.getVibration(requireActivity())

        binding.swVibrate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Utils.setVibration(requireActivity(), true)
            } else {
                Utils.setVibration(requireActivity(), false)
            }

        }

        binding.llShare.setOnClickListener {
            shareText()
            Log.d("click_btn","share")
        }

        binding.llRate.setOnClickListener {

        }

        binding.llFeedback.setOnClickListener {
            val intent = Intent(requireActivity(), FeedbackActivity::class.java)
            startActivity(intent)
        }
    }
    private fun shareText() {
        val text = "Air Horn"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"

        }
        startActivity(Intent.createChooser(sendIntent, null))
    }
}