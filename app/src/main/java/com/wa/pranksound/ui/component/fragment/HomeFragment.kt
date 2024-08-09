package com.wa.pranksound.ui.component.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wa.pranksound.adapter.CategoriesAdapter
import com.wa.pranksound.databinding.FragmentHomeBinding
import com.wa.pranksound.ui.component.activity.RecordActivity
import com.wa.pranksound.utils.extention.setOnSafeClick
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var rcv_cate: RecyclerView? = null
    private var cate: List<String>? = null

    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         try {
             cate = requireActivity().assets.list("prank_sound")?.toList()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }



        categoriesAdapter = CategoriesAdapter(requireActivity(), cate)
        binding.rcvCate.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvCate.setAdapter(categoriesAdapter)
        binding.btnCreateEffect.setOnSafeClick {
            val intent = Intent(requireActivity(), RecordActivity::class.java)
            startActivity(intent)
        }
    }
}