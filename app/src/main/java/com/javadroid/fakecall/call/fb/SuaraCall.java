package com.javadroid.fakecall.call.fb;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
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
    CircleImageView gambrH;
    ImageView gambrB, imgback;
    MediaPlayer mp;
    RelativeLayout terima, tolak, tolak2;
    LinearLayout atas, bawah;
    int Seconds, Minutes, MilliSeconds, hours;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    TextView calling;
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
        getWindow().getDecorView().setSystemUiVisibility(1280);
        getWindow().setStatusBarColor(1140850688);
        int currentApiVersion = Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            });
        }
        setContentView(R.layout.activity_f_b_voice_call);
        String judul = Tools.judul;
        String gambar = Tools.gambar;
        String suara = Tools.suara;
        handler = new Handler();
        atas = findViewById(R.id.laybawah1);
        bawah = findViewById(R.id.laybawah2);
        calling = findViewById(R.id.txtwaktu);

        mp = MediaPlayer.create(this, R.raw.facebook);
        mp.start();
        mp.setLooping(true);

        tolak = findViewById(R.id.laytolak);
        tolak.setOnClickListener(v -> {
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();

        });

        imgback = findViewById(R.id.imgback2);
        imgback.setOnClickListener(v -> {
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();

        });
        tolak2 = findViewById(R.id.laytolak2);
        tolak2.setOnClickListener(v -> {
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();

        });

        terima = findViewById(R.id.layterima);
        terima.setOnClickListener(v -> {
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            atas.setVisibility(View.GONE);
            bawah.setVisibility(View.VISIBLE);
            mp.stop();
            try {
                mp = new MediaPlayer();
                if (suara.startsWith("http")) {
                    mp.setDataSource(suara);
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                } else {
                    AssetFileDescriptor descriptor;
                    descriptor = getAssets().openFd(suara);
                    mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                    descriptor.close();
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                }
                mp.prepareAsync();
                mp.setOnPreparedListener(MediaPlayer::start);

            } catch (IllegalArgumentException | IllegalStateException | IOException e) {
                e.printStackTrace();
            }
        });

        TextView judulH = findViewById(R.id.txtfbname);
        judulH.setText(judul);

        gambrH = findViewById(R.id.fbimguser);
        gambrB = findViewById(R.id.imgback);

        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        gambrH.setImageDrawable(drawable);
        gambrB.setImageDrawable(drawable);
    }

    public void onBackPressed() {
        mp.stop();
        Intent intent = new Intent(SuaraCall.this, Main.class);
        startActivity(intent);
        finish();
    }
}