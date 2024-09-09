package com.joya.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.joya.pranksound.adapter.CategoriesAdapter
import com.joya.pranksound.databinding.FragmentHomeBinding
import com.joya.pranksound.ui.component.record.RecordActivity
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.ui.component.main.MainViewModel
import com.joya.pranksound.utils.extention.setOnSafeClick
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private lateinit var binding: FragmentHomeBinding
    private var cate: List<String>? = null

    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        try {
            cate = requireActivity().assets.list("prank_sound")?.toList()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        cate?.let {
            categoriesAdapter = CategoriesAdapter(it) {
            }
        }

        binding.rcvCate.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvCate.setAdapter(categoriesAdapter)

        initAction()
    }

    private fun initAction() {
        binding.btnCreateEffect.setOnSafeClick {
            val intent = Intent(requireActivity(), RecordActivity::class.java)
            startActivity(intent)
        }
    }

}