package com.joya.pranksound.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

import khangtran.preferenceshelper.PrefHelper;

public class Common {

    public static void setLocale(Context context) {
        Locale locale = new Locale(PrefHelper.getStringVal("locale", "en"));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }




}
