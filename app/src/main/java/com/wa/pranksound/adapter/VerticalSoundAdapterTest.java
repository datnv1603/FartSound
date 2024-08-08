package com.wa.pranksound.adapter;

import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.data.BackGround;
import com.wa.pranksound.ui.component.activity.SoundDetailActivity;
import com.wa.pranksound.model.Sound;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VerticalSoundAdapterTest extends RecyclerView.Adapter<VerticalSoundAdapterTest.MyViewHolder> {

    List<Sound> listSound;


    public VerticalSoundAdapterTest(List<Sound> listSound) {
        this.listSound = listSound;
    }

    @Override
    public int getItemCount() {
        return listSound.size();
    }


    @Override
    public void onBindViewHolder(VerticalSoundAdapterTest.MyViewHolder myViewHolder, int position) {

        Sound sound = listSound.get(position);
        sound.getTypeSound();
        Context context = myViewHolder.imgCate.getRootView().getContext();
        myViewHolder.txtSoundName.setText(soundName(sound.getName()));
        myViewHolder.cvBG.setBackgroundResource(BackGround.INSTANCE.getBackground(position));
        Glide.with(context).load("file:///android_asset/" + sound.getImage()).into(myViewHolder.imgCate);

        myViewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SoundDetailActivity.class);
            intent.putExtra(is_fav, false);
            intent.putExtra(music_name, soundName(sound.getName()));
            intent.putExtra(cate_name, sound.getTypeSound());
            intent.putExtra(image_sound, sound.getImage());
            context.startActivity(intent);
        });

    }

    public String soundName(String sound) {
        String[] sound_name = sound.split("\\.");
        return sound_name[0];
    }


    @NotNull
    @Override
    public VerticalSoundAdapterTest.MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new VerticalSoundAdapterTest.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rcv_test, viewGroup, false));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCate, imgAds;
        TextView txtSoundName;
        FrameLayout cvBG;

        public MyViewHolder(View view) {
            super(view);
            imgCate = view.findViewById(R.id.imgCate);
            txtSoundName = view.findViewById(R.id.txtSoundName);
            cvBG = view.findViewById(R.id.cvBG);
        }
    }


}