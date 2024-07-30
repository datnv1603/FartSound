package com.wa.pranksound.adapter;



import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;
import static com.wa.pranksound.utils.KeyClass.sound_path;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.activity.SoundDetailActivity;
import com.wa.pranksound.utils.AdsUtils;
import com.wa.pranksound.Room.InsertPrankSound;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class VerticalFavoriteSoundAdapter extends Adapter<VerticalFavoriteSoundAdapter.MyViewHolder> {

    private Activity context;
    List<InsertPrankSound> arrFavPrankSound;


    public VerticalFavoriteSoundAdapter(Activity context, List<InsertPrankSound> arrFavPrankSound) {
        this.context = context;
        this.arrFavPrankSound = arrFavPrankSound;

    }

    @Override
    public int getItemCount() {
        return arrFavPrankSound.size();
    }


    public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {


        String strCateName = arrFavPrankSound.get(i).folder_name;
        String imagePath = arrFavPrankSound.get(i).image_path;
        Log.d("Img Path", imagePath);
        if(!imagePath.contains("png")){
            int resourceId = Integer.parseInt(imagePath);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId);
            Glide.with(context).load(bitmap).into(myViewHolder.imgCate);
            Log.d("Img Path", "drawable");
        }else {
            Glide.with(context).load("file:///android_asset/" + imagePath).into( myViewHolder.imgCate);
            Log.d("Img Path", "asset");
        }

        myViewHolder.cvBG.setBackgroundResource(R.drawable.bg_sound);

        myViewHolder.txtSoundName.setText(arrFavPrankSound.get(i).sound_name.replace("Sound",context.getString(R.string.sound)));
        myViewHolder.itemView.setOnClickListener(v -> AdsUtils.INSTANCE.loadAndShowInterstitialAd(context, AdsUtils.INSTANCE.getInterAdHolder(), new AdsUtils.loadAndShow() {
            @Override
            public void onAdClose() {
                Log.d("check_show_ads","show in verticalFav");
                Intent intent = new Intent(context, SoundDetailActivity.class);
                intent.putExtra(is_fav, true);
                intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
                intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
                intent.putExtra(image_sound, arrFavPrankSound.get(i).image_path);
                intent.putExtra(sound_path, arrFavPrankSound.get(i).sound_path);
                context.startActivity(intent);
            }

            @Override
            public void onAdFailed() {
                Log.d("check_show_ads","not show in verticalFav");
                Intent intent = new Intent(context, SoundDetailActivity.class);
                intent.putExtra(is_fav, true);
                intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
                intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
                intent.putExtra(image_sound, arrFavPrankSound.get(i).image_path);
                intent.putExtra(sound_path, arrFavPrankSound.get(i).sound_path);
                context.startActivity(intent);
            }
        }));

    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {

        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item_rcv, viewGroup, false));
    }

    public static class MyViewHolder extends ViewHolder {
        ImageView imgCate;
        TextView txtSoundName;
        RelativeLayout cvBG;

        public MyViewHolder(View view) {
            super(view);
            imgCate = view.findViewById(R.id.imgCate);
            txtSoundName = view.findViewById(R.id.txtSoundName);
            cvBG = view.findViewById(R.id.cvBG);


        }
    }


}
