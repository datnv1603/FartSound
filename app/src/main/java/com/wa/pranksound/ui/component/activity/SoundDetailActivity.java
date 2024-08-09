package com.wa.pranksound.ui.component.activity;

import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.music_name;
import static com.wa.pranksound.utils.KeyClass.sound_path;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.adapter.CategoriesAdapter;
import com.wa.pranksound.adapter.HorizontalFavoriteSoundAdapter;
import com.wa.pranksound.adapter.HorizontalSoundAdapterTest;
import com.wa.pranksound.adapter.VerticalSoundAdapterTest;
import com.wa.pranksound.model.Sound;
import com.wa.pranksound.room.AppDatabase;
import com.wa.pranksound.room.InsertPrankSound;
import com.wa.pranksound.room.QueryClass;
import com.wa.pranksound.utils.BaseActivity;
import com.wa.pranksound.utils.ImageLoader;
import com.wa.pranksound.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SoundDetailActivity extends BaseActivity {
    ImageView imgBack, imgItem, imgPlayPause, imgHeart;
    LottieAnimationView imgAnimation;

    Boolean isPlaying = false, isLoop = false;
    String strMusicName;
    TextView txtTitle, endTime, txtCountTime, tvOff;
    Vibrator systemService;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ProgressBar seekBar;
    Handler handler = new Handler();
    Runnable runnable;
    CheckBox swLoop;
    RecyclerView rvSound;
    QueryClass queryClass;
    String strCateName;
    List<InsertPrankSound> arrFavPrankSound = new ArrayList<>();
    HorizontalFavoriteSoundAdapter horizontalSoundAdapter;
    Boolean isFav = false;
    CountDownTimer countDownTimer;
    List<String> cate;
    String[] images;
    String string_img_sound;
    Integer int_img_sound;
    String soundName;
    String soundPath;

    FrameLayout fl_banner;
    View view_line;

    ImageButton btnVolumeLoud, btnVolumeSmall;
    SeekBar volume;
    private AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detail);
        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "prank_sound").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        queryClass = db.queryClass();

        findView();
        clickEvent();

        setUpVolume();
    }

    private void setUpVolume() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volume.setMax(maxVolume);
        volume.setProgress(currentVolume);
        btnVolumeSmall.setOnClickListener(v ->
                volume.setProgress(maxVolume/5)
        );
        btnVolumeLoud.setOnClickListener(v ->
                volume.setProgress(4 * maxVolume/5)
        );
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void findView() {
        imgBack = findViewById(R.id.btnBack);
        imgItem = findViewById(R.id.imgItem);
        txtTitle = findViewById(R.id.tvTitle);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        endTime = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekBar);
        swLoop = findViewById(R.id.swLoop);
        rvSound = findViewById(R.id.rvSound);
        imgHeart = findViewById(R.id.imgHeart);
        txtCountTime = findViewById(R.id.txtCountTime);
        tvOff = findViewById(R.id.tvOff);
        volume = findViewById(R.id.seekBarVolume);
        btnVolumeLoud = findViewById(R.id.btnVolumeLoud);
        btnVolumeSmall = findViewById(R.id.btnVolumeSmall);

        imgAnimation = findViewById(R.id.animation);

        view_line = findViewById(R.id.line);

        systemService = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        strMusicName = getIntent().getStringExtra(music_name);

        isFav = getIntent().getBooleanExtra(is_fav, false);
        strCateName = getIntent().getStringExtra(cate_name);

        //get music name from sound list
        string_img_sound = getIntent().getStringExtra(image_sound);
        int_img_sound = getIntent().getIntExtra(image_sound, 0);

        soundPath = getIntent().getStringExtra(sound_path);
        InsertPrankSound insertPrankSound1 = queryClass.getFavSound(strCateName, strMusicName);
        imgHeart.setSelected(true);
        imgHeart.setSelected(insertPrankSound1 != null);

        if (strMusicName != null) {
            txtTitle.setText(strMusicName);
        }

        if (strCateName != null) {
            try {
                cate = Arrays.asList(Objects.requireNonNull(getAssets().list("prank_sound")));
                images = getAssets().list("prank_sound/" + strCateName);

                // lấy hình ảnh từ file asset
                String[] images = getAssets().list("prank_sound/" + strCateName);
                List<Sound> listSound = new ArrayList<>();
                assert images != null;

                //list đường dẫn đến hình ảnh ở list asset
                List<String> imageList = ImageLoader.getImageListFromAssets(this, "prank_image/" + strCateName);

                for (int i = 0; i < images.length; i++) {
                    Sound sound;
                    sound = new Sound(strCateName, imageList.get(i), images[i], 0, true, false);
                    listSound.add(sound);
                }
                HorizontalSoundAdapterTest verticalSoundAdapter = new HorizontalSoundAdapterTest(listSound);
                rvSound.setAdapter(verticalSoundAdapter);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //load ảnh từ file asset
            if (int_img_sound != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), int_img_sound);
                Glide.with(this).load(bitmap).into(imgItem);
            } else {
                if (!string_img_sound.contains("png")) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(string_img_sound));
                    Glide.with(this).load(bitmap).into(imgItem);
                } else {
                    Glide.with(this).load("file:///android_asset/" + string_img_sound).into(imgItem);
                }
            }

            try {
                if (strCateName.equals("record")) {
                    soundPath = strMusicName;
                }
                //set sound from asset or record
                if (soundPath != null) {
                    try {
                        // Đặt nguồn dữ liệu cho MediaPlayer từ File
                        mediaPlayer.setDataSource(soundPath);
                    } catch (IOException ignored) {
                    }
                } else {
                    AssetFileDescriptor descriptor;
                    descriptor = getAssets().openFd("prank_sound/" + strCateName + "/" + strMusicName + ".ogg");
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                }
                mediaPlayer.prepare();
                int length = mediaPlayer.getDuration(); // lấy giá trị độ dài của sound

                String durationText = DateUtils.formatElapsedTime(length / 1000); // converting time in millis to minutes:second format eg 14:15 min
                endTime.setText(durationText);
                seekBar.setMax(length);

                runnable = () -> {
                    if (mediaPlayer != null) {
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        handler.postDelayed(runnable, 5);
                    }

                };

                mediaPlayer.setOnCompletionListener(mp -> {
                    if (!isLoop) {
                        if (mediaPlayer != null) {
                            seekBar.setProgress(mediaPlayer.getDuration());
                        }
                        isPlaying = false;
                        imgPlayPause.setImageResource(R.drawable.play);
                        //set anim
                        imgAnimation.setVisibility(View.INVISIBLE);
                    } else {
                        seekBar.setProgress(0);
                        mediaPlayer.start();
                        startVibrate();
                    }
                });

            } catch (Exception e) {
                Log.d("check_file", "error " + e);
                e.printStackTrace();
            }

            swLoop.setOnCheckedChangeListener((buttonView, isChecked) -> isLoop = isChecked);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void clickEvent() {
        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        //set playing when click item
        imgItem.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mediaPlayer != null) {
                        if (!mediaPlayer.isPlaying()) {
                            imgAnimation.setVisibility(View.VISIBLE);
                            imgAnimation.playAnimation();
                            mediaPlayer.seekTo(0);
                            mediaPlayer.setLooping(true);
                            mediaPlayer.start();
                            startVibrate();
                        } else {
                            if (isLoop) {
                                mediaPlayer.pause();
                                stopVibrate();
                                imgAnimation.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (!isLoop) {
                        if (mediaPlayer != null) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                stopVibrate();
                                imgAnimation.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                    return false;
            }
            return false;
        });

        //test favories
        imgHeart.setOnClickListener(v -> {
            InsertPrankSound insertPrankSound1 = queryClass.getFavSound(strCateName, strMusicName);
            if (insertPrankSound1 != null) {
                queryClass.getUnFavSound(strCateName, strMusicName);
                imgHeart.setSelected(false);
            } else {
                InsertPrankSound insertPrankSound = new InsertPrankSound();
                insertPrankSound.folder_name = strCateName;
                insertPrankSound.sound_name = strMusicName;

                if (int_img_sound != 0) {
                    insertPrankSound.image_path = int_img_sound.toString();
                    insertPrankSound.sound_path = soundPath;
                } else {
                    insertPrankSound.image_path = string_img_sound;
                }
                Log.d("Img_path", insertPrankSound.image_path);
                queryClass.insertPrankSound(insertPrankSound);
                imgHeart.setSelected(true);
            }
            if (isFav) {
                arrFavPrankSound = queryClass.getAllFavSound();
                if (horizontalSoundAdapter != null) {
                    Collections.reverse(arrFavPrankSound);
                    horizontalSoundAdapter.notifyDataSetChangedAd(arrFavPrankSound);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopVibrate();
                mediaPlayer.reset();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                stopVibrate();
                mediaPlayer.reset();
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;

            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
        handler.removeCallbacks(runnable);
    }

    public final void startVibrate() {
        if (Utils.INSTANCE.getVibration(this)) {
            if (systemService != null) {
                if (Build.VERSION.SDK_INT < 26 || !systemService.hasVibrator()) {
                    systemService.vibrate((long) mediaPlayer.getDuration());
                } else {
                    systemService.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000}, 0));
                }
            }
        }
    }

    public final void stopVibrate() {
        if (systemService != null) {
            systemService.cancel();
        }
    }
}