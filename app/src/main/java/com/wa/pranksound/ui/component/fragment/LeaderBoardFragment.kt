package com.wa.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.adjust.sdk.Adjust
import com.wa.pranksound.databinding.FragmentLeaderBoardBinding
import com.wa.pranksound.ui.component.sound.SoundListActivity
import com.wa.pranksound.ui.component.main.MainActivity
import com.wa.pranksound.ui.component.main.MainViewModel
import com.wa.pranksound.utils.KeyClass
import com.wa.pranksound.utils.extention.setOnSafeClick

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LeaderBoardFragment : Fragment() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentLeaderBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderBoardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mMainActivity = activity as MainActivity
        binding.viewAirHorn.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "air_horn")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewHairClipper.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "hair_clipper")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewFart.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "fart")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewGhost.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "ghost")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewHalloween.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "halloween")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewSnore.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "snore")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewGun.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "gun")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
        }
        binding.viewCountDown.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "count_down")
            requireContext().startActivity(intent)
            mMainActivity.showInterstitial { }
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

    companion object {
        // Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeaderBoardFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}