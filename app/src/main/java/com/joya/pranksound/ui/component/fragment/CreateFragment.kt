package com.joya.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.adjust.sdk.Adjust
import com.joya.pranksound.ui.component.record.RecordActivity
import com.joya.pranksound.adapter.RecordAdapter
import com.joya.pranksound.databinding.FragmentCreateBinding
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.ui.component.main.MainViewModel
import com.joya.pranksound.utils.Utils


class CreateFragment : Fragment() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private lateinit var binding: FragmentCreateBinding

    private lateinit var recordAdapter: RecordAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.btnCreateEffect.setOnClickListener {
            val intent = Intent(requireActivity(), RecordActivity::class.java)
            startActivity(intent)
            mMainActivity.showInterstitial { }
        }

        val list = Utils.getAudioList(requireActivity())
        for (i in list) {

        }
        if (list.isEmpty()) {
            binding.txtNoFoundTitle.visibility = View.VISIBLE
        } else {
            recordAdapter = RecordAdapter(list) {
                mMainActivity.showInterstitial { }
            }
            binding.rcvRecord.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            binding.rcvRecord.adapter = recordAdapter
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