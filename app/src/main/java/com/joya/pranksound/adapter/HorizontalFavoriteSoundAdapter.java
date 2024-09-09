package com.joya.pranksound.adapter;


import static com.joya.pranksound.utils.KeyClass.cate_name;
import static com.joya.pranksound.utils.KeyClass.is_fav;
import static com.joya.pranksound.utils.KeyClass.music_name;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.joya.pranksound.R;
import com.joya.pranksound.ui.component.sound.SoundDetailActivity;
import com.joya.pranksound.room.InsertPrankSound;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HorizontalFavoriteSoundAdapter extends Adapter<HorizontalFavoriteSoundAdapter.MyViewHolder> {

    private Activity context;
    List<InsertPrankSound> arrFavPrankSound;


    public HorizontalFavoriteSoundAdapter(Activity context, List<InsertPrankSound> arrFavPrankSound) {
        this.context = context;
        this.arrFavPrankSound = arrFavPrankSound;

    }

    @Override
    public int getItemCount() {
        return arrFavPrankSound.size();
    }


    public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {
        if (i % 8 == 0) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.meme));
        } else if (i % 8 == 1) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.car));
        } else if (i % 8 == 2) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.breaking));
        } else if (i % 8 == 3) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.gun));
        } else if (i % 8 == 4) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.toilet_flushing));
        } else if (i % 8 == 5) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.burp));
        } else if (i % 8 == 6) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.fart));
        } else if (i % 8 == 7) {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.hair_clipper));
        } else {
            myViewHolder.cvBG.setCardBackgroundColor(context.getColor(R.color.air_horn));
        }


        myViewHolder.tvTitle.setText(arrFavPrankSound.get(i).sound_name.replace("Sound",context.getString(R.string.sound)));
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check_show_ads","show in list sound");
                Intent intent = new Intent(context, SoundDetailActivity.class);
                intent.putExtra(is_fav, true);
                intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
                intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
                context.startActivity(intent);
                context.finish();
            }
        });

    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_select_item_rcv, viewGroup, false));
    }
    public void notifyDataSetChangedAd(List<InsertPrankSound> arrFavPrankSound){
        this.arrFavPrankSound = arrFavPrankSound;

        notifyDataSetChanged();
    }
    public static class MyViewHolder extends ViewHolder {
        ImageView imgItem;
        TextView tvTitle;
        CardView cvBG;

        public MyViewHolder(View view) {
            super(view);
            imgItem = view.findViewById(R.id.imgItem);
            tvTitle = view.findViewById(R.id.tvTitle);
            cvBG = view.findViewById(R.id.cvBG);


        }
    }


}
