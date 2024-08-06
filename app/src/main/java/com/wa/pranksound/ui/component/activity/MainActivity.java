package com.wa.pranksound.ui.component.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wa.pranksound.R;
import com.wa.pranksound.ui.component.fragment.CreateFragment;
import com.wa.pranksound.ui.component.fragment.FavoritesFragment;
import com.wa.pranksound.ui.component.fragment.HomeFragment;
import com.wa.pranksound.ui.component.fragment.LeaderBoardFragment;
import com.wa.pranksound.ui.component.fragment.SettingFragment;
import com.wa.pranksound.utils.BaseActivity;
import com.wa.pranksound.utils.Gdpr;

import java.util.Objects;

public class MainActivity extends BaseActivity {
    ImageView imgHome, imgCreate, imgFavorites, imgLeaderboard, imgSetting;
    RecyclerView rcv_cate;
    FragmentManager fragmentManager;

    LinearLayout ll_home, ll_create, ll_favorites, ll_leaderboard, ll_setting;
    String navTab = "home";
    TextView tv_title, tv_home, tv_create, tv_favorites, tv_leaderboard, tv_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();

        fragmentManager = getSupportFragmentManager();
        bottomNavigationClick();
        String get_record = getIntent().getStringExtra("from_record");

        if (get_record != null) {
            changeFragment("create");
        } else {
            changeFragment("home");
        }


    }

    private void changeFragment(String tab) {
        switch (tab) {
            case "home":
                fragmentManager.beginTransaction().replace(R.id.fragmentMain, new HomeFragment()).commit();
                tv_title.setText(R.string.home);

                imgHome.setBackgroundResource(R.drawable.ic_home_selected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color_selected));

                imgCreate.setBackgroundResource(R.drawable.ic_create_unselected);
                tv_create.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected);
                tv_favorites.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected);
                tv_leaderboard.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgSetting.setBackgroundResource(R.drawable.ic_setting);
                tv_setting.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));


                break;

            case "create":
                fragmentManager.beginTransaction().replace(R.id.fragmentMain, new CreateFragment()).commit();
                tv_title.setText(R.string.create_sound);

                imgHome.setBackgroundResource(R.drawable.ic_home_unselected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgCreate.setBackgroundResource(R.drawable.ic_create_selected);
                tv_create.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color_selected));

                imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected);
                tv_favorites.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected);
                tv_leaderboard.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgSetting.setBackgroundResource(R.drawable.ic_setting);
                tv_setting.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                break;

            case "favorites":
                fragmentManager.beginTransaction().replace(R.id.fragmentMain, new FavoritesFragment()).commit();
                tv_title.setText(R.string.favorites);

                imgHome.setBackgroundResource(R.drawable.ic_home_unselected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgCreate.setBackgroundResource(R.drawable.ic_create_unselected);
                tv_create.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgFavorites.setBackgroundResource(R.drawable.ic_fav_selected);
                tv_favorites.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color_selected));

                imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected);
                tv_leaderboard.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgSetting.setBackgroundResource(R.drawable.ic_setting);
                tv_setting.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                break;

            case "leaderboard":
                fragmentManager.beginTransaction().replace(R.id.fragmentMain, new LeaderBoardFragment()).commit();
                tv_title.setText(R.string.leaderboard);

                imgHome.setBackgroundResource(R.drawable.ic_home_unselected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgCreate.setBackgroundResource(R.drawable.ic_create_unselected);
                tv_create.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected);
                tv_favorites.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_selected);
                tv_leaderboard.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color_selected));

                imgSetting.setBackgroundResource(R.drawable.ic_setting);
                tv_setting.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                break;

            case "setting":
                fragmentManager.beginTransaction().replace(R.id.fragmentMain, new SettingFragment()).commit();
                tv_title.setText(R.string.setting);

                imgHome.setBackgroundResource(R.drawable.ic_home_unselected);
                tv_home.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgCreate.setBackgroundResource(R.drawable.ic_create_unselected);
                tv_create.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected);
                tv_favorites.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected);
                tv_leaderboard.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color));

                imgSetting.setBackgroundResource(R.drawable.ic_setting_selected);
                tv_setting.setTextColor(ContextCompat.getColor(this, R.color.bottom_nav_text_color_selected));

                break;

        }
    }

    private void bottomNavigationClick() {
        ll_home.setOnClickListener(v -> {
            navTab = "home";
            changeFragment(navTab);
        });

        ll_create.setOnClickListener(v -> {
            navTab = "create";
            changeFragment(navTab);
        });

        ll_favorites.setOnClickListener(v -> {
            navTab = "favorites";
            changeFragment(navTab);
        });

        ll_leaderboard.setOnClickListener(v -> {
            navTab = "leaderboard";
            changeFragment(navTab);
        });

        ll_setting.setOnClickListener(v -> {
            navTab = "setting";
            changeFragment(navTab);
        });

    }

    private void findView() {

        rcv_cate = findViewById(R.id.rcv_cate);

        ll_home = findViewById(R.id.ll_home);
        ll_create = findViewById(R.id.ll_create);
        ll_favorites = findViewById(R.id.ll_favorites);
        ll_leaderboard = findViewById(R.id.ll_leaderboard);
        ll_setting = findViewById(R.id.ll_setting);

        tv_title = findViewById(R.id.tv_title);

        imgHome = findViewById(R.id.img_home);
        imgCreate = findViewById(R.id.img_create);
        imgFavorites = findViewById(R.id.img_favorites);
        imgLeaderboard = findViewById(R.id.img_leaderboard);
        imgSetting = findViewById(R.id.img_setting);

        tv_home = findViewById(R.id.tv_home);
        tv_create = findViewById(R.id.tv_create);
        tv_favorites = findViewById(R.id.tv_favorites);
        tv_leaderboard = findViewById(R.id.tv_leaderboard);
        tv_setting = findViewById(R.id.tv_setting);

        new Gdpr().make(this);


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    public void showExitDialog() {
        Dialog dialogCustomExit = new Dialog(MainActivity.this);
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogCustomExit.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCustomExit.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogCustomExit.setContentView(R.layout.exit_dialog_layout);
        dialogCustomExit.setCancelable(true);
        dialogCustomExit.show();
        dialogCustomExit.getWindow().setAttributes(lp);

        TextView btnNegative = dialogCustomExit.findViewById(R.id.btnNegative);
        TextView btnPositive = dialogCustomExit.findViewById(R.id.btnPositive);

        btnPositive.setOnClickListener(v -> {
            dialogCustomExit.dismiss();
            finishAffinity();

        });
        btnNegative.setOnClickListener(v -> dialogCustomExit.dismiss());
    }
}