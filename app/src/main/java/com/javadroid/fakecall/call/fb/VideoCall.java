package com.javadroid.fakecall.call.fb;


import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
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
    CircleImageView gambrH;
    ImageView gambrB, imgback;
    MediaPlayer mp;
    RelativeLayout terima, tolak, tolak2;
    LinearLayout atas, bawah;
    Handler handler;
    TextView calling;
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_b_video_call);
        String judul = Tools.judul;
        String gambar = Tools.gambar;
        String video = Tools.video;
        surfaceView = findViewById(R.id.surfaceView);
        surfaceView.setVisibility(View.GONE);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setFormat(PixelFormat.OPAQUE);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
        videoView = findViewById(R.id.videoView);
        videoView.setMediaController(null);

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

        handler = new Handler();
        atas = findViewById(R.id.layutama);
        bawah = findViewById(R.id.laybawah2);
        calling = findViewById(R.id.txtwaktu);

        mp = MediaPlayer.create(this, R.raw.facebook);
        mp.start();
        mp.setLooping(true);

        tolak = findViewById(R.id.laytolak);
        tolak.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();

        });

        imgback = findViewById(R.id.imgback2);
        imgback.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();
        });

        tolak2 = findViewById(R.id.laytolak2);
        tolak2.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCall.this, Main.class);
            startActivity(intent);
            finish();
            mp.stop();
        });

        terima = findViewById(R.id.layterima);
        terima.setOnClickListener(v -> {
            mp.stop();
            surfaceView.setVisibility(View.VISIBLE);
            atas.setVisibility(View.GONE);
            bawah.setVisibility(View.VISIBLE);
            gambrB.setVisibility(View.GONE);
            videoView.start();
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
        Intent intent = new Intent(VideoCall.this, Main.class);
        startActivity(intent);
        finish();
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

}