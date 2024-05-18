package com.javadroid.fakecall.adapter.ringtone;

import static com.javadroid.fakecall.MyApplication.mFirebaseAnalytics;
import static com.javadroid.fakecall.activity.Main.dirTarget;
import static com.javadroid.fakecall.activity.RingtoneActivity.activity;
import static com.javadroid.fakecall.activity.RingtoneActivity.layoutPlayer;
import static com.javadroid.fakecall.activity.RingtoneActivity.slidingUpPanelLayout;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.checkIfAlreadyhavePermission;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.loading;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.online;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.requestForSpecificPermission;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.spaceDown;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.javadroid.fakecall.R;
import com.javadroid.fakecall.activity.RingtoneActivity;
import com.javadroid.fakecall.ads.applovin.Inter;
import com.javadroid.fakecall.isConfig.KDDownloadFile;
import com.javadroid.fakecall.isConfig.KDDownloadListener;
import com.javadroid.fakecall.model.ringtone.RingtoneModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class RingtoneAdapter extends RecyclerView.Adapter {
    public static ArrayList<RingtoneModel> ringtoneList;
    public static ArrayList<RingtoneModel> mFilteredList;
    public static Context context;
    MediaPlayer mediaPlayer;
    public static final int SET_AS_RINGTONE = 1;
    public static final int SET_AS_ALARM = 2;
    public static final int SET_AS_MSG = 3;

    public RingtoneAdapter(ArrayList<RingtoneModel> soundList, Context context, AdapterListener adapterListener) {
        ringtoneList = soundList;
        mFilteredList = soundList;
        this.context = context;
        mediaPlayer = new MediaPlayer();
        this.adapterListener = adapterListener;

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView musictitle;
        public ImageView download_btn, menuiv, imgPlay;
        RelativeLayout layRingtone;

        public ViewHolder(View itemView) {
            super(itemView);
            layRingtone = itemView.findViewById(R.id.relativelayout);
            musictitle = itemView.findViewById(R.id.musictitle);
            download_btn = itemView.findViewById(R.id.download_btn);
            menuiv = itemView.findViewById(R.id.menuiv);
            imgPlay = itemView.findViewById(R.id.play_btn);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_ringtone, parent, false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final RingtoneModel noList = mFilteredList.get(position);
        String stringFile = noList.getTitle() + noList.getSource().substring(noList.getSource().lastIndexOf("."));
        File namaFile = new File(dirTarget, stringFile);
        if (namaFile.exists()) {
            Log.i("adslog", "onBindViewHolder: namaFile " + namaFile);
            if (!online) ((ViewHolder) holder).menuiv.setVisibility(View.GONE);
            else
                ((ViewHolder) holder).menuiv.setVisibility(View.VISIBLE);
            ((ViewHolder) holder).download_btn.setVisibility(View.GONE);
//            ((ViewHolder) holder).layRingtone.setBackground(ContextCompat.getDrawable(context, R.drawable.gradientdone));
//            ((ViewHolder) holder).musictitle.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        ((ViewHolder) holder).musictitle.setText(noList.getTitle());
        ((ViewHolder) holder).layRingtone.setOnClickListener(v -> {
            Boolean offline;
            File loadOfflineFile = null;
            if (((ViewHolder) holder).menuiv.getVisibility() == View.VISIBLE) {
                offline = true;
                loadOfflineFile = namaFile;
            } else
                offline = false;


            layoutPlayer.setVisibility(View.VISIBLE);
            slidingUpPanelLayout.setPanelHeight(layoutPlayer.getHeight());
            spaceDown.setVisibility(View.VISIBLE);
            try {
                RingtoneActivity.mp.reset();
                ((RingtoneActivity) context).changeText(noList, position, offline, loadOfflineFile);
            } catch (Exception ex) {
                Log.i("adslog", "onClick: err or " + ex.getMessage());
            }


        });


        ((ViewHolder) holder).download_btn.setOnClickListener(v -> {
            if (!checkIfAlreadyhavePermission(activity)) {
                requestForSpecificPermission(activity);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    try {
                        new downloadFile().execute(noList.getSource(), noList.getTitle(), Integer.toString(position));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Inter.SHOWADS((Activity) context);
                                notifyDataSetChanged();
                            }
                        }, 2000);
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                } else {
                    try {
                        new KDDownloadFile.Builder(activity)
                                .setUrl(noList.getSource())
                                .setName(noList.getTitle())
                                .setDirectory(String.valueOf(dirTarget))
                                .setDownloadMessage(true)
                                .setListener(new KDDownloadListener() {
                                    @Override
                                    public void onFinish(String directory, String filename) {
                                        File from = new File(dirTarget, filename);
                                        File to = new File(directory, noList.getTitle() + ".mp3");
                                        from.renameTo(to);
                                        Inter.SHOWADS((Activity) context);
                                        if (from.exists()) {
//                                            ((ViewHolder) holder).menuiv.setVisibility(View.VISIBLE);
//                                            ((ViewHolder) holder).download_btn.setVisibility(View.GONE);
                                            adapterListener.beresDownload(position);

                                        }
                                    }
                                })
                                .download();
                    } catch (Exception e) {
                        Log.i("adslog", "onBindViewHolder: excption " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }
                }
            }
        });
        ((ViewHolder) holder).menuiv.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, ((ViewHolder) holder).menuiv);
            popup.inflate(R.menu.popupmenu);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
//                    ringtonename = stringFile.substring(0,stringFile.lastIndexOf("."));
                    Log.w("adslog", "onMenuItemClick: stringFile " + stringFile);
                    Log.w("adslog", "onMenuItemClick: nmamFile " + namaFile);
                    switch (item.getItemId()) {
                        case R.id.menu_setas:
                            try {
                                setringtone(stringFile, namaFile, SET_AS_RINGTONE);
                            } catch (Exception e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                Log.i("adslog", "onMenuItemClick: exc " + e.getMessage());
                                e.printStackTrace();
                            }
                            return true;
                        case R.id.menu_setasalarm:
                            setringtone(stringFile, namaFile, SET_AS_ALARM);
                            return true;
                        case R.id.menu_setasnotification:
                            setringtone(stringFile, namaFile, SET_AS_MSG);
                        default:
                            return false;
                    }
                }
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList == null ? 0 : mFilteredList.size();
    }


    private static AdapterListener adapterListener;

    public interface AdapterListener {
        void beresDownload(int pos);
    }

    public void setAdapterListener(AdapterListener adaptListener) {
        adapterListener = adaptListener;
    }


    public static void download(final RingtoneModel track, int pos) {
        new KDDownloadFile.Builder(activity)
                .setUrl(track.getSource())
                .setName(track.getTitle())
                .setDirectory(String.valueOf(dirTarget))
                .setDownloadMessage(true)
                .setListener(new KDDownloadListener() {
                    @Override
                    public void onFinish(String directory, String filename) {
                        adapterListener.beresDownload(pos);
                    }
                })
                .download();

    }


    public static class downloadFile extends AsyncTask<String, String, String> {
        String result = "";
        String namaFile;
        Integer posisiOffline;
        File mFile;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.show();

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {

                // TODO Proses ke SongInfo dari sini
                URL url = new URL(aurl[0]);
                namaFile = aurl[1];
                posisiOffline = Integer.valueOf(aurl[2]);
                URLConnection connection = url.openConnection();
                connection.connect();
                mFile = new File(dirTarget + "/" + ((namaFile == null || !namaFile.contains(".mp3")) ? namaFile + ".mp3" : namaFile));
                Bundle params = new Bundle();
                params.putString("url", String.valueOf(url));
                params.putString("namaFile", namaFile);
                mFirebaseAnalytics.logEvent("log", params);
                int lenghtOfFile = connection.getContentLength();
                int lengthFileExist = Integer.parseInt(String.valueOf(mFile.length()));
                if (!mFile.exists() || lengthFileExist < lenghtOfFile)
                    try {
                        InputStream input = new BufferedInputStream(url.openStream());
                        OutputStream output = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            output = Files.newOutputStream(Paths.get(mFile.getAbsolutePath()));
                        }
                        byte data[] = new byte[1024];
                        long total = 0;
                        while ((count = input.read(data)) != -1) {
                            total += count;
                            publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                            output.write(data, 0, count);
                        }
                        assert output != null;
                        output.close();
                        input.close();
                        result = "true";
                        refreshStorage(context, mFile.getAbsolutePath());

                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("url " + url);
                        FirebaseCrashlytics.getInstance().log("namaFile " + namaFile);
                        FirebaseCrashlytics.getInstance().log("mFile " + mFile);
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.e("adslogonline", "doInBackground: exxception " + e.getMessage());
                        result = "false";
                    }
            } catch (Throwable ex) {
                FirebaseCrashlytics.getInstance().recordException(ex);
                ex.printStackTrace();
                result = "false";
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            loading.setLabel("Download " + namaFile.replace(".mp3", "") + "\n " + Integer.parseInt(progress[0]) + "%");
        }

        @Override
        protected void onPostExecute(String unused) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.dismiss();
                }
            }, 1500);
            adapterListener.beresDownload(posisiOffline);
        }
    }


    public static void refreshStorage(Context mContext, String filePath) {
        if (Build.VERSION.SDK_INT < 19) {
            mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            return;
        }
        MediaScannerConnection.scanFile(mContext, new String[]{filePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
            }
        });
    }

    private String ringtonename;

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void setringtone(String stringName, File fileName, int setOption) {
        loading.show();
        String path = (dirTarget.getAbsolutePath() + "/media/");
        File k = new File(path, stringName);
        ringtonename = stringName.substring(0, stringName.lastIndexOf("."));
        String option;
        if (setOption == SET_AS_RINGTONE) option = "Ringtone";
        else if (setOption == SET_AS_ALARM) option = "Alarm";
        else option = "Notification ";
        loading.setLabel("Applying " + ringtonename + " as " + option);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (!checkIfAlreadyhavePermission((Activity) context)) {
                requestForSpecificPermission((Activity) context);
            } else {
                Log.e("adslog", "setringtone: pass permission ");
                Uri ringtoneuri = null;
                try {
                    ContentValues contentValues = new ContentValues();
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R)
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName.getAbsolutePath()); //TODO DISPLAY_NAME
                    else
                        contentValues.put(MediaStore.MediaColumns.DATA, fileName.getAbsolutePath()); //TODO DISPLAY_NAME

                    contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(fileName.getAbsolutePath()));
                    contentValues.put(MediaStore.MediaColumns.SIZE, fileName.length());
                    contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
                    contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                    contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                    contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
                    contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
                    Log.i("adslog", "setringtone: entri ringtonename " + ringtonename);
                    Log.i("adslog", "setringtone: entri fileName.getAbsolutePath() " + fileName.getAbsolutePath());
                    Log.i("adslog", "setringtone: entri getMIMEType(fileName.getAbsolutePath()) " + getMIMEType(fileName.getAbsolutePath()));
                    Log.i("adslog", "setringtone: entri fileName.length() " + fileName.length());
                    ContentResolver contentResolver = context.getContentResolver();
                    Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R)
                        contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DISPLAY_NAME + "='" + fileName.getAbsolutePath() + "'", null);
                    else
                        contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + fileName.getAbsolutePath() + "'", null);
                    ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);
                    Log.w("adslog", "setringtone: generalaudiouri " + generalaudiouri);
