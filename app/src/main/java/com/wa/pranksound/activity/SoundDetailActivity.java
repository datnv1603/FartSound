package com.wa.pranksound.activity;

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
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.wa.pranksound.R;
import com.wa.pranksound.adapter.CategoriesAdapter;
import com.wa.pranksound.adapter.HorizontalFavoriteSoundAdapter;
import com.wa.pranksound.Room.AppDatabase;
import com.wa.pranksound.Room.InsertPrankSound;
import com.wa.pranksound.Room.QueryClass;
import com.wa.pranksound.utils.BaseActivity;
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
    Switch swLoop;
    RecyclerView rvSound;
    QueryClass queryClass;
    String strCateName, musicName;
    List<InsertPrankSound> arrFavPrankSound = new ArrayList<>();
    HorizontalFavoriteSoundAdapter horizontalSoundAdapter;
    Boolean isFav = false;
    LinearLayout llBtnOff;
    CountDownTimer countDownTimer;
    List<String> cate;
    String[] images;
    String string_img_sound;
    Integer int_img_sound;
    String soundName;
    String soundPath;

    FrameLayout fl_banner;
    View view_line;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detail);
        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "prank_sound").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        queryClass = db.queryClass();

        findView();
        clickEvent();
    }

    private void findView() {
        imgBack = findViewById(R.id.imgBack);
        imgItem = findViewById(R.id.imgItem);
        txtTitle = findViewById(R.id.txtTitle);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        endTime = findViewById(R.id.endTime);
        seekBar = findViewById(R.id.seekBar);
        swLoop = findViewById(R.id.swLoop);
        rvSound = findViewById(R.id.rvSound);
        imgHeart = findViewById(R.id.imgHeart);
        llBtnOff = findViewById(R.id.llBtnOff);
        txtCountTime = findViewById(R.id.txtCountTime);
        tvOff = findViewById(R.id.tvOff);

        imgAnimation = findViewById(R.id.animation);

        view_line = findViewById(R.id.line);

        systemService = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        strMusicName = getIntent().getStringExtra(music_name);

        isFav = getIntent().getBooleanExtra(is_fav, false);
        strCateName = getIntent().getStringExtra(cate_name);

        //get music name from sound list
        musicName = getIntent().getStringExtra(music_name);
        string_img_sound = getIntent().getStringExtra(image_sound);
        int_img_sound = getIntent().getIntExtra(image_sound, 0);

        soundPath = getIntent().getStringExtra(sound_path);

        Log.d("check_file", "sound path: " + soundPath);
        Log.d("check_file", "image_sound_in_detail: " + int_img_sound);
        Log.d("check_file", "image_sound_from_fav: " + string_img_sound);
        Log.d("check_file", "music name: " + musicName);

        InsertPrankSound insertPrankSound1 = queryClass.getFavSound(strCateName, strMusicName);
        if (insertPrankSound1 != null) {
            imgHeart.setImageResource(R.drawable.ic_favorite_check);
        } else {
            imgHeart.setImageResource(R.drawable.ic_heart);
        }

        if (strMusicName != null) {
            txtTitle.setText(musicName);
        }
        Log.d("check_file", "str cate name: " + strCateName);
        if (strCateName != null) {
            try {
                cate = Arrays.asList(Objects.requireNonNull(getAssets().list("prank_sound")));
                images = getAssets().list("prank_sound/" + strCateName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, cate);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            rvSound.setLayoutManager(layoutManager);
            rvSound.setAdapter(categoriesAdapter);

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
                Log.d("check_file", "str cate name in sound: " + strCateName);
                if (strCateName.equals("record")) {
                    soundPath = musicName;
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
                    Log.d("check_file", "des path: " + "prank_sound/" + strCateName + "/" + strMusicName + ".ogg");
                    Log.d("check_file", "descriptor: " + descriptor);
                    mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();

                }
                mediaPlayer.prepare();
                Log.d("check_file", "str cate name outttt: " + strCateName);
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
        imgBack.setOnClickListener(v -> {
            Log.d("check_show_ads", "show in back sound list");
            getOnBackPressedDispatcher().onBackPressed();
        });
        llBtnOff.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(SoundDetailActivity.this, R.style.myPopupMenu), v);
            popupMenu.getMenuInflater().inflate(R.menu.set_time, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.five) {
                    startCountDownTimer(5000, txtCountTime);
                    tvOff.setText(R.string._5s);
                    return true;
                } else if (item.getItemId() == R.id.fiveMinute) {
                    startCountDownTimer(300000, txtCountTime);
                    tvOff.setText(R.string._5m);
                    return true;
                } else if (item.getItemId() == R.id.off) {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    imgPlayPause.setEnabled(true);
                    txtCountTime.setText("");
                    tvOff.setText(R.string._off);
                    return true;
                } else if (item.getItemId() == R.id.oneMinute) {
                    startCountDownTimer(60000, txtCountTime);
                    tvOff.setText(R.string._1m);
                    return true;
                } else if (item.getItemId() == R.id.ten) {
                    startCountDownTimer(10000, txtCountTime);
                    tvOff.setText(R.string._10s);
                    return true;
                } else if (item.getItemId() == R.id.thirty) {
                    startCountDownTimer(30000, txtCountTime);
                    tvOff.setText(R.string._30s);
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

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

        imgPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                isPlaying = false;
                imgPlayPause.setImageResource(R.drawable.play);
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        stopVibrate();
                    }
                }
            } else {
                isPlaying = true;
                imgPlayPause.setImageResource(R.drawable.pause);
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        startVibrate();
                        handler.postDelayed(runnable, 5);
                    }

                }

            }

        });


        //test favories
        imgHeart.setOnClickListener(v -> {
            InsertPrankSound insertPrankSound1 = queryClass.getFavSound(strCateName, strMusicName);
            if (insertPrankSound1 != null) {
                queryClass.getUnFavSound(strCateName, strMusicName);
                imgHeart.setImageResource(R.drawable.ic_heart);
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
                imgHeart.setImageResource(R.drawable.ic_favorite_check);
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

    private void startCountDownTimer(int i, TextView txtCountTime) {
        imgPlayPause.setEnabled(false);
        imgAnimation.setVisibility(View.INVISIBLE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(i, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long j2 = millisUntilFinished / ((long) 1000);
                long j3 = (long) 60;
                long j4 = j2 / j3;
                long j5 = j2 % j3;
                String format = String.format("%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(j4), Long.valueOf(j5)}, 2));
                txtCountTime.setText(format);
            }

            @Override
            public void onFinish() {
                tvOff.setText(R.string._off);
                txtCountTime.setText("");
                imgPlayPause.setEnabled(true);
                isPlaying = true;
                imgPlayPause.setImageResource(R.drawable.pause);
                imgAnimation.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        startVibrate();
                        handler.postDelayed(runnable, 5);
                    }
                }
            }
        }.start();

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