package com.wa.pranksound.adapter;

import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.ui.component.activity.SoundDetailActivity;
import com.wa.pranksound.model.Sound;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VerticalSoundAdapterTest extends RecyclerView.Adapter<VerticalSoundAdapterTest.MyViewHolder> {

    private Activity context;

    List<Sound> listSound;


    public VerticalSoundAdapterTest(Activity context, List<Sound> listSound) {
        this.context = context;
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

        myViewHolder.txtSoundName.setText(soundName(String.valueOf(sound.getName())));

       Glide.with(context).load("file:///android_asset/" + sound.getImage()).into(myViewHolder.imgCate);
       Log.d("list_image_get_image",sound.getImage());

        if(sound.isShowAds()){
            myViewHolder.imgAds.setVisibility(View.VISIBLE);
        }

       myViewHolder.itemView.setOnClickListener(v -> {
           Intent intent = new Intent(context, SoundDetailActivity.class);
           intent.putExtra(is_fav, false);
           intent.putExtra(music_name, soundName(sound.getName()));
           intent.putExtra(cate_name, sound.getTypeSound());
           intent.putExtra(image_sound, sound.getImage());
           context.startActivity(intent);
       });

    }

    public String soundName(String sound){
        String[] sound_name = sound.split("\\.");
        return sound_name[0];
    }


    @NotNull
    @Override
    public VerticalSoundAdapterTest.MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new VerticalSoundAdapterTest.MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rcv_test, viewGroup, false));
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCate,imgAds;
        TextView txtSoundName;
        RelativeLayout cvBG;



        public MyViewHolder(View view) {
            super(view);
            imgCate = view.findViewById(R.id.imgCate);
            txtSoundName = view.findViewById(R.id.txtSoundName);
            cvBG = view.findViewById(R.id.cvBG);
            imgAds = view.findViewById(R.id.img_ads);


        }
    }


}