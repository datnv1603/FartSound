package com.wa.pranksound.adapter

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
import com.wa.pranksound.model.Sound
import com.wa.pranksound.ui.component.sound.SoundDetailActivity
import com.wa.pranksound.utils.KeyClass

class VerticalSoundAdapterTest(private var listSound: List<Sound>, private val callBack: () -> Unit) :
    RecyclerView.Adapter<VerticalSoundAdapterTest.MyViewHolder>() {
    override fun getItemCount(): Int {
        return listSound.size
    }


    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val sound = listSound[position]
        val context = myViewHolder.imgCate.rootView.context
        myViewHolder.txtSoundName.text = soundName(sound.name)
        myViewHolder.cvBG.setBackgroundResource(getBackground(position))
        Glide.with(context).load("file:///android_asset/" + sound.image).into(myViewHolder.imgCate)

        myViewHolder.itemView.setOnClickListener { v: View? ->
            val intent = Intent(context, SoundDetailActivity::class.java)
            intent.putExtra(KeyClass.is_fav, false)
            intent.putExtra(KeyClass.music_name, soundName(sound.name))
            intent.putExtra(KeyClass.cate_name, sound.typeSound)
            intent.putExtra(KeyClass.image_sound, sound.image)
            context.startActivity(intent)
            callBack.invoke()
        }
    }

    fun soundName(sound: String): String {
        val sound_name = sound.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return sound_name[0]
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_rcv_test, viewGroup, false)
        )
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCate: ImageView = view.findViewById(R.id.imgCate)
        var imgAds: ImageView? = null
        var txtSoundName: TextView = view.findViewById(R.id.txtSoundName)
        var cvBG: FrameLayout = view.findViewById(R.id.cvBG)
    }
}