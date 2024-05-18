package com.javadroid.fakecall.activity;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.config.SettingAll;
import com.javadroid.fakecall.databinding.ActivitySplashBinding;
import com.javadroid.fakecall.manager.LanguageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Splash extends AppCompatActivity{
    private ActivitySplashBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
        );
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_splash);
        AndroidNetworking.initialize(getApplicationContext());
        getsp();

    }

    public void getsp() {
        if (SettingAll.mode_test){
           go_intent();
        }else {
            AndroidNetworking.get(SettingAll.BASEURL)
                    .setPriority(Priority.HIGH)
                    .doNotCacheResponse()
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            {
                                try {
                                    if (getPackageName().equals(response.getString("packagename"))) {
                                        if (response.getString("packagename_new_app").contentEquals("null")) {
                                            SettingAll.interval_inter = response.getInt("interval_inter");
                                            JSONArray jsonArray2 = response.getJSONArray("applovin");
                                            for (int i = 0; i < jsonArray2.length(); i++) {
                                                JSONObject obj = jsonArray2.getJSONObject(i);
                                                SettingAll.banner_ap = obj.getString("banner");
                                                SettingAll.inter_ap = obj.getString("inter");
                                                SettingAll.rewards_ap = obj.getString("reward");
                                                SettingAll.native_ap = obj.getString("native");
                                            }
                                            go_intent();
                                        } else {
                                            binding.sp.setVisibility(View.GONE);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                                            builder.setCancelable(false);
                                            builder.setTitle("Update");
                                            builder.setMessage("A new version is available for this application.");
                                            builder.setPositiveButton("Update", (dialog, which) -> {
                                                Uri uri = null;
                                                try {
                                                    uri = Uri.parse("market://details?id=" + response.getString("link_new_app"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                                try {
                                                    startActivity(goToMarket);
                                                } catch (ActivityNotFoundException e) {
                                                    try {
                                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                                Uri.parse("http://play.google.com/store/apps/details?id=" + response.getString("packagename_new_app"))));
                                                    } catch (JSONException jsonException) {
                                                        jsonException.printStackTrace();
                                                    }
                                                }
                                            });
                                            builder.setNegativeButton("Exit", (dialog, which) -> finish());
                                            final AlertDialog alert = builder.create();
                                            alert.setOnShowListener(arg0 -> {
                                                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
                                                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
                                            });
                                            alert.show();


                                        }
                                    }else {
                                        binding.sp.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Do Not Change Anything In This Application");
                                        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            Toast.makeText(Splash.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void go_intent(){
        new CountDownTimer(4000, 500) {
            @Override
            public void onFinish() {
                Intent intent;
                boolean isLanguage = LanguageManager.isLanguage(Splash.this);
                String language = LanguageManager.getLanguage(Splash.this);
                if (isLanguage){
                    setLocale(language);
                }
                intent = new Intent(getBaseContext(), Language.class);
                startActivity(intent);
                finish();
                customType(Splash.this,"fadein-to-fadeout");
            }

            @Override
            public void onTick(long millisUntilFinished) {
            }
        }.start();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }


}