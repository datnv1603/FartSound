package com.joya.pranksound.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joya.pranksound.R;
import com.joya.pranksound.data.BackGround;
import com.joya.pranksound.model.Sound;
import com.joya.pranksound.ui.OnSoundItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HorizontalSoundAdapterTest extends RecyclerView.Adapter<HorizontalSoundAdapterTest.MyViewHolder> {

    List<Sound> listSound;
    private OnSoundItemClickListener listener;

    public HorizontalSoundAdapterTest(List<Sound> listSound, OnSoundItemClickListener listener) {
        this.listSound = listSound;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return listSound.size();
    }


    @Override
    public void onBindViewHolder(HorizontalSoundAdapterTest.MyViewHolder myViewHolder, int position) {

        Sound sound = listSound.get(position);
        sound.getTypeSound();
        Context context = myViewHolder.imgCate.getRootView().getContext();
        myViewHolder.txtSoundName.setText(soundName(sound.getName()));
        myViewHolder.cvBG.setBackgroundResource(BackGround.INSTANCE.getBackground(position));
        Glide.with(context).load("file:///android_asset/" + sound.getImage()).into(myViewHolder.imgCate);

        myViewHolder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSoundItemClick(myViewHolder.getAdapterPosition());
            }
            /*Intent intent = new Intent(context, SoundDetailActivity.class);
            intent.putExtra(is_fav, false);
            intent.putExtra(music_name, soundName(sound.getName()));
            intent.putExtra(cate_name, sound.getTypeSound());
            intent.putExtra(image_sound, sound.getImage());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            if (context instanceof SoundDetailActivity) {
                ((SoundDetailActivity) context).finish();
            }*/
        });
    }

    public String soundName(String sound) {
        String[] sound_name = sound.split("\\.");
        return sound_name[0];
    }


    @NotNull
    @Override
    public HorizontalSoundAdapterTest.MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new HorizontalSoundAdapterTest.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_horizontal, viewGroup, false));
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