package com.javadroid.fakecall.activity;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.databinding.ActivityLanguageBinding;
import com.javadroid.fakecall.manager.LanguageManager;

import java.util.Locale;

public class Language extends AppCompatActivity {

    private ActivityLanguageBinding binding;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle(getString(R.string.language));
        activity = this;
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        setOnClickListener(R.id.indo, () -> {
            go("id");
        });

        setOnClickListener(R.id.inggris, () -> {
            go("en");
        });


    }

    void go(String string){
        setLocale(string);
    }

    private void setLocale(String lang) {
        LanguageManager.setLanguage(activity, lang,true);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        Intent  intent = new Intent(getBaseContext(), Main.class);
        startActivity(intent);
        customType(Language.this,"fadein-to-fadeout");
        finish();
    }
    private void setOnClickListener(int viewId, Runnable action) {
        findViewById(viewId).setOnClickListener(view -> action.run());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        customType(Language.this,"fadein-to-fadeout");
    }
}