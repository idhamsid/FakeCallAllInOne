package com.javadroid.fakecall.isConfig;

import static com.javadroid.fakecall.MyApplication.mFirebaseAnalytics;
import static com.javadroid.fakecall.activity.RingtoneFrgmnt.loading;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;


public class KDDownloadFile {
    private Activity activity;
    private String url;
    private String name;
    private String downloadDirectory;
    private KDDownloadListener bsDownloadListener;
    private boolean downloadMessage;

    public KDDownloadFile(Activity activity, String url, String name,String downloadDirectory, boolean downloadMessage, KDDownloadListener bsDownloadListener) {
        this.activity = activity;
        this.url = url;
        this.name = name;
        this.downloadDirectory = downloadDirectory;
        this.bsDownloadListener = bsDownloadListener;
        this.downloadMessage = downloadMessage;
        downloadNow();
//        Log.e("adslog", "KDDownloadFile: donlod direct " + downloadDirectory);
    }

    private void downloadNow() {
        if (downloadMessage)
            loading.setLabel("Starting download...");
        else
            loading.setLabel("Please wait...");
        loading.show();
        File directory = new File(downloadDirectory);
        Log.v("adsloge", "downloadNow: directory "+directory);
        Log.v("adsloge", "downloadNow: url "+url);
        Log.v("adsloge", "downloadNow: name+url.substring(url.lastIndexOf(\".\")) "+name+url.substring(url.lastIndexOf(".")));
        Bundle params = new Bundle();
        params.putString("directory", String.valueOf(directory));
        params.putString("namaFile", name+url.substring(url.lastIndexOf(".")));
        FirebaseCrashlytics.getInstance().log("url "+url);
        FirebaseCrashlytics.getInstance().log("directory "+directory);
        FirebaseCrashlytics.getInstance().log("mFile "+name+url.substring(url.lastIndexOf(".")));
        mFirebaseAnalytics.logEvent("log", params);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        PRDownloader.download(url, downloadDirectory, name+url.substring(url.lastIndexOf(".")))
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
//                        Log.i("adslog", "onProgress: "+(progress.currentBytes/progress.totalBytes)*100 );
//                        loading.setLabel("Download " + url.substring(url.lastIndexOf('/') + 1) + Integer.parseInt(progress.toString()) + " %");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (downloadMessage)
                            Toast.makeText(activity, "Download finished", Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                        bsDownloadListener.onFinish(downloadDirectory, name+url.substring(url.lastIndexOf(".")));
                    }

                    @Override
                    public void onError(Error error) {
                        Log.i("adsloge", "onError: isConnectionError " + error.isConnectionError());
                        Log.i("adsloge", "onError: getConnectionException().getLocalizedMessage() " + error.getConnectionException().getLocalizedMessage());
                        Log.i("adsloge", "onError: getConnectionException().getMessage() " + error.getConnectionException().getMessage());
                        Log.i("adsloge", "onError: error.getServerErrorMessage() " + error.getServerErrorMessage());
                        Log.v("adsloge", "onError: downloadDirectory " + downloadDirectory);
                        Log.v("adsloge", "onError: url " + url);
                        Log.v("adsloge", "onError: name " + name);
                        Log.w("adsloge", "onError: url.substring(url.lastIndexOf('/') + 1) " + url.substring(url.lastIndexOf('/') + 1));
                        bsDownloadListener.onFinish(downloadDirectory, url.substring(url.lastIndexOf('/') + 1));
                        if (error.getServerErrorMessage() != null)
                            Toast.makeText(activity, error.getServerErrorMessage(), Toast.LENGTH_SHORT).show();
                        loading.dismiss();
                    }
                });
    }

    public static class Builder {
        private Activity activity;
        private String url;
        private String name;
        private String downloadDirectory;
        private KDDownloadListener bsDownloadListener;
        private boolean downloadMessage;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDirectory(String downloadDirectory) {
            this.downloadDirectory = downloadDirectory;
            return this;
        }

        public Builder setListener(KDDownloadListener bsDownloadListener) {
            this.bsDownloadListener = bsDownloadListener;
            return this;
        }

        public Builder setDownloadMessage(Boolean downloadMessage) {
            this.downloadMessage = downloadMessage;
            return this;
        }

        public KDDownloadFile download() {
            return new KDDownloadFile(activity, url,name, downloadDirectory, downloadMessage, bsDownloadListener);
        }
    }
}
