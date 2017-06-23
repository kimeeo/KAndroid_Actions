package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;

import com.kimeeo.kAndroid.actions.v2.BaseAction;
import com.kimeeo.kAndroid.actions.R;
import com.kimeeo.kAndroid.actions.v1.*;

/**
 * Created by bpa001 on 6/21/17.
 */

public class OpenApp extends BaseAction {
    private String title;
    private String downloadMSG;

    private String yes;
    private String no;
    public String getYes() {
        return yes;
    }
    public void setYes(String yes) {
        this.yes = yes;
    }
    public OpenApp yes(String yes) {
        this.yes = yes;
        return this;
    }
    public OpenApp yesRes(@StringRes int yesRes) {
        this.yes = activity.getString(yesRes);
        return this;
    }
    public String getNo() {
        return no;
    }
    public void setNo(String no) {
        this.no = no;
    }
    public OpenApp no(String no) {
        this.no = no;
        return this;
    }
    public OpenApp noRes(@StringRes int noRes) {
        this.no = activity.getString(noRes);
        return this;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public OpenApp title(String title) {
        this.title = title;
        return this;
    }
    public OpenApp titleRes(int titleRes) {
        this.title = activity.getString(titleRes);
        return this;
    }

    public String getDownloadMSG() {
        return downloadMSG;
    }
    public void setDownloadMSG(String downloadMSG) {
        this.downloadMSG = downloadMSG;
    }
    public OpenApp downloadMSG(String downloadMSG) {
        this.downloadMSG = downloadMSG;
        return this;
    }
    public OpenApp downloadMSGRes(int downloadMSGRes) {
        this.downloadMSG = activity.getString(downloadMSGRes);
        return this;
    }


    public OpenApp(Activity activity) {
        super(activity);
    }


    private String playStoreURL="https://play.google.com/store/apps/details?id=";
    public String getPlayStoreURL() {
        return playStoreURL;
    }
    public void setPlayStoreURL(String playStoreURL) {
        this.playStoreURL = playStoreURL;
    }


    public void open(String classPath, boolean offerDownloadOption, String appName) {
        if(classPath!=null) {
            try {
                PackageManager manager = activity.getPackageManager();
                Intent i  = manager.getLaunchIntentForPackage(classPath);
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                activity.startActivity(i);
            } catch (Exception e) {
                if (offerDownloadOption)
                    downloadApp(classPath, appName);
            }
        }
    }
    public void open(String classPath){
        open(classPath,false,null);
    }



    public void downloadApp(final String classPath,final String appName) {
        new AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getTitle())
                .setMessage(appName +"("+classPath+")" +getDownloadMSG())
                .setPositiveButton(getYes(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String link = getPlayStoreURL()+classPath;
                        openURL(link);
                    }
                })
                .setNegativeButton(getNo(), null)
                .show();

    }
    protected void openURL(String link) {
        new OpenBrowser(activity).open(link);
    }
}
