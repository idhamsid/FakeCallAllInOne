package com.javadroid.fakecall.activity;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static maes.tech.intentanim.CustomIntent.customType;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.call.MainCalll;
import com.javadroid.fakecall.call.Tools;
import com.javadroid.fakecall.chat.MainChat;
import com.javadroid.fakecall.adapter.AdapterKontak;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.config.SettingAll;
import com.javadroid.fakecall.databinding.ActivityKontakBinding;
import com.javadroid.fakecall.listener.RecyclerTouchListener;
import com.javadroid.fakecall.model.ModelKontak;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Kontak extends AppCompatActivity {

    private ActivityKontakBinding binding;
    private final List<ModelKontak> listData_list = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.kontak));
        {activity = this;}

        isStoragePermissionGranted();
        binding= DataBindingUtil.setContentView(this,R.layout.activity_kontak);
        Banner.GET(getBaseContext(),binding.banner);
        binding.recyclerviewList.setHasFixedSize(true);
        binding.recyclerviewList.setLayoutManager(new GridLayoutManager(this,2));
        binding.recyclerviewList.addOnItemTouchListener(new RecyclerTouchListener(this, binding.recyclerviewList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Inter.SHOW(Kontak.this);
                switch (SettingAll.aksi) {
                    case "chat":
                        Intent intent = new Intent(Kontak.this, MainChat.class);
                        Tools.judul = listData_list.get(position).getNama();
                        Tools.gambar = listData_list.get(position).getImage();
                        Tools.suara = listData_list.get(position).getSuara();
                        Tools.video = listData_list.get(position).getVideo();
                        startActivity(intent);
                        finish();
                        customType(Kontak.this, "fadein-to-fadeout");
                        break;
                    case "call_suara":
                        Intent intentt = new Intent(Kontak.this, MainCalll.class);
                        Tools.judul = listData_list.get(position).getNama();
                        Tools.gambar = listData_list.get(position).getImage();
                        Tools.suara = listData_list.get(position).getSuara();
                        Tools.video = listData_list.get(position).getVideo();
                        startActivity(intentt);
                        finish();
                        customType(Kontak.this, "fadein-to-fadeout");
                        break;
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        getlist();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("konten.json"); // your file name
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void getlist(){
        listData_list.clear();
        String jsonString = loadJSONFromAsset();
        if(jsonString != null){
            try {
                JSONObject json = new JSONObject(jsonString);
                JSONArray jsonObject = json.getJSONArray("list_kontak");
                for (int i = 0; i < jsonObject.length(); i++) {
                    JSONObject obj = jsonObject.getJSONObject(i);
                    ModelKontak dataModel = new ModelKontak();
                    dataModel.setId(obj.getString("id"));
                    dataModel.setNama(obj.getString("nama"));
                    dataModel.setImage(obj.getString("gambar"));
                    dataModel.setSuara(obj.getString("url_suara"));
                    dataModel.setVideo(obj.getString("url_video"));
                    listData_list.add(0,dataModel);
                }
                AdapterKontak adapterList = new AdapterKontak(listData_list, Kontak.this);
                binding.recyclerviewList.setAdapter(adapterList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }



    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else { //permission is automatically granted on sdk<23 upon installation
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intentt = new Intent(Kontak.this, Main.class);
        startActivity(intentt);
        finish();
        customType(Kontak.this, "fadein-to-fadeout");
    }
}