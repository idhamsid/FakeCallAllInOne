package com.javadroid.fakecall.isConfig;


import static com.javadroid.fakecall.activity.Main.appNamme;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class ActivityConfig {

    public static File getBaseDirectory() {
        File file;
        if (Build.VERSION.SDK_INT >= 29) {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES), appNamme);
        } else {
            file = new File(Environment.getExternalStorageDirectory(), appNamme);
        }
        return file;
    }

}
