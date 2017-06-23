package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Context;

import com.kimeeo.kAndroid.actions.v2.BaseAction;
import com.kimeeo.kAndroid.actions.v1.Toast;

/**
 * Created by bpa001 on 6/21/17.
 */

public class Copy extends BaseAction{
    public Copy(Activity activity) {super(activity);}
    public void copy(String data, String successMsg, String failMsg) {
        try
        {
            if (data != null) {
                Object clipboardobj = activity.getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardobj instanceof android.text.ClipboardManager) {
                    android.text.ClipboardManager clipboard1 = (android.text.ClipboardManager) clipboardobj;
                    clipboard1.setText(data);
                } else {
                    android.content.ClipboardManager clipboard2 = (android.content.ClipboardManager) clipboardobj;
                    int currentVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentVersion >= 11)
                        clipboard2.setText(data);
                }
                if (successMsg != null && successMsg.equals("") == false)
                    new Toast(activity).perform(successMsg);
            }
        }catch (Exception e){
            if (failMsg != null && failMsg.equals("") == false)
                new Toast(activity).perform(failMsg);

        }

    }
    public void copy(String data, String successMsg) {
        copy(data, successMsg,null);
    }
    public void copy(String data) {
        copy(data, null,null);
    }
}
