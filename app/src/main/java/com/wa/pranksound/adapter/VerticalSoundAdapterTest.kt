package com.wa.pranksound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.wa.pranksound.R
import com.wa.pranksound.data.BackGround.getBackground
import com.wa.pranksound.model.Sound
import com.wa.pranksound.utils.extention.gone
import com.wa.pranksound.utils.extention.visible

class VerticalSoundAdapterTest(private var listSound: List<Sound>, private val callBack: (sound: Sound) -> Unit) :
    RecyclerView.Adapter<VerticalSoundAdapterTest.MyViewHolder>() {
    override fun getItemCount(): Int {
        return listSound.size
    }

    private var focusedItemPosition: Int = -1

    override fun onBindViewHolder(myViewHolder: MyViewHolder, position: Int) {
        val sound = listSound[position]
        val context = myViewHolder.imgCate.rootView.context
        myViewHolder.txtSoundName.text = soundName(sound.name)
        myViewHolder.cvBG.setBackgroundResource(getBackground(position))
        Glide.with(context).load("file:///android_asset/" + sound.image).into(myViewHolder.imgCate)

        myViewHolder.itemView.setOnClickListener {
            onItemFocus(position)
            callBack.invoke(sound)
        }

        if (position == focusedItemPosition) {
            myViewHolder.imgSelected.visible()
        } else {
            myViewHolder.imgSelected.gone()
        }
    }

    private fun soundName(sound: String): String {
        val soundName = sound.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return soundName[0]
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_rcv_test, viewGroup, false)
        )
    }

    fun onItemFocus(pos: Int) {
        val previousFocusedItem = focusedItemPosition
        focusedItemPosition = pos

        if (previousFocusedItem >= 0) {
            notifyItemChanged(previousFocusedItem)
        }
        if (focusedItemPosition >= 0) {
            notifyItemChanged(focusedItemPosition)
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCate: ImageView = view.findViewById(R.id.imgCate)
        var txtSoundName: TextView = view.findViewById(R.id.txtSoundName)
        var cvBG: FrameLayout = view.findViewById(R.id.cvBG)
        var imgSelected: ImageButton = view.findViewById(R.id.imgSelected)
    }
}