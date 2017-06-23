package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kimeeo.kAndroid.actions.R;

import java.io.File;

/**
 * Created by bpa001 on 6/21/17.
 */

public class ImageSet extends BaseAction {
    Download download;
    public ImageSet(Activity activity) {
        super(activity);
        chooserTitle=activity.getString(R.string._action_open_file_with);
        download=new Download(activity);
    }
    private String chooserTitle;
    public String getChooserTitle() {
        return chooserTitle;
    }
    public void setChooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
    }
    public ImageSet chooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
        return this;
    }



    public void setWithUri(Uri uri,String appID) {}
    public void setWithUri(Uri uri,String appID,String filePath) {
        try
        {
            activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            if(filePath==null || filePath.equalsIgnoreCase("")) {
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("mimeType", "image/*");
            }
            else {
                intent.setDataAndType(uri,  Download.getMimeType(filePath));
                intent.putExtra("mimeType", Download.getMimeType(filePath));
            }

            if(appID!=null)
                intent.setPackage(appID);

            activity.startActivity(Intent.createChooser(intent, getChooserTitle()));
        }catch (Exception e){}
    }
    public void setWithUri(Uri uri) {
        setWithUri(uri,"");
    }
    public void setWithLink(final  String link,final  String appID) {
        DownloadFileAsync.DownloadWatcher localDownloadWatcher=new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {

            }

            @Override
            public void onProgressUpdate(int progressVal) {

            }

            @Override
            public void success(File file) {
                setWithFile(file,appID);
            }

            @Override
            public void fail(Object file) {

            }
        };
        download.getDownoladConfig().setWatcher(localDownloadWatcher);
        download.startWithDefaultConfig(link);
    }
    public void setWithLink(final  String link) {
        DownloadFileAsync.DownloadWatcher localDownloadWatcher=new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {

            }

            @Override
            public void onProgressUpdate(int progressVal) {

            }

            @Override
            public void success(File file) {
                setWithFile(file,null);
            }

            @Override
            public void fail(Object file) {

            }
        };
        download.getDownoladConfig().setWatcher(localDownloadWatcher);
        download.startWithDefaultConfig(link);
    }
    public void setWithDownoladConfig(final  Download.DownoladConfig downoladConfig,final  String appID) {
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
                setWithFile(file,appID);
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcher!=null)
                    downloadWatcher.fail(file);
            }
        };
        downoladConfig.setWatcher(localDownloadWatcher);
        download.startWithConfig(downoladConfig);
    }
    public void setWithDownoladConfig(final  Download.DownoladConfig downoladConfig) {
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
                setWithFile(file,null);
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcher!=null)
                    downloadWatcher.fail(file);
            }
        };
        downoladConfig.setWatcher(localDownloadWatcher);
        download.startWithConfig(downoladConfig);
    }
    public void setWithFile(File file,String appID) {
        Uri uri=null;
        if(file!=null)
            uri=Uri.fromFile(file);
        setWithUri(uri,appID,file.getPath());
    }
    public void setWithFile(File file) {
        Uri uri=null;
        if(file!=null)
            uri=Uri.fromFile(file);
        setWithUri(uri,null,file.getPath());
    }

}
