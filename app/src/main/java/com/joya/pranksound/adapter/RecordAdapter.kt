package com.joya.pranksound.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joya.pranksound.R
import com.joya.pranksound.model.Record
import com.joya.pranksound.ui.component.sound.SoundDetailActivity
import com.joya.pranksound.utils.KeyClass


class RecordAdapter(private val recordList: List<Record>, private val callBack:() -> Unit) :
    RecyclerView.Adapter<RecordAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = recordList[position]
        holder.textViewName.text = record.name
        holder.textViewTime.text = record.time
        val context = holder.itemView.context
        Glide.with(context).load(record.image).into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SoundDetailActivity::class.java)
            intent.putExtra(KeyClass.is_record, true)
            intent.putExtra(KeyClass.is_fav, false)
            intent.putExtra(KeyClass.music_name, record.name)
            intent.putExtra(KeyClass.image_sound, record.image)
            intent.putExtra(KeyClass.cate_name, "Record")
            intent.putExtra(KeyClass.sound_path, record.filePath)
            context.startActivity(intent)
            callBack.invoke()
        }
    }

    override fun getItemCount(): Int {
        return recordList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.textViewName)
        var textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        var imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
