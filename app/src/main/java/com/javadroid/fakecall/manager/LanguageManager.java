package com.javadroid.fakecall.manager;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguageManager {

    private static final String PREF_NAME = "MyAppPrefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_IS_LANGUAGE = "is_language";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static void setLanguage(Context context, String string, boolean status) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(KEY_LANGUAGE, string);
        editor.putBoolean(KEY_IS_LANGUAGE, status);
        editor.apply();
    }

    public static String getLanguage(Context context) {
        return getSharedPreferences(context).getString(KEY_LANGUAGE, "");
    }

    public static boolean isLanguage(Context context) {
        return getSharedPreferences(context).getBoolean(KEY_IS_LANGUAGE, false);
    }
}

