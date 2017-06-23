package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.kimeeo.kAndroid.actions.R;

import java.io.File;

/**
 * Created by bpa001 on 6/21/17.
 */

public class OpenFile extends BaseAction {
    Download download;
    public OpenFile(Activity activity) {
        super(activity);
        activity.getString(R.string._action_open_file_with);
        download=new Download(activity);
    }
    private String chooserTitle;
    public String getChooserTitle() {
        return chooserTitle;
    }
    public void setChooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
    }
    public OpenFile chooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
        return this;
    }


    public void openWithLink(final  String link) {
        DownloadFileAsync.DownloadWatcher localDownloadWatcher=new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {

            }
            @Override
            public void onProgressUpdate(int progressVal) {

            }
            @Override
            public void success(File file) {
                openWithFile(file);
            }
            @Override
            public void fail(Object file) {

            }
        };
        download.getDownoladConfig().setWatcher(localDownloadWatcher);
        download.startWithDefaultConfig(link);
    }
    public void openWithDownoladConfig(Download.DownoladConfig downoladConfig) {

        final DownloadFileAsync.DownloadWatcher downloadWatcher =downoladConfig.getWatcher();
        DownloadFileAsync.DownloadWatcher localDownloadWatcher=new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {
                if(downloadWatcher!=null)
                    downloadWatcher.start();
            }

            @Override
            public void onProgressUpdate(int progressVal) {
                if(downloadWatcher!=null)
                    downloadWatcher.onProgressUpdate(progressVal);
            }

            @Override
            public void success(File file) {
                if(downloadWatcher!=null)
                    downloadWatcher.success(file);
                openWithFile(file);
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcher!=null)
                    downloadWatcher.fail(file);
            }
        };
        downoladConfig.watcher(localDownloadWatcher);
        download.startWithConfig(downoladConfig);
    }
    public void openWithFile(File file) {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), Download.getMimeType(file.getPath()));
            if(getChooserTitle()==null)
                setChooserTitle(activity.getString(R.string._action_open_file_with));
            activity.startActivity(Intent.createChooser(intent, getChooserTitle()));
        }catch (Exception e){}
    }
    public void openWithUri(Uri uri) {
        File file = new File(Download.getPath(activity,uri));
        openWithFile(file);
    }
}
