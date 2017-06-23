package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;

import com.kimeeo.kAndroid.actions.v2.BaseAction;

/**
 * Created by bpa001 on 6/21/17.
 */

public class ShowBusyDialog extends BaseAction {
    public ShowBusyDialog(Activity activity) {
        super(activity);
    }
    public void clear()
    {
        hideBusy();
        super.clear();
    }

    Runnable runnablelocal = new Runnable() {
        @Override
        public void run() {
            hideBusy();
        }
    };
    Handler handler;
    private ProgressDialog progressDialog;

    public void show(String msg) {
        showForDuration(msg,-1);
    }
    public void showForDuration(String msg, Integer duration) {
        showForDuration(msg,null,duration);
    }
    public void showForDuration(String msg,String details, Integer duration) {

        try
        {
            hideBusy();
            progressDialog= new ProgressDialog(activity);
            progressDialog.setMessage(msg);
            if(details!=null)
                progressDialog.setMessage(details);
            progressDialog.show();

            if(handler!=null)
            {
                handler.removeCallbacks(runnablelocal);
                handler = null;
            }

            if(duration!=-1)
            {
                handler = new Handler();
                handler.postDelayed(runnablelocal, duration);
            }
        }
        catch (Exception e)
        {

        }
    }


    public void hideBusy() {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
