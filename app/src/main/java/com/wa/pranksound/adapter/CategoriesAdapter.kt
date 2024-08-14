package com.wa.pranksound.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wa.pranksound.R
import com.wa.pranksound.adapter.CategoriesAdapter.CategoriesViewHolder
import com.wa.pranksound.ui.component.sound.SoundListActivity
import com.wa.pranksound.utils.KeyClass
import java.util.Locale

class CategoriesAdapter(private val folderNames: List<String>, private val callBack:() -> Unit) :
    RecyclerView.Adapter<CategoriesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val folderName = folderNames[position]
        holder.bind(folderName)
        val context = holder.itemView.context

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SoundListActivity::class.java)
            intent.putExtra(KeyClass.cate_name, folderName)
            context.startActivity(intent)
            callBack.invoke()
        }
    }

    override fun getItemCount(): Int {
        return folderNames.size
    }

    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderImage: ImageView = itemView.findViewById(R.id.folder_image)
        var folderNameText: TextView = itemView.findViewById(R.id.folder_name)

        fun bind(folderName: String) {
            // Load image based on folderName

            var folderName = folderName
            if (folderName == KeyClass.air_horn) {
                folderImage.setImageResource(R.drawable.img_air_horn)
            } else if (folderName == KeyClass.hair_clipper) {
                folderImage.setImageResource(R.drawable.img_hair_clipper)
            } else if (folderName == KeyClass.fart) {
                folderImage.setImageResource(R.drawable.img_fart)
            } else if (folderName == KeyClass.count_down) {
                folderImage.setImageResource(R.drawable.img_count_down)
            } else if (folderName == KeyClass.ghost) {
                folderImage.setImageResource(R.drawable.img_ghost)
            } else if (folderName == KeyClass.halloween) {
                folderImage.setImageResource(R.drawable.img_halloween)
            } else if (folderName == KeyClass.snore) {
                folderImage.setImageResource(R.drawable.img_snore)
            } else if (folderName == KeyClass.gun) {
                folderImage.setImageResource(R.drawable.img_gun)
            }
            // Set folder name
            folderName = folderName.replace("_", " ")
            val capitalizedText =
                folderName.substring(0, 1).uppercase(Locale.getDefault()) + folderName.substring(1)
                    .lowercase(
                        Locale.getDefault()
                    )
            folderNameText.text = capitalizedText
        }
    }
}

