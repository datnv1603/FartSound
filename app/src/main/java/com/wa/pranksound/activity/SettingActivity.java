package com.wa.pranksound.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wa.pranksound.R;
import com.wa.pranksound.utils.BaseActivity;

import java.util.List;

public class SettingActivity extends BaseActivity {
    ImageView imgBack;
    LinearLayout llLanguage, llShare, llRate, llPrivacyPolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findView();
        clickEvent();
    }

    private void findView() {
        imgBack = findViewById(R.id.imgBack);
        llLanguage = findViewById(R.id.llLanguage);
        llShare = findViewById(R.id.llShare);
        llRate = findViewById(R.id.llRate);
        llPrivacyPolicy = findViewById(R.id.llPrivacyPolicy);
    }

    private void clickEvent() {
        imgBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        llLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, LanguageActivity.class);
            startActivity(intent);
        });

        llShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + "\nTry it Now " + "https://play.google.com/store/apps/details?id=" + getPackageName());
            shareIntent.setType("text/plain");
            if (isAvailable(shareIntent, SettingActivity.this)) {
                startActivity(shareIntent);
            } else {
                Toast.makeText(SettingActivity.this, "There is no app availalbe for this task", Toast.LENGTH_SHORT).show();
            }
        });

        llRate.setOnClickListener(v -> {
            try {
                String sb2 = "http://play.google.com/store/apps/details?id=" +
                        getPackageName();
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sb2)));

            } catch (ActivityNotFoundException unused) {
                unused.printStackTrace();
            }
        });
        llPrivacyPolicy.setOnClickListener(v -> {
            /*try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_link)));
                startActivity(browserIntent);
            } catch (ActivityNotFoundException unused) {
                unused.printStackTrace();
            }*/
        });
    }

    public boolean isAvailable(Intent intent, Context context) {
        final PackageManager mgr = context.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }
}