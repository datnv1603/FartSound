package com.wa.pranksound.adapter;

import static com.wa.pranksound.utils.KeyClass.air_horn;
import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.count_down;
import static com.wa.pranksound.utils.KeyClass.fart;
import static com.wa.pranksound.utils.KeyClass.ghost;
import static com.wa.pranksound.utils.KeyClass.gun;
import static com.wa.pranksound.utils.KeyClass.hair_clipper;
import static com.wa.pranksound.utils.KeyClass.halloween;
import static com.wa.pranksound.utils.KeyClass.snore;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wa.pranksound.R;
import com.wa.pranksound.activity.SoundListActivity;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder> {
    private List<String> folderNames;
    private Activity context;

    public CategoriesAdapter(Activity context, List<String> folderNames) {
        this.context = context;
        this.folderNames = folderNames;
    }

    @NonNull
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
        return new CategoriesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        String folderName = folderNames.get(position);
        holder.bind(folderName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("check_show_ads", "show in categories");
                Intent intent = new Intent(context, SoundListActivity.class);
                intent.putExtra(cate_name, folderName);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folderNames.size();
    }

    public static class CategoriesViewHolder extends RecyclerView.ViewHolder {
        ImageView folderImage;
        TextView folderNameText, tv_new, tv_event;


        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImage = itemView.findViewById(R.id.folder_image);
            folderNameText = itemView.findViewById(R.id.folder_name);
            tv_new = itemView.findViewById(R.id.tv_new);
            tv_event = itemView.findViewById(R.id.tv_event);
        }

        public void bind(String folderName) {
            // Load image based on folderName

            if (folderName.equals(air_horn)) {
                folderImage.setImageResource(R.drawable.air_horn_test);
            } else if (folderName.equals(hair_clipper)) {
                folderImage.setImageResource(R.drawable.img_hair_clipper);
            } else if (folderName.equals(fart)) {
                folderImage.setImageResource(R.drawable.img_fart);
            } else if (folderName.equals(count_down)) {
                folderImage.setImageResource(R.drawable.img_count_down);
                //   tv_new.setVisibility(View.VISIBLE);
            } else if (folderName.equals(ghost)) {
                folderImage.setImageResource(R.drawable.img_ghost);
                // tv_new.setVisibility(View.VISIBLE);
            } else if (folderName.equals(halloween)) {
                folderImage.setImageResource(R.drawable.img_halloween);
            } else if (folderName.equals(snore)) {
                folderImage.setImageResource(R.drawable.img_snore);
                //    tv_event.setVisibility(View.VISIBLE);
            } else if (folderName.equals(gun)) {
                folderImage.setImageResource(R.drawable.img_gun);
                //     tv_event.setVisibility(View.VISIBLE);
            }
            // Set folder name
            folderName = folderName.replace("_", " ");
            folderNameText.setText(folderName);
        }
    }

}

