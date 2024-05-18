package com.javadroid.fakecall.call.tele;

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
    MediaPlayer mp;
    int Seconds, Minutes, MilliSeconds, hours;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    CircleImageView gambrH;
    private RelativeLayout tolak;
    private LinearLayout atas, bawah;
    private TextView calling;
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
    private ImageView imguser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_voice_call);
        atas = findViewById(R.id.atas);
        bawah = findViewById(R.id.bawah);
        calling = findViewById(R.id.txtcall);
        imguser2 = findViewById(R.id.imguser2);
        ImageView adduser = findViewById(R.id.adduser);
        adduser.setVisibility(View.INVISIBLE);
        RelativeLayout cancel = findViewById(R.id.layclose2);
        tolak = findViewById(R.id.layclose);
        RelativeLayout pesan = findViewById(R.id.laypesan);
        String judul = Tools.judul;
        String gambar = Tools.gambar;
        String suara = Tools.suara;
        pesan.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();

        });
        handler = new Handler();
        tolak.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
        });

        cancel.setOnClickListener(v -> {
            mp.stop();
            Intent intent = new Intent(SuaraCall.this, Main.class);
            startActivity(intent);
            finish();
        });

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
        mp.setLooping(true);
        TextView judulH = findViewById(R.id.txtname);
        judulH.setText(judul);
        gambrH = findViewById(R.id.imguser);

        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        gambrH.setImageDrawable(drawable);
        imguser2.setImageDrawable(drawable);

        RelativeLayout terima = findViewById(R.id.layterima);
        terima.setOnClickListener(view -> {
            atas.setVisibility(View.GONE);
            tolak.setVisibility(View.VISIBLE);
            bawah.setVisibility(View.VISIBLE);
            imguser2.setVisibility(View.VISIBLE);
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            imguser2.setImageDrawable(drawable);

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


    }

    public void onBackPressed() {
        mp.stop();
        Intent intent = new Intent(SuaraCall.this, Main.class);
        startActivity(intent);
        finish();
    }
}