//                Log.i("adslog", "setringtone: " + setOption);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    Log.i("adslog", "setringtone: eexv " + e.getMessage());
                    e.printStackTrace();
                }
                if (setOption == SET_AS_RINGTONE) {
                    Log.i("adslog", "setringtone: setOption == SET_AS_RINGTONE ");
                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Ringtone Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Log.i("adslog", "setringtone: checsytem true ");
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Ringtone ", Toast.LENGTH_SHORT).show();
                    }

                } else if (setOption == SET_AS_ALARM) {

                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Alarm Tone Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Alarm Tone ", Toast.LENGTH_SHORT).show();
                    }

                } else if (setOption == SET_AS_MSG) {
                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Notification Sound Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Notification ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        } else {
//            Log.d("adslog", "setringtone: outputfile " + outputfile.getAbsolutePath());
//            Log.d("adslog", "setringtone: MediaStore.Audio.Media.getContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()) " + MediaStore.Audio.Media.getContentUri(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()));
            Set<String> volumeNames = MediaStore.getExternalVolumeNames(context);
            String firstVolumeName = volumeNames.iterator().next();
//            Log.i("adslog", "setringtone: " + firstVolumeName);
            try {
//                Log.i("ringtoon", "setringtone: outputfile.getAbsolutePath() " + k.getAbsolutePath());
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, k.getAbsolutePath());
                contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, getMIMEType(k.getAbsolutePath()));
                contentValues.put(MediaStore.MediaColumns.SIZE, k.length());
                contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
                contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
                ContentResolver contentResolver = context.getContentResolver();
                Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                Log.d("ringtoon", "setringtone: MediaStore.Audio.Media.EXTERNAL_CONTENT_URI " + MediaStore.MediaColumns.DISPLAY_NAME);
                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DISPLAY_NAME + "='" + k.getAbsolutePath() + "'", null);
                Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);

