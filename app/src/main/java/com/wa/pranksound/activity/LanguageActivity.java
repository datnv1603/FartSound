package com.wa.pranksound.activity;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wa.pranksound.R;

import khangtran.preferenceshelper.PrefHelper;

public class LanguageActivity extends AppCompatActivity {
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        findView();
        clickEvent();
    }

    private void findView() {
        imgBack = findViewById(R.id.imgBack);

        FrameLayout fl_adplaceholder = findViewById(R.id.fl_adplaceholder);
        RelativeLayout rl_adview = findViewById(R.id.rl_adview);
       // nativeAdsLoadAndShow(this, fl_adplaceholder, rl_adview, null);

        findViewById(R.id.hindiCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "hi");
                updatingUi();
            }
        });
        findViewById(R.id.englishCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "en");
                updatingUi();
            }
        });
        findViewById(R.id.germanCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "de");
                updatingUi();
            }
        });
        findViewById(R.id.frenchCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "fr");
                updatingUi();
            }
        });
        findViewById(R.id.spanishCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "es");
                updatingUi();
            }
        });
        findViewById(R.id.purtaguesCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "pt");
                updatingUi();
            }
        });
        findViewById(R.id.italianCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefHelper.setVal("locale", "it");
                updatingUi();
            }
        });
        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LanguageActivity.this, MainActivity.class));
                finish();
            }
        });
        updatingUi();
    }

    private void clickEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void updatingUi() {
        if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("hi")) {
            findViewById(R.id.hindiCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        } else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("es")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        } else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("fr")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        }  else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("pt")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        } else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("de")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        } else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("en")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button));
        } else if (PrefHelper.getStringVal("locale", "en").equalsIgnoreCase("it")) {
            findViewById(R.id.hindiCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCheck).setVisibility(View.VISIBLE);
            findViewById(R.id.englishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.spanishCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.frenchCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.germanCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.portaguesCheck).setVisibility(View.INVISIBLE);
            findViewById(R.id.italianCard).setBackground(getDrawable(R.drawable.lang_button_press));
            findViewById(R.id.englishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.hindiCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.frenchCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.germanCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.spanishCard).setBackground(getDrawable(R.drawable.lang_button));
            findViewById(R.id.portaguesCheck).setBackground(getDrawable(R.drawable.lang_button));
        }
    }
}