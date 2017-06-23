package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;

import com.kimeeo.kAndroid.actions.v2.BaseAction;

/**
 * Created by bpa001 on 6/21/17.
 */

public class Toast extends BaseAction {
    public Toast(Activity activity) {
        super(activity);
    }
    public void showForLongDuration(String msg) {
        showForDuration(msg, android.widget.Toast.LENGTH_LONG);
    }
    public void showForShortDuration(String msg) {
        showForDuration(msg, android.widget.Toast.LENGTH_SHORT);
    }
    public void showForDuration(String msg,int duration) {
        try{
            if(msg!=null && msg.equals("")==false)
                android.widget.Toast.makeText(activity, msg, duration).show();
        }
        catch (Exception e){}
    }
    public void show(String msg) {
        showForDuration(msg, android.widget.Toast.LENGTH_SHORT);
    }
}
