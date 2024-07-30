package com.wa.pranksound.fragment

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
import java.io.IOException

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var rcv_cate: RecyclerView? = null
    private var cate: List<String>? = null

    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        Log.d("Categories", cate!!.size.toString())
        for (i in cate!!.indices) {
            Log.d("Categories", cate!!.get(i))
        }



        categoriesAdapter = CategoriesAdapter(requireActivity(), cate)
        binding.rcvCate.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvCate.setAdapter(categoriesAdapter)
    }
}