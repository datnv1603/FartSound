package com.wa.pranksound.ui.component.fragment

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.wa.pranksound.R
import com.wa.pranksound.adapter.VerticalFavoriteSoundAdapter
import com.wa.pranksound.room.AppDatabase
import com.wa.pranksound.room.InsertPrankSound
import com.wa.pranksound.room.QueryClass
import com.wa.pranksound.databinding.FragmentFavoritesBinding
import java.util.Collections


class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding

    var imgBack: ImageView? = null
    var queryClass: QueryClass? = null
    var arrFavPrankSound: List<InsertPrankSound>? = null


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

        val db = Room.databaseBuilder(
           requireActivity(),
            AppDatabase::class.java, "prank_sound"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        queryClass = db.queryClass()

    }



    fun customText(){
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_favorite_in_fav)
        // Chuyển đổi ảnh thành drawable
        val drawable = BitmapDrawable(resources, bitmap)

        val text1 = "Click the button "
        val text2 = " to add sound to your favorites"

        // Tạo một SpannableStringBuilder
        val spannableString = SpannableString(" ")
        val imageSpan = ImageSpan(drawable, ImageSpan.ALIGN_BASELINE)
        drawable.setBounds(0, 0, binding.txtNoFound.lineHeight, binding.txtNoFound.lineHeight)
        spannableString.setSpan(imageSpan, 0, 1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Căn giữa văn bản và hình ảnh
        val alignmentSpan = AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        spannableString.setSpan(alignmentSpan, 0, spannableString.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(text1)
        spannableStringBuilder.append(spannableString)
        spannableStringBuilder.append(text2)


        binding.txtNoFound.text = spannableStringBuilder
    }




    override fun onResume() {
        super.onResume()
        arrFavPrankSound = queryClass!!.getAllFavSound()
        if ((arrFavPrankSound as MutableList<InsertPrankSound>?)!!.size > 0) {
            arrFavPrankSound?.let { Collections.reverse(it) }
            binding.llNoFavorites.visibility = View.GONE
           binding.rvSound.visibility = View.VISIBLE
            val verticalSoundAdapter = VerticalFavoriteSoundAdapter(requireActivity(), arrFavPrankSound)
            val layoutManager = GridLayoutManager(requireContext(), 3)
            binding.rvSound.setLayoutManager(layoutManager)
            binding.rvSound.setAdapter(verticalSoundAdapter)
        } else {
            binding.rvSound.visibility = View.GONE
            binding.llNoFavorites.visibility = View.VISIBLE
            customText()
        }
    }
}