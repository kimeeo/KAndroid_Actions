package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.kimeeo.kAndroid.actions.R;

import java.io.File;

/**
 * Created by bpa001 on 6/21/17.
 */

public class Share extends BaseAction {
    Download download;
    public Share(Activity activity) {
        super(activity);
        chooserTitle=activity.getString(R.string.text_share_with);
        download=new Download(activity);
    }
    private String chooserTitle;
    public String getChooserTitle() {
        return chooserTitle;
    }
    public void setChooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
    }
    public Share chooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
        return this;
    }

    public void shareSimpleText(String data,String subject) {
        share(data,subject,false,null,"");
    }
    public void shareHTMLText(String data,String subject) {
        share(data,subject,true,null,"");
    }
    public void shareSimpleTextWithAPP(String appID,String data,String subject) {
        share(data,subject,false,appID,"");
    }
    public void shareHTMLTextWithAPP(String appID,String data,String subject) {
        share(data,subject,true,appID,"");
    }
    public void shareFile(File file,String data,String subject,boolean isHTML) {
        share(data,subject,isHTML,null,file);
    }
    public void shareLink(String link,String data,String subject,boolean isHTML) {
        share(data,subject,isHTML,null,link);
    }
    public void shareFileWithApp(File file,String appID,String data,String subject,boolean isHTML) {
        share(data,subject,isHTML,appID,file);
    }
    public void shareLinkWithApp(String link,String appID,String data,String subject,boolean isHTML) {
        share(data,subject,isHTML,appID,link);
    }
    public void share(final String data,final  String subject,final  boolean isHTML,final  String shareWith,final  String link) {
        DownloadFileAsync.DownloadWatcher localDownloadWatcher=new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {

            }

            @Override
            public void onProgressUpdate(int progressVal) {

            }

            @Override
            public void success(File file) {
                share(data,subject,isHTML,shareWith,file);
            }

            @Override
            public void fail(Object file) {

            }
        };
        download.getDownoladConfig().setWatcher(localDownloadWatcher);
        download.startWithDefaultConfig(link);
    }
    public void share(final String data,final  String subject,final  boolean isHTML,final  String shareWith,final  Download.DownoladConfig downoladConfig) {

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
                share(data,subject,isHTML,shareWith,file);
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
    public void share(String data, String subject, boolean isHTML, String shareWith, File file) {
        if(data!=null && data.equals("")==false)
        {
            Uri uri=null;
            if(file!=null)
                uri=Uri.fromFile(file);
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(activity);
            if(uri==null)
                intentBuilder.setType("text/plain");
            else
            {
                intentBuilder.setStream(uri);
                intentBuilder.setType(Download.getMimeType(file.getPath()));
            }

            intentBuilder.setChooserTitle(chooserTitle);

            if (subject != null)
                intentBuilder.setSubject(subject);

            if(data.indexOf("<a href")!=-1)
                isHTML=true;

            if (data != null && isHTML)
                intentBuilder.setHtmlText(data);
            else if (data != null)
                intentBuilder.setText(data);

            if(shareWith!=null)
                intentBuilder.getIntent().setPackage(shareWith);
            intentBuilder.startChooser();
        }
    }
    public void share(String data, String subject, boolean isHTML, String shareWith, Uri uri) {
        if(data!=null && data.equals("")==false)
        {
            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(activity);
            if(uri==null)
                intentBuilder.setType("text/plain");
            else
                intentBuilder.setStream(uri);

            intentBuilder.setChooserTitle(chooserTitle);

            if (subject != null)
                intentBuilder.setSubject(subject);

            if(data.indexOf("<a href")!=-1)
                isHTML=true;

            if (data != null && isHTML)
                intentBuilder.setHtmlText(data);
            else if (data != null)
                intentBuilder.setText(data);

            if(shareWith!=null)
                intentBuilder.getIntent().setPackage(shareWith);
            intentBuilder.startChooser();
        }
    }
}
