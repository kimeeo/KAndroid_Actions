package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kimeeo.kAndroid.actions.v2.BaseAction;

/**
 * Created by bpa001 on 6/21/17.
 */

public class SMSComposer extends BaseAction {
    public SMSComposer(Activity activity) {
        super(activity);
    }
    public void open(String recipient)
    {
        open(recipient,null);
    }
    public void open(String recipient,String message) {
        if(recipient!=null)
        {
            String uri = "smsto:" + recipient;
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
            intent.putExtra("compose_mode", true);
            if (message != null)
                intent.putExtra("sms_body", message);
            activity.startActivity(intent);
        }
    }
}
