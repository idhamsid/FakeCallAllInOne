package com.javadroid.fakecall;


import android.app.Application;
import android.content.res.Configuration;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.javadroid.fakecall.manager.LanguageManager;

import java.util.Locale;

public class MyApplication extends Application {
    public static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        boolean isLanguage = LanguageManager.isLanguage(this);
        String language = LanguageManager.getLanguage(this);
        if (!language.isEmpty()){
            setLocale(language);
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }



}
