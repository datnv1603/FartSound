package com.wa.pranksound.adapter;


import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.is_record;
import static com.wa.pranksound.utils.KeyClass.music_name;
import static com.wa.pranksound.utils.KeyClass.sound_path;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.ui.component.activity.SoundDetailActivity;
import com.wa.pranksound.model.Record;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private List<Record> recordList;

    public RecordAdapter(List<Record> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.textViewName.setText(record.getName());
        holder.textViewTime.setText(record.getTime());
        Context context = holder.itemView.getContext();
        Glide.with(context).load(record.getImage()).into( holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SoundDetailActivity.class);
            intent.putExtra(is_record, true);
            intent.putExtra(is_fav, false);
            intent.putExtra(music_name, record.getName());
            intent.putExtra(image_sound, record.getImage());
            intent.putExtra(cate_name, "Record");
            intent.putExtra(sound_path, record.getFilePath());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewTime;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

}
