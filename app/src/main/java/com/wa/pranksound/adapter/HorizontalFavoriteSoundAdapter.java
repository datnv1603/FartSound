package com.wa.pranksound.adapter;


import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.wa.pranksound.R;
import com.wa.pranksound.activity.SoundDetailActivity;
import com.wa.pranksound.utils.AdsUtils;
import com.wa.pranksound.Room.InsertPrankSound;

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


    @RequiresApi(api = 16)
    public void onBindViewHolder(MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        String strCateName = arrFavPrankSound.get(i).folder_name;

//        if (strCateName.equals(air_horn_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.air_horn);
//        } else if (strCateName.equals(hair_clipper_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.hair_clipper);
//        } else if (strCateName.equals(fart_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.fart);
//        } else if (strCateName.equals(burp_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.burp);
//        } else if (strCateName.equals(toilet_flushing_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.toilet_flushing);
//        } else if (strCateName.equals(gun_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.gun);
//        } else if (strCateName.equals(breaking_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.breaking);
//        } else if (strCateName.equals(car_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.car);
//        } else if (strCateName.equals(meme_sounds)) {
//            myViewHolder.imgItem.setImageResource(R.drawable.meme);
//        }
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
//        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fullAdsLoadAndShow(context, new AdsClass.MyCallback() {
//                    @Override
//                    public void callbackCall() {
//                        Intent intent = new Intent(context, SoundDetailActivity.class);
//                        intent.putExtra(is_fav, true);
//                        intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
//                        intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
//                        context.startActivity(intent);
//                        context.finish();
//                    }
//                });
//
//            }
//        });

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AdmobManager.showInterstitialAd(context, INSTANCE.getInterAdHolder(), new AdmobManager.ShowAdCallBack() {
//                    @Override
//                    public void onAdShowed() {
//
//                    }
//
//                    @Override
//                    public void onAdFailed(@NonNull String s) {
//
//                    }
//
//                    @Override
//                    public void onAdClosed() {
//                        Intent intent = new Intent(context, SoundDetailActivity.class);
//                        intent.putExtra(is_fav, true);
//                        intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
//                        intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
//                        context.startActivity(intent);
//                        context.finish();
//
//                    }
//
//                    @Override
//                    public void onAdPaid(@NonNull AdValue adValue, @NonNull String s) {
//
//                    }
//                });

                AdsUtils.INSTANCE.loadAndShowInterstitialAd(context, AdsUtils.INSTANCE.getInterAdHolder(), new AdsUtils.loadAndShow() {
                    @Override
                    public void onAdClose() {
                        Log.d("check_show_ads","show in list sound");
                        Intent intent = new Intent(context, SoundDetailActivity.class);
                        intent.putExtra(is_fav, true);
                        intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
                        intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
                        context.startActivity(intent);
                        context.finish();

                    }

                    @Override
                    public void onAdFailed() {
                        Log.d("check_show_ads","not show in list sound");
                        Intent intent = new Intent(context, SoundDetailActivity.class);
                        intent.putExtra(is_fav, true);
                        intent.putExtra(music_name, arrFavPrankSound.get(i).sound_name);
                        intent.putExtra(cate_name, arrFavPrankSound.get(i).folder_name);
                        context.startActivity(intent);
                        context.finish();

                    }
                });

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
