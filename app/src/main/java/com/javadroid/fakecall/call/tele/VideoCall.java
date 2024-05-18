package com.javadroid.fakecall.call.tele;


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
import android.widget.ImageView;
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
    MediaPlayer mp;
    Camera camera;
    SurfaceView surfaceView, surfaceView2;
    SurfaceHolder surfaceHolder;
    VideoView videoView;
    Handler handler;
    private TextView calling, nameuser;
    private ImageView adduser;
    private CircleImageView imguser;
    private RelativeLayout tolak;
    private LinearLayout atas, bawah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tele_video_call);
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

        if (video.startsWith("https://")) {
            Uri uri = Uri.parse(video);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
        } else if (video.startsWith("http://")) {
            Uri uri = Uri.parse(video);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
        } else {
            String fileName = "android.resource://" + BuildConfig.APPLICATION_ID + "/raw/" + video;
            videoView.setVideoURI(Uri.parse(fileName));
            videoView.requestFocus();
        }


        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setVisibility(View.GONE);
        surfaceView2 = findViewById(R.id.surfaceView2);
        surfaceView2.setVisibility(View.GONE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.OPAQUE);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        handler = new Handler();
        calling = findViewById(R.id.txtcall);
        nameuser = findViewById(R.id.txtname);
        imguser = findViewById(R.id.imguser);
        adduser = findViewById(R.id.adduser);
        adduser.setVisibility(View.INVISIBLE);
        RelativeLayout cancel = findViewById(R.id.layclose2);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoCall.this, Main.class);
                startActivity(intent);
                finish();
                mp.stop();
            }
        });

        RelativeLayout pesan = findViewById(R.id.laypesan);
        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoCall.this, Main.class);
                startActivity(intent);
                finish();
                mp.stop();
            }
        });

        tolak = findViewById(R.id.layclose);
        tolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoCall.this, Main.class);
                startActivity(intent);
                finish();
                mp.stop();
            }
        });

        RelativeLayout terima = findViewById(R.id.layterima);
        terima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                mp.stop();
                calling.setVisibility(View.GONE);
                nameuser.setVisibility(View.GONE);
                imguser.setVisibility(View.GONE);
                adduser.setVisibility(View.VISIBLE);
                surfaceView.setVisibility(View.GONE);
                surfaceView2.setVisibility(View.VISIBLE);
                surfaceHolder = surfaceView2.getHolder();
                surfaceHolder.addCallback(VideoCall.this);
                surfaceHolder.setFormat(PixelFormat.OPAQUE);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);

                videoView.start();
                atas.setVisibility(View.GONE);
                bawah.setVisibility(View.VISIBLE);
                tolak.setVisibility(View.VISIBLE);

            }
        });

        imguser = findViewById(R.id.imguser);
        nameuser.setText(judul);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        imguser.setImageDrawable(drawable);

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

        } catch (Exception e) {
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
    }
}