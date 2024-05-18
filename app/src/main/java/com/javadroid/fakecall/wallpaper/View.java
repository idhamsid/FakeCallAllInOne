package com.javadroid.fakecall.wallpaper;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.databinding.ActivityViewWallBinding;

import java.io.IOException;
import java.io.InputStream;

public class View extends AppCompatActivity {

    private ActivityViewWallBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setSystemUiVisibility(
                android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                        | android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
        );

        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_view_wall);
        Banner.GET(getBaseContext(),binding.banner);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(getIntent().getStringExtra("img"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        binding.imgFull.setImageDrawable(drawable);

        binding.apply.setOnClickListener(view -> {
            Inter.SHOW(this);
            setBackground();
        });
    }

    private void setBackground() {
        Bitmap bitmap = ((BitmapDrawable)binding.imgFull.getDrawable()).getBitmap();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try {
            manager.setBitmap(bitmap);
            Toast.makeText(this, R.string.successful_set_wallpaper, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.there_is_a_mistake, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        customType(View.this,"fadein-to-fadeout");
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        customType(View.this,"fadein-to-fadeout");
    }
}