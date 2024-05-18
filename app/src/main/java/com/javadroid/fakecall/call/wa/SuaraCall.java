package com.javadroid.fakecall.call.wa;


import static maes.tech.intentanim.CustomIntent.customType;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Main;
import com.javadroid.fakecall.call.Tools;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuaraCall extends AppCompatActivity {
    private MediaPlayer mp;
    int Seconds, Minutes, MilliSeconds, hours;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    private Handler handler;
    private RelativeLayout tolak;
    private LinearLayout atas, bawah;
    private TextView calling;
    private String gambar;
    private String suara;
    public Runnable runnable = new Runnable() {

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            hours = Minutes / 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            calling.setText(String.format("%02d", hours) + ":" + String.format("%02d", Minutes) + ":"
                    + String.format("%02d", Seconds));
            handler.postDelayed(this, 0);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_voice_call);
        String judul = Tools.judul;
        gambar = Tools.gambar;
        suara = Tools.suara;
        atas = findViewById(R.id.atas);
        bawah = findViewById(R.id.bawah);
        calling = findViewById(R.id.txtcall);
        RelativeLayout cancel = findViewById(R.id.layclose2);
        tolak = findViewById(R.id.layclose);
        RelativeLayout pesan = findViewById(R.id.laypesan);
        pesan.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(SuaraCall.this,"fadein-to-fadeout");

        });
        handler = new Handler();
        tolak.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(SuaraCall.this,"fadein-to-fadeout");

        });

        cancel.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(SuaraCall.this,"fadein-to-fadeout");
        });

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
        mp.setLooping(true);
        TextView judulH = findViewById(R.id.txtname);
        judulH.setText(judul);

        CircleImageView gambrH = findViewById(R.id.imguser);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        gambrH.setImageDrawable(drawable);

        RelativeLayout terima = findViewById(R.id.layterima);
        terima.setOnClickListener(this::onClick);


    }

    public void onBackPressed() {
        mp.stop();
        Intent intent = new Intent(SuaraCall.this, Main.class);
        startActivity(intent);
        finish();
        customType(SuaraCall.this,"fadein-to-fadeout");
    }

    private void onClick(View view) {
        atas.setVisibility(View.GONE);
        tolak.setVisibility(View.VISIBLE);
        bawah.setVisibility(View.VISIBLE);
        StartTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        String url = suara;
        mp.stop();
        try {

            mp = new MediaPlayer();
            if (url.startsWith("http")) {
                mp.setDataSource(url);
            } else {
                AssetFileDescriptor descriptor;
                descriptor = getAssets().openFd(url);
                mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
            }
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepareAsync();
            mp.setOnPreparedListener(MediaPlayer::start);

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }
}