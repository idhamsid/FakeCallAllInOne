package com.javadroid.fakecall.activity;

import static com.javadroid.fakecall.activity.Main.dirTarget;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.itemList;
import static com.javadroid.fakecall.adapter.ringtone.RingtoneAdapter.download;
import static com.javadroid.fakecall.config.SettingAll.MAX_LOAD_MP3;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaDrmThrowable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.isConfig.notifController.CreateNotification;
import com.javadroid.fakecall.isConfig.notifController.OnClearFromRecentService;
import com.javadroid.fakecall.isConfig.notifController.PlayerActivity;
import com.javadroid.fakecall.model.ringtone.RingtoneModel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RingtoneActivity extends PlayerActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener {
    public static LinearLayout linearlayout;
    ProgressBar progressBar1, progressBar2;
    public static ImageView iv_min_play, iv_music_play, loopBtn;
    public static Activity activity;
    public static SlidingUpPanelLayout slidingUpPanelLayout;
    public static RelativeLayout layoutPlayer, layoutRL;
    public static MediaPlayer mp = new MediaPlayer();
    public static Boolean loop = false;
    SeekBar seekbar_music;
    private static int REQUEST_PERMISIION = 2704;
    long persenEllapsed = 0;
    long timeElapsed, finalTime, timeRemaining;

    public void showLoader() {
        linearlayout.setVisibility(View.GONE);
        linearlayout.setClickable(false);
        progressBar2.setVisibility(View.VISIBLE);
        progressBar1.setVisibility(View.VISIBLE);
        iv_min_play.setVisibility(View.GONE);
        loopBtn.setVisibility(View.GONE);
        iv_music_play.setVisibility(View.GONE);
    }

    public void hideLoader() {
        linearlayout.setVisibility(View.GONE);
        linearlayout.setClickable(false);
        progressBar2.setVisibility(View.GONE);
        progressBar1.setVisibility(View.GONE);
        iv_min_play.setVisibility(View.VISIBLE);
//        loopBtn.setVisibility(View.VISIBLE);
        iv_music_play.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_ringtone);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISIION);
            }
        }

        linearlayout = findViewById(R.id.linearlayout);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar1 = findViewById(R.id.progressBar1);
        iv_min_play = findViewById(R.id.iv_min_play);
        loopBtn = findViewById(R.id.loop);
        iv_music_play = findViewById(R.id.iv_music_play);
        iv_music_play.setTag("nosound");

        layoutRL = findViewById(R.id.rl);
        layoutPlayer = findViewById(R.id.rl_small);
        slidingUpPanelLayout = findViewById(R.id.sliding_layout);//umanoPanelHeight sothree
        slidingUpPanelLayout.setPanelHeight(0);

        downloadic = findViewById(R.id.downloadic);
        favbtn = findViewById(R.id.favbtn);
        backwardic = findViewById(R.id.backwardic);
        forwardic = findViewById(R.id.forwardic);
        tv_min_title = findViewById(R.id.tv_min_title);
        tv_max_title = findViewById(R.id.tv_max_title);
        seekbar_music = findViewById(R.id.seekbar_music);

        tv_music_time = findViewById(R.id.tv_music_time);
        tv_music_total_time = findViewById(R.id.tv_music_total_time);

        hideLoader();

        Fragment newFragment = new RingtoneFrgmnt();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        seekbar_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mp.seekTo(i * 1000);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        final Handler mHandler = new Handler();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mp != null) {
                    int mCurrentPosition = mp.getCurrentPosition() / 1000;
                    seekbar_music.setProgress(mCurrentPosition);
                    timeElapsed = mp.getCurrentPosition();
                    finalTime = mp.getDuration();
                    tv_music_time.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed))));
                    tv_music_total_time.setText(String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) finalTime), TimeUnit.MILLISECONDS.toSeconds((long) finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime))));
                    if (timeElapsed > 0) {
                        timeRemaining = (finalTime - timeElapsed) / finalTime * 100;
                        if (tv_music_time.getText().equals(tv_music_total_time.getText())) {
                            Log.e("adslog----------------", "sama :  ");
                            currentSong = itemList.get(currentPosition);
                            mp.reset();
                            seekbar_music.setProgress(0);
                            nextSong();
                        }
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });


    }

    ImageView downloadic;
    ImageView backwardic, favbtn, forwardic;
    TextView tv_min_title, tv_max_title;

    TextView tv_music_time;
    TextView tv_music_total_time;
    RingtoneModel currentSong;
    int currentPosition;
    NotificationManager notificationManager;


    private void callNotif() {
        play();
        CreateNotification.createNotification(RingtoneActivity.this, itemList.get(currentPosition),
                R.drawable.baseline_pause_circle_outline_24, currentPosition, itemList.size() - 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"), RECEIVER_EXPORTED);
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            // Log.i("adslogact", "onReceive: " + action);
            switch (action) {
                case CreateNotification.ACTION_PREVIUOS:
                    prevSong();
                    break;
                case CreateNotification.ACTION_NEXT:
                    nextSong();
                    SharedPreferences myScore = getSharedPreferences("MyAwesomeScore", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = myScore.edit();
                    editor.putInt("position", currentPosition);
                    editor.apply();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mp != null) {
                        if (mp.isPlaying()) {
                            mp.pause();
                            CreateNotification.createNotification(RingtoneActivity.this, itemList.get(currentPosition),
                                    R.drawable.baseline_play_circle_outline_24, currentPosition, itemList.size() - 1);

                        } else {
                            mp.start();
                            CreateNotification.createNotification(RingtoneActivity.this, itemList.get(currentPosition),
                                    R.drawable.baseline_pause_circle_outline_24, currentPosition, itemList.size() - 1);
                        }
                    }
                    break;

            }
        }
    };

    public void changeText(final RingtoneModel itemSong, int currentPos, Boolean offline, File offlineFile) {
        currentPosition = currentPos;
        hitungError = 0;
        showLoader();
        Log.i("adslog", "changeText:  " + mp);
        try {
            mp.reset();
            if (!offline) {
                Log.w("adslog", ": online start song ");
                if (itemSong.getSource().startsWith("http")) {
                    mp.setDataSource(itemSong.getSource());
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.setOnBufferingUpdateListener(this);
                    mp.setOnPreparedListener(this);
                    mp.setOnErrorListener(this);
                    mp.prepareAsync();
                } else {
                    AssetFileDescriptor afd = getAssets().openFd(itemSong.getSource());
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.setOnPreparedListener(this);
                    mp.prepare();
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    mp.setDataSource(itemSong.getSourceOff33());
                else
                    mp.setDataSource(offlineFile.getPath());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnBufferingUpdateListener(this);
                mp.setOnPreparedListener(this);
                mp.prepare();
            }
            callNotif();

            tv_min_title.setText(itemSong.getTitle());
            tv_max_title.setText(itemSong.getTitle());

        } catch (Exception e) {

            Log.e("adslog", "Nooo " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        tv_max_title.setText(itemSong.getTitle());
        backwardic.setOnClickListener(v -> prevSong());
        forwardic.setOnClickListener(v -> nextSong());
        iv_music_play.setOnClickListener(v -> {
            if (iv_music_play.getTag().equals("nosound")) {
                Toast.makeText(RingtoneActivity.this, "Please Select A Song First", Toast.LENGTH_SHORT).show();
            } else {
                if (iv_music_play.getTag().equals("playing")) {
                    mp.pause();
                    iv_music_play.setTag("notplaying");
                    iv_music_play.setImageResource(R.drawable.playic);
                    iv_min_play.setImageResource(R.drawable.playic);
                } else {
                    mp.start();
                    iv_music_play.setTag("playing");
                    iv_music_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    iv_min_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    iv_music_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                    iv_min_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        });
        loopBtn.setOnClickListener(v -> {
            if (iv_music_play.getTag().equals("playing")) {
                if (!loop) {
                    mp.setLooping(true);
                    loopBtn.setImageResource(R.drawable.ic_loop_red);
                    loop = true;
                } else {
                    mp.setLooping(false);
                    loopBtn.setImageResource(R.drawable.ic_loop);
                    loop = false;
                }
            }
        });
        iv_min_play.setOnClickListener(v -> {

            if (iv_music_play.getTag().equals("playing")) {
                mp.pause();
                loopBtn.setVisibility(View.INVISIBLE);
                iv_music_play.setTag("notplaying");
                iv_music_play.setImageResource(R.drawable.playic);
                iv_min_play.setImageResource(R.drawable.playic);
            } else {
                mp.start();
//                    loopBtn.setVisibility(View.VISIBLE);
                iv_music_play.setTag("playing");
                iv_music_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                iv_min_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                iv_music_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                iv_min_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
            }

        });
        downloadic.setOnClickListener(view -> {
            if (mp != null) {
                download(itemSong, currentPos);
            }
        });

    }

    private void prevSong() {
        hitungError = 0;
        showLoader();
        // Log.i("adslog", "onClick: back" + itemList.size());
        if (currentPosition > 0)
            currentPosition--;
        else
            Toast.makeText(RingtoneActivity.this, "This start of list ringtone !", Toast.LENGTH_SHORT).show();

        // Log.e("adslog", "onClick: forwawrd " + currentPosition);
        currentSong = itemList.get(currentPosition);
        if (mp.isPlaying()) mp.reset();
        try {
            String stringFile = currentSong.getTitle() + currentSong.getSource().substring(currentSong.getSource().lastIndexOf("."));
            File namaFile = new File(dirTarget, stringFile);
            // Log.d("adslog", "onClick: offline " + namaFile.exists());
            if (!namaFile.exists()) {
                mp.setDataSource(currentSong.getSource());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnBufferingUpdateListener(this);
                mp.setOnErrorListener(this);
                mp.setOnPreparedListener(this);
                mp.prepareAsync();
            } else {
                // Log.i("adslog", "onClick: call offline " + namaFile.getPath());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    mp.setDataSource(currentSong.getSourceOff33());
                else
                    mp.setDataSource(namaFile.getPath());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnPreparedListener(this);
                mp.prepare();
            }
            callNotif();

        } catch (Exception e) {
            // Log.i("adslog", "onClick: exc " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        tv_min_title.setText(currentSong.getTitle());
        tv_max_title.setText(currentSong.getTitle());


    }

    private void nextSong() {
        hitungError = 0;
        showLoader();
        if (currentPosition < itemList.size() - 1)
            currentPosition++;
        else
            Toast.makeText(RingtoneActivity.this, "This end of list ringtone !", Toast.LENGTH_SHORT).show();
        currentSong = itemList.get(currentPosition);

        play();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"), RECEIVER_EXPORTED);
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

        if (mp.isPlaying()) mp.reset();
        try {
            String stringFile = currentSong.getTitle() + currentSong.getSource().substring(currentSong.getSource().lastIndexOf("."));
            File namaFile = new File(dirTarget, stringFile);
            Log.d("adslog", "onClick: offline " + namaFile.exists());
            if (!namaFile.exists()) {
                mp.setDataSource(currentSong.getSource());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnBufferingUpdateListener(this);
                mp.setOnErrorListener(this);
                mp.setOnPreparedListener(this);
                mp.prepareAsync();
            } else {
                // Log.i("adslog", "onClick: call offline " + namaFile.getPath());
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                    mp.setDataSource(currentSong.getSourceOff33());
                else
                    mp.setDataSource(namaFile.getPath());
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setOnBufferingUpdateListener(this);
                mp.setOnPreparedListener(this);
                mp.prepare();
            }
            callNotif();

            tv_min_title.setText(currentSong.getTitle());
            tv_max_title.setText(currentSong.getTitle());
//            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mediaPlayer) {
//                    mp.start();
//                    hideLoader();
////                    // Log.i("adslog", "onPrepared: Main mp prepared list");
//                    int maxPos = mp.getDuration() / 1000;
//
//                    seekbar_music.setMax(maxPos);
//
//                    iv_music_play.setTag("playing");
//                    iv_music_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
//                    iv_min_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
//                    iv_music_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                    iv_min_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                    layoutPlayer.setVisibility(View.VISIBLE);
//                    slidingUpPanelLayout.setPanelHeight(layoutPlayer.getHeight());
//                }
//
//            });
//            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mps) {
//                    currentSong = itemList.get(currentPosition);
//                    mp.reset();
//                    seekbar_music.setProgress(0);
//                    if (currentPosition < itemList.size() - 1)
//                        nextSong();
//                    else {
//                        Toast.makeText(RingtoneActivity.this, "This end of list ringtone !", Toast.LENGTH_SHORT).show();
//                        hideLoader();
//                        iv_music_play.setImageResource(R.drawable.baseline_play_circle_outline_24);
//                        iv_min_play.setImageResource(R.drawable.baseline_play_circle_outline_24);
//                        iv_music_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                        iv_min_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
//                    }
//                }
//            });

        } catch (Exception e) {
            // Log.i("adslog", "onClick: exc " + e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelHeight() > 0) {
            slidingUpPanelLayout.setPanelHeight(0);
        } else {
            if (mp.isPlaying()) mp.reset();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            switch (requestCode) {
                case 1:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(RingtoneActivity.this, "Woho, you have enabled notifications!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RingtoneActivity.this, "Ouch, this is gonna hurt without notifications", Toast.LENGTH_SHORT).show();
                    }
                    return;
            }
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        float persenSekarang;
//        long timeElapsed, finalTime, timeRemaining;

        if (timeElapsed > 0)
            persenSekarang = (float) timeElapsed / finalTime * 100;
        else
            persenSekarang = 0;
        Log.e("adslog", "persen:  " + percent + " persen sekarang " + persenSekarang);
        if (percent == 100) {
            hideLoader();
        } else if (percent > persenSekarang) {
            hideLoader();
        } else {
            showLoader();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("adslog", "onCompletion: ");
        currentSong = itemList.get(currentPosition);
        mp.reset();
        seekbar_music.setProgress(0);
        nextSong();
    }

    int hitungError = 0;

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        hideLoader();
        Log.i("adslog", "onPrepared: Main mp prepared list");
        int maxPos = mp.getDuration() / 1000;
        seekbar_music.setMax(maxPos);
        iv_music_play.setTag("playing");
        iv_music_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        iv_min_play.setImageResource(R.drawable.baseline_pause_circle_outline_24);
        iv_music_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        iv_min_play.setColorFilter(ContextCompat.getColor(activity, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
        layoutPlayer.setVisibility(View.VISIBLE);
        slidingUpPanelLayout.setPanelHeight(layoutPlayer.getHeight());


    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (what == -38) {
            hitungError++;
            if (hitungError >= MAX_LOAD_MP3){
                currentSong = itemList.get(currentPosition);
                mp.reset();
                seekbar_music.setProgress(0);
                nextSong();
            }
            Log.i("adslog", "onError:  " + hitungError);
        }
        return false;
    }
}
