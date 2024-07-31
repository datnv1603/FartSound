package com.wa.pranksound.adapter;



import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.wa.pranksound.R;
import com.wa.pranksound.activity.SoundDetailActivity;

import org.jetbrains.annotations.NotNull;


public class HorizontalSoundAdapter extends Adapter<HorizontalSoundAdapter.MyViewHolder> {

    private Activity context;
    String[] images;
    String strCateName;


    public HorizontalSoundAdapter(Activity context, String[] images, String strCateName) {
        this.context = context;
        this.images = images;
        this.strCateName = strCateName;

    }

    @Override
    public int getItemCount() {
        return images.length;
    }


    public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        int number = i + 1;
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


        myViewHolder.tvTitle.setText(context.getString(R.string.sound) + " " + number);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SoundDetailActivity.class);
                intent.putExtra(is_fav, false);
                intent.putExtra(music_name, "Sound " + number);
                intent.putExtra(cate_name, strCateName);
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
