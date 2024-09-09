package com.joya.pranksound.ui.component.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.adjust.sdk.Adjust
import com.joya.pranksound.adapter.VerticalFavoriteSoundAdapter
import com.joya.pranksound.room.AppDatabase
import com.joya.pranksound.room.InsertPrankSound
import com.joya.pranksound.room.QueryClass
import com.joya.pranksound.databinding.FragmentFavoritesBinding
import com.joya.pranksound.ui.component.main.MainActivity
import com.joya.pranksound.ui.component.main.MainViewModel
import com.joya.pranksound.utils.extention.gone
import com.joya.pranksound.utils.extention.visible

class FavoritesFragment : Fragment() {

    private lateinit var mMainActivity: MainActivity
    private lateinit var mMainViewModel: MainViewModel

    private lateinit var binding: FragmentFavoritesBinding

    var imgBack: ImageView? = null
    private var queryClass: QueryClass? = null
    private var arrFavPrankSound: List<InsertPrankSound>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMainActivity = activity as MainActivity
        mMainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val db = Room.databaseBuilder(
            requireActivity(),
            AppDatabase::class.java, "prank_sound"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        queryClass = db.queryClass()

    }

    override fun onResume() {
        super.onResume()
        arrFavPrankSound = queryClass!!.getAllFavSound()
        if ((arrFavPrankSound as MutableList<InsertPrankSound>?)!!.size > 0) {
            arrFavPrankSound?.reversed()
            binding.tvNotFound.gone()
            binding.rvSound.visible()
            val verticalSoundAdapter =
                VerticalFavoriteSoundAdapter(arrFavPrankSound as List<InsertPrankSound>) {
                }
            binding.rvSound.setAdapter(verticalSoundAdapter)
        } else {
            binding.rvSound.gone()
            binding.tvNotFound.visible()
        }

        Adjust.onResume()
    }

    override fun onPause() {
        super.onPause()
        Adjust.onPause()
    }
}