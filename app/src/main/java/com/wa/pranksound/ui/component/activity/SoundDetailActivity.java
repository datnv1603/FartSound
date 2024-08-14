package com.wa.pranksound.ui.component.activity;

import static com.wa.pranksound.common.Constant.KEY_IS_FIRST_OPEN_SOUND;
import static com.wa.pranksound.utils.KeyClass.cate_name;
import static com.wa.pranksound.utils.KeyClass.image_sound;
import static com.wa.pranksound.utils.KeyClass.is_fav;
import static com.wa.pranksound.utils.KeyClass.is_record;
import static com.wa.pranksound.utils.KeyClass.music_name;
import static com.wa.pranksound.utils.KeyClass.sound_path;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
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
import com.wa.pranksound.adapter.HorizontalFavoriteSoundAdapter;
import com.wa.pranksound.adapter.HorizontalSoundAdapterTest;
import com.wa.pranksound.data.SharedPreferenceHelper;
import com.wa.pranksound.model.Record;
import com.wa.pranksound.model.Sound;
import com.wa.pranksound.room.AppDatabase;
import com.wa.pranksound.room.InsertPrankSound;
import com.wa.pranksound.room.QueryClass;
import com.wa.pranksound.ui.component.main.MainActivity;
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
    ImageButton swLoop;
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
    LinearLayout llBtnOff;
    LinearLayout llMoreSound;
    FrameLayout fl_banner;
    View view_line;
    LottieAnimationView animGuide;
    View viewBlur;

    ImageButton btnVolumeLoud, btnVolumeSmall, btnDelete;
    SeekBar volume;
    private AudioManager audioManager;
    Boolean isRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_detail);
        AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, "prank_sound").allowMainThreadQueries().fallbackToDestructiveMigration().build();
        queryClass = db.queryClass();


        findView();
        getData();
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
                volume.setProgress(maxVolume / 5)
        );
        btnVolumeLoud.setOnClickListener(v ->
                volume.setProgress(4 * maxVolume / 5)
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

    private void getData() {
        Intent intent = getIntent();
        soundName = intent.getStringExtra("sound_name");

        systemService = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        strMusicName = getIntent().getStringExtra(music_name);

        isFav = getIntent().getBooleanExtra(is_fav, false);
        strCateName = getIntent().getStringExtra(cate_name);

        //get music name from sound list
        string_img_sound = getIntent().getStringExtra(image_sound);
        int_img_sound = getIntent().getIntExtra(image_sound, 0);
        isRecord = getIntent().getBooleanExtra(is_record, false);

        soundPath = getIntent().getStringExtra(sound_path);

        setUpSound();

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
                String imageName = Utils.INSTANCE.removeAfterDot(images[i]);
                sound = new Sound(strCateName, imageList.get(i), imageName, 0, true, false);
                listSound.add(sound);
            }
            HorizontalSoundAdapterTest verticalSoundAdapter = new HorizontalSoundAdapterTest(listSound, position -> {
                Sound sound = listSound.get(position);
                getData(sound);
            });
            rvSound.setAdapter(verticalSoundAdapter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void getData(Sound sound) {
        if (sound != null) {
            soundName = sound.getName();
            strMusicName = sound.getName();
            strCateName = sound.getTypeSound();
            string_img_sound = sound.getImage();
            int_img_sound = 0;
            isRecord = false;
            soundPath = null;
            setUpSound();
        }
    }

    private void setUpSound() {

        InsertPrankSound insertPrankSound1 = queryClass.getFavSound(strCateName, strMusicName);
        if (isRecord) {
            llMoreSound.setVisibility(View.GONE);
            btnDelete.setVisibility(View.VISIBLE);
            imgHeart.setVisibility(View.GONE);

        } else {
            btnDelete.setVisibility(View.GONE);
            imgHeart.setVisibility(View.VISIBLE);
            imgHeart.setSelected(insertPrankSound1 != null);
        }


        if (strMusicName != null) {
            txtTitle.setText(strMusicName);
        }

        if (strCateName != null) {

            //load ảnh từ file asset
            if (int_img_sound != 0) {
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), int_img_sound);
                Glide.with(this).load(int_img_sound).into(imgItem);
            } else {
                if (!string_img_sound.contains("png")) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(string_img_sound));
                    Glide.with(this).load(bitmap).into(imgItem);
                } else {
                    Glide.with(this).load("file:///android_asset/" + string_img_sound).into(imgItem);
                }
            }

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = new MediaPlayer();
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
                        //set anim
                        imgAnimation.setVisibility(View.INVISIBLE);
                        stopVibrate();
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
        }
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
        volume = findViewById(R.id.volumeSeekBar);
        btnVolumeLoud = findViewById(R.id.btnVolumeLoud);
        btnVolumeSmall = findViewById(R.id.btnVolumeSmall);
        btnDelete = findViewById(R.id.btnDelete);

        imgAnimation = findViewById(R.id.animation);
        viewBlur = findViewById(R.id.viewBlur);

        view_line = findViewById(R.id.line);
        llBtnOff = findViewById(R.id.llBtnOff);
        llMoreSound = findViewById(R.id.llMoreSounds);
        animGuide = findViewById(R.id.animGuide);
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
                @SuppressLint("DefaultLocale") String format = String.format("%02d:%02d", Arrays.copyOf(new Object[]{j4, j5}, 2));
                txtCountTime.setText(format);
            }

            @Override
            public void onFinish() {
                tvOff.setText(R.string._off);
                txtCountTime.setText("");
                isPlaying = true;
                imgAnimation.setVisibility(View.VISIBLE);
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(false);
                        startVibrate();
                        handler.postDelayed(runnable, 5);
                    }
                }
            }
        }.start();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void clickEvent() {
        boolean isFirsTime = SharedPreferenceHelper.Companion.getBoolean(KEY_IS_FIRST_OPEN_SOUND, true);
        if (isFirsTime) {
            animGuide.setVisibility(View.VISIBLE);
            viewBlur.setVisibility(View.VISIBLE);
        } else {
            animGuide.setVisibility(View.GONE);
            viewBlur.setVisibility(View.GONE);
        }

        animGuide.setOnTouchListener((v, event) -> {
            SharedPreferenceHelper.Companion.storeBoolean(KEY_IS_FIRST_OPEN_SOUND, false);
            animGuide.setVisibility(View.GONE);
            viewBlur.setVisibility(View.GONE);
            return true;
        });

        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

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
        imgItem.setOnTouchListener((v, event) -> switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN -> {
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
                yield true;
            }
            case MotionEvent.ACTION_UP -> {
                if (!isLoop) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            stopVibrate();
                            imgAnimation.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                yield false;
            }
            default -> false;
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

        swLoop.setOnClickListener(v -> {
            swLoop.setSelected(!swLoop.isSelected());
            isLoop = swLoop.isSelected();
        });

        btnDelete.setOnClickListener(v -> {
            showDiscardDialog(() -> {
                deleteRecord(strMusicName);
                startActivity(new Intent(SoundDetailActivity.this, MainActivity.class));
            });
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
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                stopVibrate();
                imgAnimation.setVisibility(View.INVISIBLE);
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
                    systemService.vibrate(mediaPlayer.getDuration());
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

    public void deleteRecord(String fileName) {
        List<Record> audioFileList = Utils.INSTANCE.getAudioList(this);
        for (int i = 0; i < audioFileList.size(); i++) {
            if (audioFileList.get(i).getName().equals(fileName)) {
                audioFileList.remove(i);
                break;
            }
        }
        Utils.INSTANCE.saveAudioList(this, audioFileList);
    }

    private void showDiscardDialog(Runnable callback) {
        Dialog dialogCustomExit = new Dialog(SoundDetailActivity.this);
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogCustomExit.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogCustomExit.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialogCustomExit.setContentView(R.layout.dialog_delete_record);
        dialogCustomExit.setCancelable(true);
        dialogCustomExit.show();
        dialogCustomExit.getWindow().setAttributes(lp);

        TextView btnNegative = dialogCustomExit.findViewById(R.id.btnNegative);
        TextView btnPositive = dialogCustomExit.findViewById(R.id.btnPositive);

        btnPositive.setOnClickListener(v -> {
            dialogCustomExit.dismiss();
            callback.run();
        });

        btnNegative.setOnClickListener(v -> dialogCustomExit.dismiss());
    }
}