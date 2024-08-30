package com.wa.pranksound.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class Gdpr {
    private String TAG = "Gradle";
    private Boolean under_age = false;
    private ConsentInformation consentInformation;
    private Activity activity;


    public void make(Activity activity){
        // Here is GDPR  :
        //================
        this.activity = activity;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.activity);

        if (sharedPreferences.getBoolean("already_viewed_gdpr", false)){
            return;
        }

        // Set tag for underage of consent. false means users are not underage.
        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> {
                    // The consent information state was updated.
                    // You are now ready to check if a form is available.
                    if (consentInformation.isConsentFormAvailable()) {
                        loadForm();
                    }
                },
                formError -> {
                    // Handle the error.
                });

    }

    public void loadForm() {
        UserMessagingPlatform.loadConsentForm(
                activity,
                consentForm -> {
                    if(consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
                        consentForm.show(
                                activity,
                                formError -> {
                                    // Handle dismissal by reloading form.
                                    loadForm();
                                });

                        SharedPreferences.Editor sharedPreferencesEditor =
                                PreferenceManager.getDefaultSharedPreferences(activity).edit();
                        sharedPreferencesEditor.putBoolean("already_viewed_gdpr", true);
                        sharedPreferencesEditor.apply();

                    }

                },
                formError -> {
                    // Handle the error
                }
        );
    }

}
