package com.joya.pranksound.adapter;


import static com.joya.pranksound.utils.KeyClass.cate_name;
import static com.joya.pranksound.utils.KeyClass.is_fav;
import static com.joya.pranksound.utils.KeyClass.music_name;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.joya.pranksound.R;
import com.joya.pranksound.ui.component.sound.SoundDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Random;


public class VerticalSoundAdapter extends Adapter<VerticalSoundAdapter.MyViewHolder> {

    private Activity context;
    String[] images;
    String strCateName;


    public VerticalSoundAdapter(Activity context, String[] images, String strCateName) {
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
        final int min = 0;
        final int max = 8;
        final int random = new Random().nextInt((max - min) + 1) + min;
        myViewHolder.txtSoundName.setText(soundName(images[i]));

        myViewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SoundDetailActivity.class);
            intent.putExtra(is_fav, false);
            intent.putExtra(music_name, soundName(images[i]));
            intent.putExtra(cate_name, strCateName);
            context.startActivity(intent);
        });

    }

    public String soundName(String sound) {
        String[] sound_name = sound.split("\\.");
        return sound_name[0];
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item_rcv, viewGroup, false));
    }

    public static class MyViewHolder extends ViewHolder {
        ImageView imgCate;
        TextView txtSoundName;
        LinearLayout cvBG;

        public MyViewHolder(View view) {
            super(view);
            imgCate = view.findViewById(R.id.imgCate);
            txtSoundName = view.findViewById(R.id.txtSoundName);
            cvBG = view.findViewById(R.id.cvBG);


        }
    }


}
