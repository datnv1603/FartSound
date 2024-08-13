package com.wa.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wa.pranksound.databinding.FragmentLeaderBoardBinding
import com.wa.pranksound.ui.component.activity.SoundListActivity
import com.wa.pranksound.utils.KeyClass
import com.wa.pranksound.utils.extention.setOnSafeClick

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LeaderBoardFragment : Fragment() {
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
        binding.viewAirHorn.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "air_horn")
            requireContext().startActivity(intent)
        }
        binding.viewHairClipper.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "hair_clipper")
            requireContext().startActivity(intent)
        }
        binding.viewFart.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "fart")
            requireContext().startActivity(intent)
        }
        binding.viewGhost.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "ghost")
            requireContext().startActivity(intent)
        }
        binding.viewHalloween.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "halloween")
            requireContext().startActivity(intent)
        }
        binding.viewSnore.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "snore")
            requireContext().startActivity(intent)
        }
        binding.viewGun.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "gun")
            requireContext().startActivity(intent)
        }
        binding.viewCountDown.setOnSafeClick {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, "count_down")
            requireContext().startActivity(intent)
        }
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