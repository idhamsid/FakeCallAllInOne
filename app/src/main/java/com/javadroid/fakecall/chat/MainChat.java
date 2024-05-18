package com.javadroid.fakecall.chat;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.Kontak;
import com.javadroid.fakecall.adapter.chat.PertanyaanAdapter;
import com.javadroid.fakecall.call.MainCalll;
import com.javadroid.fakecall.call.Tools;
import com.javadroid.fakecall.adapter.chat.AdapterChat;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.databinding.ActivityMainChatBinding;
import com.javadroid.fakecall.manager.LanguageManager;
import com.javadroid.fakecall.model.chat.ModelChat;
import com.javadroid.fakecall.model.chat.PertanyaanModel;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainChat extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainChatBinding binding;

    private List<Integer> listViewType;
    private List<ModelChat> listDataSms;
    private AdapterChat adapterSms;
    private SimpleDateFormat simpleDateFormat;
    private Chat chat;
    private PertanyaanAdapter adapter;
    private InputStream inputStream;
    private String mas_folder;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_main_chat);
        Banner.GET(getBaseContext(),binding.banner);
        simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
        binding.name.setText(Tools.judul);
        InputStream inputstream= null;
        try {
            inputstream = getAssets().open(Tools.gambar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable drawable = Drawable.createFromStream(inputstream, null);
        binding.user.setImageDrawable(drawable);
        binding.floatingActionButtonKirimPesan.setOnClickListener(this);
        binding.back.setOnClickListener(v -> {
            Intent intentt = new Intent(MainChat.this, Kontak.class);
            startActivity(intentt);
            finish();
            customType(MainChat.this, "fadein-to-fadeout");
        });
        binding.call.setOnClickListener(v -> {
            Intent intentt = new Intent(MainChat.this, MainCalll.class);
            startActivity(intentt);
            finish();
            customType(MainChat.this, "fadein-to-fadeout");
        });

        binding.videoCall.setOnClickListener(v -> {
            Intent intentt = new Intent(MainChat.this, MainCalll.class);
            startActivity(intentt);
            finish();
            customType(MainChat.this, "fadein-to-fadeout");
        });
        loadData();
        List<PertanyaanModel> daftarPertanyaan = parseAIMLFile();

        for (PertanyaanModel pertanyaan : daftarPertanyaan) {
            System.out.println("Pertanyaan: " + pertanyaan);
        }
        adapter = new PertanyaanAdapter(daftarPertanyaan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerViewTemp.setLayoutManager(layoutManager);
        binding.recyclerViewTemp.setAdapter(adapter);
        adapter.setOnItemClickListener(new PertanyaanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Inter.SHOW(MainChat.this);
                String pesan = daftarPertanyaan.get(position).getPertanyaan();
                if (TextUtils.isEmpty(pesan)) {
                    Toast.makeText(MainChat.this, R.string.message_has_not_been_filled, Toast.LENGTH_SHORT).show();
                    binding.editTextIsiPesan.setError(getString(R.string.blank_message));
                } else {
                    binding.editTextIsiPesan.setText("");
                    listViewType.add(AdapterChat.VIEW_TYPE_KANAN);
                    ModelChat dataSms = new ModelChat();
                    dataSms.setPesan(pesan);
                    dataSms.setWaktu(simpleDateFormat.format(new Date()));
                    listDataSms.add(dataSms);

                    //  bot response
                    String botResponse = chat.multisentenceRespond(pesan);
                    listViewType.add(AdapterChat.VIEW_TYPE_KIRI);
                    ModelChat dataSmsBot = new ModelChat();
                    dataSmsBot.setPesan(botResponse);
                    dataSmsBot.setWaktu(simpleDateFormat.format(new Date()));
                    listDataSms.add(dataSmsBot);
                    adapterSms.notifyDataSetChanged();
                }
            }
        });
    }

    private List<PertanyaanModel> parseAIMLFile() {
        List<PertanyaanModel> daftarPertanyaan = new ArrayList<>();
        try {
            AssetManager assetManager = getAssets();
            String language = LanguageManager.getLanguage(this);
            if (language.equals("en")){
                inputStream = assetManager.open("chatbot/aiml/profile_bot.aiml");
            }else if (language.equals("id")){
                inputStream = assetManager.open("chatbot_in/aiml/profile_bot.aiml");
            }


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inputStream, null);

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("pattern")) {
                    eventType = xpp.next();
                    if (eventType == XmlPullParser.TEXT) {
                        String pertanyaan = xpp.getText().trim();
                        PertanyaanModel pertanyaanModel = new PertanyaanModel(pertanyaan);
                        daftarPertanyaan.add(pertanyaanModel);
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return daftarPertanyaan;
    }

    private void loadData() {
        //  checking SD Card availability
        boolean sdCardAvailable = isSdCardAvailable();

        //  receiving the assets from the app directory
        String language = LanguageManager.getLanguage(this);
        if (language.equals("en")){
           mas_folder = "chatbot";
        }else if (language.equals("id")){
            mas_folder = "chatbot_in";
        }
        AssetManager assetManager = getResources().getAssets();
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"/"+mas_folder+"/bots/"+Tools.judul);
        boolean makeDir = dir.mkdirs();
        if (dir.exists()) {
            //  reading the file assets
            try {
                for (String fileAsset : assetManager.list(mas_folder)) {
                    File subdir = new File(dir.getPath() + "/" + fileAsset);
                    boolean subdirCheck = subdir.mkdirs();
                    for (String file : assetManager.list(mas_folder+"/" + fileAsset)) {
                        File f = new File(dir.getPath() + "/" + fileAsset + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assetManager.open(mas_folder+"/" + fileAsset + "/" + file);
                        out = new FileOutputStream(dir.getPath() + "/" + fileAsset + "/" + file);

                        //  copy file from assets to the mobile SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //  get the working directory
        MagicStrings.root_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/"+mas_folder;
        System.out.println("working directory: " + MagicStrings.root_path);
        AIMLProcessor.extension = new PCAIMLProcessorExtension();

        //  assign the AIML files to bot for processing
        Bot bot = new Bot(Tools.judul, MagicStrings.root_path, "chat");
        chat = new Chat(bot);



        listViewType = new ArrayList<>();
        listDataSms = new ArrayList<>();
        /*listViewType.add(AdapterChat.VIEW_TYPE_KIRI);
        ModelChat dataSms = new ModelChat();
        dataSms.setPesan("Hello, apa kabar?");
        dataSms.setWaktu(simpleDateFormat.format(new Date()));
        listDataSms.add(dataSms);*/
        adapterSms = new AdapterChat(listViewType, listDataSms);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewMain.setLayoutManager(layoutManager);
        binding.recyclerViewMain.setAdapter(adapterSms);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private boolean isSdCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClick(View view) {
        Inter.SHOW(this);
        if (view == binding.floatingActionButtonKirimPesan) {
            String pesan = binding.editTextIsiPesan.getText().toString();
            if (TextUtils.isEmpty(pesan)) {
                Toast.makeText(this, R.string.message_has_not_been_filled, Toast.LENGTH_SHORT).show();
                binding.editTextIsiPesan.setError(getString(R.string.blank_message));
            } else {
                binding.editTextIsiPesan.setText("");
                listViewType.add(AdapterChat.VIEW_TYPE_KANAN);
                ModelChat dataSms = new ModelChat();
                dataSms.setPesan(pesan);
                dataSms.setWaktu(simpleDateFormat.format(new Date()));
                listDataSms.add(dataSms);

                //  bot response
                String botResponse = chat.multisentenceRespond(pesan);
                listViewType.add(AdapterChat.VIEW_TYPE_KIRI);
                ModelChat dataSmsBot = new ModelChat();
                dataSmsBot.setPesan(botResponse);
                dataSmsBot.setWaktu(simpleDateFormat.format(new Date()));
                listDataSms.add(dataSmsBot);
                adapterSms.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intentt = new Intent(MainChat.this, Kontak.class);
        startActivity(intentt);
        finish();
        customType(MainChat.this, "fadein-to-fadeout");
    }
}