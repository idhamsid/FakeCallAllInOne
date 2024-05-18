package com.javadroid.fakecall.activity;

import static com.javadroid.fakecall.config.SettingAll.PRIVACY_POLICY_LINK;
import static maes.tech.intentanim.CustomIntent.customType;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.databinding.DataBindingUtil;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.ads.applovin.Banner;
import com.javadroid.fakecall.ads.applovin.Initialize;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.config.SettingAll;
import com.javadroid.fakecall.databinding.ActivityMainBinding;
import com.javadroid.fakecall.isConfig.ActivityConfig;
import com.javadroid.fakecall.keyboard.ThemeActivity;

import java.io.File;
import java.util.Locale;

public class Main extends AppCompatActivity {
    private ActivityMainBinding binding;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    @SuppressLint("StaticFieldLeak")
    public static Activity activityMain;
    public static File dirTarget;
    public static String appNamme;
    private static int REQUEST_PERMISIION = 2704;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FirebaseCrashlytics.getInstance().log("Start");
        PRDownloader.initialize(getApplicationContext());
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        appNamme = getString(R.string.app_name);
        dirTarget = ActivityConfig.getBaseDirectory();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISIION);
            }
        }


        if (!Settings.canDrawOverlays(this)) {
            if ("xiaomi".equals(Build.MANUFACTURER.toLowerCase(Locale.ROOT))) {
                final Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", getPackageName());
                new AlertDialog.Builder(this)
                        .setTitle(R.string.please_enable_additional_permissions)
                        .setMessage(R.string.you_will_not)
                        .setPositiveButton(R.string.go_to_settings, (dialog, which) -> startActivity(intent))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(false)
                        .show();
            } else {
                Intent overlaySettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                overlaySettings.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(overlaySettings, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                int LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
                WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                        LAYOUT_FLAG, // Overlay over the other apps.
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE    // This flag will enable the back key press.
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, // make the window to deliver the focus to the BG window.
                        PixelFormat.TRANSPARENT);
            }

        }
        activityMain = this;
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.more, menu);

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.language:
                        Intent intent = new Intent(getBaseContext(), Language.class);
                        startActivity(intent);
                        customType(Main.this, "fadein-to-fadeout");
                        finish();
                        break;
                    case R.id.privacyPolicy:
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(Main.this);
                        alert.setTitle("Privacy Policy");
                        WebView wv = new WebView(Main.this);
                        wv.loadUrl(PRIVACY_POLICY_LINK);
                        wv.setWebViewClient(new WebViewClient());
                        alert.setView(wv);
                        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();
                        break;
                }
                return true;
            }
        });
        Initialize.SDK(getBaseContext());
        Banner.GET(getBaseContext(), binding.banner);
        binding.chatx.setText(getString(R.string.chat));
        binding.callx.setText(getString(R.string.video_call));
        binding.walx.setText(getString(R.string.wallpaper));
        binding.keyxx.setText(getString(R.string.keyboard));
        binding.ring.setText("Ringtone");
        binding.chat.setOnClickListener(v -> {
            Inter.SHOW(this);
            Intent intent = new Intent(getBaseContext(), Kontak.class);
            SettingAll.aksi = "chat";
            startActivity(intent);
            finish();
            customType(Main.this, "fadein-to-fadeout");
        });

        binding.videoCall.setOnClickListener(v -> {
            Inter.SHOW(this);
            Intent intent = new Intent(getBaseContext(), Kontak.class);
            SettingAll.aksi = "call_suara";
            startActivity(intent);
            finish();
            customType(Main.this, "fadein-to-fadeout");
        });

        binding.wallpaper.setOnClickListener(v -> {
            Inter.SHOW(this);
            Intent intent = new Intent(getBaseContext(), com.javadroid.fakecall.wallpaper.Main.class);
            startActivity(intent);
            customType(Main.this, "fadein-to-fadeout");
        });

        binding.keyboard.setOnClickListener(v -> {
            Inter.SHOW(this);
            Intent intent = new Intent(getBaseContext(), ThemeActivity.class);
            startActivity(intent);
            customType(Main.this, "fadein-to-fadeout");
        });

        binding.ringtone.setOnClickListener(v -> {
            Inter.SHOW(this);
            Intent intent = new Intent(getBaseContext(), RingtoneActivity.class);
            startActivity(intent);
            customType(Main.this, "fadein-to-fadeout");
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage(R.string.message_exit);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 103) {
                //            Log.i("adslog", "onActivityResult: " + ContextCompat.checkSelfPermission(activity, "android.permission.READ_MEDIA_AUDIO"));
                if (!dirTarget.exists()) {
                    dirTarget.mkdirs();
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == 2704) {
                //            Log.i("adslog", "onActivityResult: " + ContextCompat.checkSelfPermission(activity, "android.permission.MANAGE_WRITE_SETTINGS"));
                //            Log.i("adslog", "onActivityResult: " + ContextCompat.checkSelfPermission(activity, "android.permission.MANAGE_WRITE_SETTINGS"));
                //            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC) + File.separator + activity.getResources().getString(R.string.app_name));
                if (!dirTarget.exists()) {
                    dirTarget.mkdirs();
                }
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(Main.this, "Woho, you have enabled notifications!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Main.this, "Ouch, this is gonna hurt without notifications", Toast.LENGTH_SHORT).show();
                    }
                    return;
            }
        }

    }
}