package com.javadroid.fakecall.call.wa;


import static maes.tech.intentanim.CustomIntent.customType;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.javadroid.fakecall.BuildConfig;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Main;
import com.javadroid.fakecall.call.Tools;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoCall extends AppCompatActivity implements SurfaceHolder.Callback {
    private MediaPlayer mp;
    private Camera camera;
    private SurfaceView surfaceView, surfaceView2;
    private VideoView videoView;
    private Handler handler;
    private TextView calling, nameuser;
    private CircleImageView imguser;
    private RelativeLayout tolak;
    private LinearLayout atas, bawah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        String judul = Tools.judul;
        String gambar = Tools.gambar;
        String video = Tools.video;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
        mp.setLooping(true);

        atas = findViewById(R.id.atas);
        bawah = findViewById(R.id.bawah);
        videoView = findViewById(R.id.videoView);

        if (video.startsWith("https://") || video.startsWith("http://")) {
            Uri uri = Uri.parse(video);
            videoView.setVideoURI(uri);
        } else {
            String fileName = "android.resource://" + BuildConfig.APPLICATION_ID + "/raw/" + video;
            videoView.setVideoURI(Uri.parse(fileName));
        }
        videoView.requestFocus();
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView2 = findViewById(R.id.surfaceView2);
        surfaceView2.setVisibility(View.GONE);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.OPAQUE);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        SurfaceHolder surfaceHolder2 = surfaceView2.getHolder();
        surfaceHolder2.addCallback(VideoCall.this);
        surfaceHolder2.setFormat(PixelFormat.OPAQUE);
        surfaceHolder2.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

        handler = new Handler();

        calling = findViewById(R.id.txtcall);
        nameuser = findViewById(R.id.txtname);
        imguser = findViewById(R.id.imguser);
        RelativeLayout cancel = findViewById(R.id.layclose2);
        cancel.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(VideoCall.this,"fadein-to-fadeout");
            mp.stop();

        });

        RelativeLayout pesan = findViewById(R.id.laypesan);
        pesan.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(VideoCall.this,"fadein-to-fadeout");
            mp.stop();

        });

        tolak = findViewById(R.id.layclose);
        tolak.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            customType(VideoCall.this,"fadein-to-fadeout");
            mp.stop();

        });

        RelativeLayout terima = findViewById(R.id.layterima);
        terima.setOnClickListener(this::onClick);

        imguser = findViewById(R.id.imguser);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        imguser.setImageDrawable(drawable);

        nameuser.setText(judul);

    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open(1);
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception ignored) {
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;

    }

    public void onBackPressed() {
        mp.stop();
        Intent intent = new Intent(VideoCall.this, Main.class);
        startActivity(intent);
        finish();
        customType(VideoCall.this,"fadein-to-fadeout");
    }

    private void onClick(View v) {
        mp.stop();
        mp.stop();
        calling.setVisibility(View.GONE);
        nameuser.setVisibility(View.GONE);
        imguser.setVisibility(View.GONE);
        surfaceView.setVisibility(View.GONE);
        surfaceView2.setVisibility(View.VISIBLE);
        videoView.start();
        atas.setVisibility(View.GONE);
        bawah.setVisibility(View.VISIBLE);
        tolak.setVisibility(View.VISIBLE);

    }
}