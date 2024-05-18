package com.javadroid.fakecall.wallpaper;

import static maes.tech.intentanim.CustomIntent.customType;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.javadroid.fakecall.R;
import com.javadroid.fakecall.adapter.wallpaper.AdapterList;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.databinding.ActivityHomeWallBinding;
import com.javadroid.fakecall.model.ModelListWall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends AppCompatActivity {

    private ActivityHomeWallBinding binding;
    private final List<ModelListWall> listData_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.wallpaper));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_home_wall);
        Banner.GET(getBaseContext(),binding.banner);
        binding.recyclerviewList.setHasFixedSize(true);
        binding.recyclerviewList.setLayoutManager(new GridLayoutManager(this,2));
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
                JSONArray jsonObject = json.getJSONArray("list_wallpaper");
                for (int i = 0; i < jsonObject.length(); i++) {
                    JSONObject obj = jsonObject.getJSONObject(i);
                    ModelListWall dataModel = new ModelListWall();
                    dataModel.setImage(obj.getString("url_image"));
                    listData_list.add(0,dataModel);
                }
                AdapterList adapterList = new AdapterList(listData_list, Main.this);
                binding.recyclerviewList.setAdapter(adapterList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
       super.onBackPressed();
        customType(this,"fadein-to-fadeout");
    }

}