package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kimeeo.kAndroid.actions.v2.BaseAction;

/**
 * Created by bpa001 on 6/21/17.
 */

public class Phone extends BaseAction {
    public Phone(Activity activity) {
        super(activity);
    }
    public void dial(String phoneNo) {
        try
        {
            if(phoneNo!=null) {
                String link = "tel:" + phoneNo;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                activity.startActivity(browserIntent);
            }
        }
        catch (Exception e){}
    }
}
