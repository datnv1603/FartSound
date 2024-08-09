package com.wa.pranksound.ui.component.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wa.pranksound.R;
import com.wa.pranksound.adapter.VerticalFavoriteSoundAdapter;
import com.wa.pranksound.room.AppDatabase;
import com.wa.pranksound.room.InsertPrankSound;
import com.wa.pranksound.room.QueryClass;

import java.util.Collections;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {
    ImageView imgBack;
    QueryClass queryClass;
    List<InsertPrankSound> arrFavPrankSound;
    RecyclerView rvSound;
    TextView txtNoFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        AppDatabase db = Room.databaseBuilder(this,
                AppDatabase.class, "prank_sound").allowMainThreadQueries().fallbackToDestructiveMigration().build();

        queryClass = db.queryClass();

        findView();
        clickEvent();
    }

    private void findView() {
        imgBack = findViewById(R.id.imgBack);
        rvSound = findViewById(R.id.rvSound);
        txtNoFound = findViewById(R.id.txtNoFound);
    }

    private void clickEvent() {
        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        arrFavPrankSound = queryClass.getAllFavSound();
        if (!arrFavPrankSound.isEmpty()) {
            Collections.reverse(arrFavPrankSound);
            txtNoFound.setVisibility(View.GONE);
            rvSound.setVisibility(View.VISIBLE);
            VerticalFavoriteSoundAdapter verticalSoundAdapter = new VerticalFavoriteSoundAdapter(arrFavPrankSound);
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            rvSound.setLayoutManager(layoutManager);
            rvSound.setAdapter(verticalSoundAdapter);
        } else {
            rvSound.setVisibility(View.GONE);
            txtNoFound.setVisibility(View.VISIBLE);
        }

    }
}