//                Log.w("ringtoon", "setringtone: generalaudiouri " + generalaudiouri);
//                Log.w("ringtoon", "setringtone: ringtoneuri " + ringtoneuri);
                if (setOption == SET_AS_RINGTONE) {
                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Ringtone Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Ringtone ", Toast.LENGTH_SHORT).show();
                    }

                } else if (setOption == SET_AS_ALARM) {

                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Alarm Tone Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Alarm Tone ", Toast.LENGTH_SHORT).show();
                    }

                } else if (setOption == SET_AS_MSG) {
                    try {
                        if (checkSystemWritePermission()) {
                            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loading.dismiss();
                                    Toast.makeText(context, "Notification Sound Set Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }, 2000);
                        } else {
                            Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                        Log.i("adslog", e.toString());
                        Toast.makeText(context, "Unable to set as Notification ", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.e("adslog", "setringtone: exception 683MAA " + e.getMessage());
            }
        }
    }

    public static String getMIMEType(String url) {
        String mType = null;
        String ext = url.substring(url.lastIndexOf(".") + 1);
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        if (mime != null) {
//            Log.d("adslog", "getMIMEType:mime.getMimeTypeFromExtension(ext); " + mime.getMimeTypeFromExtension(ext));
            mType = mime.getMimeTypeFromExtension(ext);
        }
        return mType;
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
//        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            Log.i("adslog", "openAndroidPermissionsMenu: ");
//        }
    }


}
