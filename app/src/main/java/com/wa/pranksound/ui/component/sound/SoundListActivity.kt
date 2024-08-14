package com.wa.pranksound.ui.component.activity;

import static com.wa.pranksound.utils.KeyClass.air_horn;
import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.count_down;
import static com.wa.pranksound.utils.KeyClass.fart;
import static com.wa.pranksound.utils.KeyClass.ghost;
import static com.wa.pranksound.utils.KeyClass.gun;
import static com.wa.pranksound.utils.KeyClass.hair_clipper;
import static com.wa.pranksound.utils.KeyClass.halloween;
import static com.wa.pranksound.utils.KeyClass.snore;
import static com.wa.pranksound.utils.KeyClass.test_sound;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.wa.pranksound.R;
import com.wa.pranksound.adapter.VerticalSoundAdapterTest;
import com.wa.pranksound.databinding.AdNativeVideoBinding;
import com.wa.pranksound.model.Sound;
import com.wa.pranksound.ui.component.main.MainActivity;
import com.wa.pranksound.utils.BaseActivity;
import com.wa.pranksound.utils.ImageLoader;
import com.wa.pranksound.utils.RemoteConfigKey;
import com.wa.pranksound.utils.ads.NativeAdsUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SoundListActivity extends BaseActivity {

    String keyNative = FirebaseRemoteConfig.getInstance().getString(RemoteConfigKey.NATIVE_SOUND);

    ImageView imgBack;
    RecyclerView rvSound;

    TextView txtTitle;

    View view_line;
    RelativeLayout rlNative;
    FrameLayout frNativeAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list);

        findView();
        setUpData();
    }

    private void findView() {
        imgBack = findViewById(R.id.imgBack);
        rvSound = findViewById(R.id.rvSound);
        txtTitle = findViewById(R.id.txtTitle);

        rlNative = findViewById(R.id.rl_native);
        frNativeAds = findViewById(R.id.fr_native_ads);

        view_line = findViewById(R.id.line);

        String strCateName = getIntent().getStringExtra(cate_name);

        try {
            if (strCateName != null) {
                if (strCateName.equals(air_horn)) {
                    txtTitle.setText(getString(R.string.air_horn));
                } else if (strCateName.equals(hair_clipper)) {
                    txtTitle.setText(getString(R.string.hair_clipper));
                } else if (strCateName.equals(fart)) {
                    txtTitle.setText(getString(R.string.fart));
                } else if (strCateName.equals(count_down)) {
                    txtTitle.setText(getString(R.string.count_down));
                } else if (strCateName.equals(gun)) {
                    txtTitle.setText(getString(R.string.gun));
                } else if (strCateName.equals(ghost)) {
                    txtTitle.setText(getString(R.string.ghost));
                } else if (strCateName.equals(halloween)) {
                    txtTitle.setText(getString(R.string.halloween));
                } else if (strCateName.equals(snore)) {
                    txtTitle.setText(getString(R.string.snore));
                } else if (strCateName.equals(test_sound)) {
                    txtTitle.setText(R.string.test_sound);
                }

                // lấy hình ảnh từ file asset
                String[] images = getAssets().list("prank_sound/" + strCateName);
                List<Sound> listSound = new ArrayList<>();
                assert images != null;

                //list đường dẫn đến hình ảnh ở list asset
                List<String> imageList = ImageLoader.getImageListFromAssets(this, "prank_image/" + strCateName);

                for (int i = 0; i < images.length; i++) {
                    Sound sound;
                    if (i >= 10) {
                        sound = new Sound(strCateName, imageList.get(i), images[i], 0, true, false);
                    } else {
                        sound = new Sound(strCateName, imageList.get(i), images[i], 0, false, false);
                    }
                    listSound.add(sound);

                }
                VerticalSoundAdapterTest verticalSoundAdapter = new VerticalSoundAdapterTest(listSound);
                rvSound.setAdapter(verticalSoundAdapter);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        imgBack.setOnClickListener(v ->
                startActivity(new Intent(SoundListActivity.this, MainActivity.class))
        );

        imgBack.setOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setUpData() {
        loadNativeAd();
    }

    private void loadNativeAd() {
        if (FirebaseRemoteConfig.getInstance().getBoolean(RemoteConfigKey.IS_SHOW_ADS_NATIVE_SOUND)) {
            loadNativeAds(keyNative);
        } else {
            rlNative.setVisibility(View.GONE);
        }
    }

    private void loadNativeAds(String keyAds) {
        NativeAdsUtils.Companion.getInstance().loadNativeAds(
                getApplicationContext(),
                keyAds,
                nativeAds -> {
                    if (nativeAds != null) {
                        AdNativeVideoBinding adNativeVideoBinding = AdNativeVideoBinding.inflate(getLayoutInflater());
                        NativeAdsUtils.Companion.getInstance().populateNativeAdVideoView(
                                nativeAds,
                                (NativeAdView) adNativeVideoBinding.getRoot(),
                                true
                        );
                        frNativeAds.removeAllViews();
                        frNativeAds.addView(adNativeVideoBinding.getRoot());
                    }
                    return null;
                }
        );
    }

}