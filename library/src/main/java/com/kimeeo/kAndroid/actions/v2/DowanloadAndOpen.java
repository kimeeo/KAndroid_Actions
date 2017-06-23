package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.kimeeo.kAndroid.actions.R;
import java.io.File;

/**
 * Created by bpa001 on 6/22/17.
 */

public class DowanloadAndOpen extends Download implements DownloadFileAsync.DownloadWatcher {
    public DowanloadAndOpen(Activity activity) {
        super(activity);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;


    public void startWithDefaultConfig(String link)
    {
        getDownoladConfig().watcher(this);
        super.startWithDefaultConfig(link);
    }
    public void startWithConfig(Download.DownoladConfig value) {
        final  DownloadFileAsync.DownloadWatcher downloadWatcherConfig=value.getWatcher();
        DownloadFileAsync.DownloadWatcher downloadWatcherLocal =new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {
                if(downloadWatcherConfig!=null)
                    downloadWatcherConfig.start();
                DowanloadAndOpen.this.start();
            }

            @Override
            public void onProgressUpdate(int progressVal) {
                if(downloadWatcherConfig!=null)
                    downloadWatcherConfig.onProgressUpdate(progressVal);
                DowanloadAndOpen.this.onProgressUpdate(progressVal);
            }

            @Override
            public void success(File file) {
                if(downloadWatcherConfig!=null)
                    downloadWatcherConfig.success(file);

                DowanloadAndOpen.this.success(file);
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcherConfig!=null)
                    downloadWatcherConfig.fail(file);
                DowanloadAndOpen.this.fail(file);
            }
        };
        value.watcher(downloadWatcherLocal);
        super.startWithConfig(value);
    }
    public void start(String link,
                      String folder,
                      File downloadToFolder,
                      Boolean showProgress,
                      final DownloadFileAsync.DownloadWatcher downloadWatcher,
                      String fileName,
                      boolean overWrite,
                      boolean uniqueName,
                      String notificationTitle,
                      String notificationDetails,
                      int progressBarType,
                      String successMsg,
                      String failMsg){
        DownloadFileAsync.DownloadWatcher downloadWatcherLocal =new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {
                if(downloadWatcher!=null)
                    downloadWatcher.start();
                DowanloadAndOpen.this.start();
            }

            @Override
            public void onProgressUpdate(int progressVal) {
                if(downloadWatcher!=null)
                    downloadWatcher.onProgressUpdate(progressVal);
                DowanloadAndOpen.this.onProgressUpdate(progressVal);
            }

            @Override
            public void success(File file) {
                if(downloadWatcher!=null)
                    downloadWatcher.success(file);

                DowanloadAndOpen.this.success(file);
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcher!=null)
                    downloadWatcher.fail(file);
                DowanloadAndOpen.this.fail(file);
            }
        };
        super.start(link,folder,downloadToFolder,showProgress,downloadWatcherLocal,fileName,overWrite,uniqueName,notificationTitle,notificationDetails,progressBarType,successMsg,failMsg);
    }

    @Override
    final public void start(){}
    @Override
    public void onProgressUpdate(int progressVal) {

    }
    @Override
    public void success(File file) {
        openFile(file);
    }
    public void openFile(File file) {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), getMimeType(file.getPath()));
            if(getTitle()==null)
                setTitle(activity.getString(R.string._action_open_file_with));
            activity.startActivity(Intent.createChooser(intent, title));
        }catch (Exception e){}
    }

    public void openFile(Uri uri) {
        File file = new File(getPath(activity,uri));
        openFile(file);
    }
        @Override
    public void fail(Object file) {}
}
