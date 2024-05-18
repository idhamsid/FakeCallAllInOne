package com.javadroid.fakecall.keyboard;

import static android.content.ContentValues.TAG;

import static maes.tech.intentanim.CustomIntent.customType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Main;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.ads.applovin.Inter;

import java.util.List;

public class ThemeActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String THEME_KEY = "theme_key";
    public static final String AD_COUNT = "ad_count";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Integer counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        ImageButton themeButton1 = findViewById(R.id.theme1_imageButton);
        ImageButton themeButton2 = findViewById(R.id.theme2_imageButton);
        ImageButton themeButton3 = findViewById(R.id.theme3_imageButton);
        ImageButton themeButton4 = findViewById(R.id.theme4_imageButton);
        ImageButton themeButton5 = findViewById(R.id.theme5_imageButton);
        ImageButton themeButton6 = findViewById(R.id.theme6_imageButton);
        ImageButton themeButton7 = findViewById(R.id.theme7_imageButton);
        ImageButton themeButton8 = findViewById(R.id.theme8_imageButton);
        ImageButton themeButton9 = findViewById(R.id.theme9_imageButton);
        ImageButton themeButton10 = findViewById(R.id.theme10_imageButton);

        RelativeLayout banner = findViewById(R.id.banner);
        Banner.GET(getBaseContext(),banner);

        themeButton1.setOnClickListener(this);
        themeButton2.setOnClickListener(this);
        themeButton3.setOnClickListener(this);
        themeButton4.setOnClickListener(this);
        themeButton5.setOnClickListener(this);
        themeButton6.setOnClickListener(this);
        themeButton7.setOnClickListener(this);
        themeButton8.setOnClickListener(this);
        themeButton9.setOnClickListener(this);
        themeButton10.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(getActionBar() != null){
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Display the full screen Ad after third visit.
        counter = sharedPreferences.getInt(AD_COUNT, 0);
        editor = sharedPreferences.edit();
        editor.putInt(AD_COUNT, sharedPreferences.getInt(AD_COUNT, 0) + 1).apply();
        if(2 == counter) {

        } else {

        }

        findViewById(R.id.aktif).setOnClickListener(v -> {
            boolean enabled = false;
            try {
                enabled = isInputMethodOfThisImeEnabled();
            } catch (Exception e) {
                Log.e(TAG, "Exception in check if input method is enabled", e);
            }

            if (!enabled) {
                final Context context = this;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.setup_message);
                builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    dialog.dismiss();
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> dialog.dismiss());
                builder.setCancelable(false);
                builder.create().show();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.the_keyboard_is_active);
                builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                    dialog.dismiss();
                });
                builder.setCancelable(false);

                builder.create().show();
            }
        });

        findViewById(R.id.input).setOnClickListener(v -> {
            if (isInputEnabled()) {
                ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .showInputMethodPicker();
            } else {
                Toast.makeText(this, R.string.please_enable_keyboard, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Inter.SHOW(this);
        if (isInputEnabled()) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (view.getId()) {
                case R.id.theme1_imageButton:
                    editor.putInt(THEME_KEY, 0).apply();
                    break;
                case R.id.theme2_imageButton:
                    editor.putInt(THEME_KEY, 1).apply();
                    break;
                case R.id.theme3_imageButton:
                    editor.putInt(THEME_KEY, 2).apply();
                    break;
                case R.id.theme4_imageButton:
                    editor.putInt(THEME_KEY, 3).apply();
                    break;
                case R.id.theme5_imageButton:
                    editor.putInt(THEME_KEY, 4).apply();
                    break;
                case R.id.theme6_imageButton:
                    editor.putInt(THEME_KEY, 5).apply();
                    break;
                case R.id.theme7_imageButton:
                    editor.putInt(THEME_KEY, 6).apply();
                    break;
                case R.id.theme8_imageButton:
                    editor.putInt(THEME_KEY, 7).apply();
                    break;
                case R.id.theme9_imageButton:
                    editor.putInt(THEME_KEY, 8).apply();
                    break;
                case R.id.theme10_imageButton:
                    editor.putInt(THEME_KEY, 9).apply();
                    break;
                default:
                    break;
            }
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this,  R.string.please_enable_keyboard, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isInputEnabled() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

        final int N = mInputMethodProperties.size();
        boolean isInputEnabled = false;

        for (int i = 0; i < N; i++) {

            InputMethodInfo imi = mInputMethodProperties.get(i);
            Log.d("INPUT ID", String.valueOf(imi.getId()));
            if (imi.getId().contains(getPackageName())) {
                isInputEnabled = true;
            }
        }

        if (isInputEnabled) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isInputMethodOfThisImeEnabled() {
        final InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        final String imePackageName = getPackageName();
        for (final InputMethodInfo imi : imm.getEnabledInputMethodList()) {
            if (imi.getPackageName().equals(imePackageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        editor.putInt(AD_COUNT, sharedPreferences.getInt(AD_COUNT, 0)).apply();
        super.onBackPressed();
        customType(this,"fadein-to-fadeout");
    }
}