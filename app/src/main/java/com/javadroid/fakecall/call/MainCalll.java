package com.javadroid.fakecall.call;

import static maes.tech.intentanim.CustomIntent.customType;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Kontak;
import com.javadroid.fakecall.activity.Main;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.call.wa.SuaraCall;
import com.javadroid.fakecall.call.wa.VideoCall;
import com.javadroid.fakecall.databinding.MainCallActivityBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


public class MainCalll extends AppCompatActivity {
    private MainCallActivityBinding binding;
    private static final int REQUEST_CODE = 134;
    public static String tunggu_waktu;
    private PendingIntent pendingIntent;

    @SuppressLint({"UnspecifiedImmutableFlag", "NonConstantResourceId"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.main_call_activity);
        Banner.GET(getBaseContext(),binding.banner);
        Intent alarmIntent = new Intent(this, AppReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, alarmIntent,  PendingIntent.FLAG_IMMUTABLE );
        String nama = Tools.judul;
        String gambar = Tools.gambar;
        binding.name.setText(nama);
        tunggu_waktu =getString(R.string.wait_a_moment);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        binding.user.setImageDrawable(drawable);
        binding.back.setOnClickListener(v -> {
            Intent intentt = new Intent(MainCalll.this, Kontak.class);
            startActivity(intentt);
            finish();
            customType(MainCalll.this, "fadein-to-fadeout");
        });
        binding.waktunya.setOnCheckedChangeListener((radioGroup, id) -> {
            switch (id) {
                case R.id.radio_sekarang:
                    Tools.waktu_call = 1;
                    tunggu_waktu = getString(R.string.wait_2_seconds);
                    break;
                case R.id.radio_10:
                    Tools.waktu_call = Tools.waktu_10;
                    tunggu_waktu = getString(R.string.wait_10_seconds);
                    break;
                case R.id.radio_30:
                    Tools.waktu_call = Tools.waktu_30;
                    tunggu_waktu = getString(R.string.wait_30_seconds);
                    break;
                case R.id.radio_11:
                    Tools.waktu_call = Tools.waktu_60;
                    tunggu_waktu = getString(R.string.wait_1_minute);
                    break;
                case R.id.radio55:
                    Tools.waktu_call = Tools.waktu_300;
                    tunggu_waktu = getString(R.string.wait_5_minute);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + id);
            }
        });
        binding.template.setOnCheckedChangeListener((radioGroup, id) -> {
            switch (id) {
                case R.id.wa:
                    Tools.template_call = 1;
                    break;
                case R.id.fb:
                    Tools.template_call = 2;
                    break;
                case R.id.tele:
                    Tools.template_call = 3;
                    break;
            }
        });
        binding.call.setOnClickListener(v -> {
            //Inter.SHOW(getBaseContext());
            Tools.type_call = 2;
            if (Tools.waktu_call != 1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, Tools.waktu_call);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                Toast.makeText(MainCalll.this, tunggu_waktu, Toast.LENGTH_SHORT).show();
                finish();
                Main.activityMain.finish();
                Kontak.activity.finish();
            } else {
                switch (Tools.template_call) {
                    case 1:
                        Intent intent = new Intent(MainCalll.this, SuaraCall.class);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Intent intent1 = new Intent(MainCalll.this, com.javadroid.fakecall.call.fb.SuaraCall.class);
                        intent1.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                        break;
                    case 3:
                        Intent intent2 = new Intent(MainCalll.this, com.javadroid.fakecall.call.tele.SuaraCall.class);
                        intent2.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
        });
        binding.videoCall.setOnClickListener(v -> {
            //Inter.SHOW(getBaseContext());
            Tools.type_call = 1;
            if (Tools.waktu_call != 1) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, Tools.waktu_call);
                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                Toast.makeText(MainCalll.this, tunggu_waktu, Toast.LENGTH_SHORT).show();
                finish();
                Main.activityMain.finish();
                Kontak.activity.finish();
            } else {
                switch (Tools.template_call) {
                    case 1:
                        Intent intent = new Intent(MainCalll.this, VideoCall.class);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case 2:
                        Intent intent1 = new Intent(MainCalll.this, com.javadroid.fakecall.call.fb.VideoCall.class);
                        intent1.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent1);
                        finish();
                        break;
                    case 3:
                        Intent intent2 = new Intent(MainCalll.this, com.javadroid.fakecall.call.tele.VideoCall.class);
                        intent2.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        finish();
                        break;
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intentt = new Intent(MainCalll.this, Kontak.class);
        startActivity(intentt);
        finish();
        customType(MainCalll.this, "fadein-to-fadeout");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}