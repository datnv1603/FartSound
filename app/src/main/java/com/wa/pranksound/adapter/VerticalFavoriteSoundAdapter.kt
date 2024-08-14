package com.wa.pranksound.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wa.pranksound.R
import com.wa.pranksound.data.BackGround.getBackground
import com.wa.pranksound.room.InsertPrankSound
import com.wa.pranksound.ui.component.sound.SoundDetailActivity
import com.wa.pranksound.utils.KeyClass


class VerticalFavoriteSoundAdapter(private var arrFavPrankSound: List<InsertPrankSound>, private val callBack:() -> Unit) :
    RecyclerView.Adapter<VerticalFavoriteSoundAdapter.MyViewHolder>() {
    override fun getItemCount(): Int {
        return arrFavPrankSound.size
    }

    override fun onBindViewHolder(
        myViewHolder: MyViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        val context = myViewHolder.imgCate.rootView.context
        val strCateName = arrFavPrankSound[i].folder_name
        val imagePath = arrFavPrankSound[i].image_path
        val isDrawable: Boolean
        if (!imagePath.contains("png")) {
            val resourceId = imagePath.toInt()
            Glide.with(context).load(resourceId).into(myViewHolder.imgCate)
            isDrawable = true
        } else {
            isDrawable = false
            Glide.with(context).load("file:///android_asset/$imagePath").into(myViewHolder.imgCate)
        }

        myViewHolder.cvBG.setBackgroundResource(getBackground(i))
        myViewHolder.txtSoundName.text =
            arrFavPrankSound[i].sound_name.replace("Sound", context.getString(R.string.sound))

        myViewHolder.itemView.setOnClickListener {
            val intent = Intent(context, SoundDetailActivity::class.java)
            intent.putExtra(KeyClass.is_fav, true)
            intent.putExtra(KeyClass.music_name, arrFavPrankSound[i].sound_name)
            intent.putExtra(KeyClass.cate_name, arrFavPrankSound[i].folder_name)
            if (isDrawable) {
                intent.putExtra(KeyClass.image_sound, imagePath.toInt())
            } else {
                intent.putExtra(KeyClass.image_sound, imagePath)
            }
            intent.putExtra(KeyClass.sound_path, arrFavPrankSound[i].sound_path)
            context.startActivity(intent)
            callBack.invoke()
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_rcv_test, viewGroup, false)
        )
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCate: ImageView = view.findViewById(R.id.imgCate)
        var txtSoundName: TextView = view.findViewById(R.id.txtSoundName)
        var cvBG: FrameLayout = view.findViewById(R.id.cvBG)
    }
}
