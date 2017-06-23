package com.kimeeo.kAndroid.actions.v2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.gun0912.tedpermission.PermissionListener;
import com.kimeeo.kAndroid.actions.v2.BaseAction;
import com.kimeeo.kAndroid.actions.R;

import java.util.ArrayList;

/**
 * Created by bpa001 on 6/21/17.
 */

public class SMS extends BaseAction {
    private String costMessage;
    private OnFinish onFinish;
    private String costTitle;
    private String yes;
    private String no;
    public SMS(Activity activity) {
        super(activity);
        costTitle=activity.getString(R.string._mayCostSMSChargeTitle);
        costMessage=activity.getString(R.string._mayCostSMSCharge);
        yes=activity.getString(R.string._yesClose);
        no=activity.getString(R.string._noClose);
    }
    public String getCostTitle() {
        return costTitle;
    }
    public void setCostTitle(String costTitle) {
        this.costTitle = costTitle;
    }
    public SMS costTitle(String costTitle) {
        this.costTitle = costTitle;
        return this;
    }
    public SMS costTitleRes(@StringRes int costTitleRes) {
        this.costTitle = activity.getString(costTitleRes);
        return this;
    }
    public String getYes() {
        return yes;
    }
    public void setYes(String yes) {
        this.yes = yes;
    }
    public SMS yes(String yes) {
        this.yes = yes;
        return this;
    }
    public SMS yesRes(@StringRes int yesRes) {
        this.yes = activity.getString(yesRes);
        return this;
    }
    public String getNo() {
        return no;
    }
    public void setNo(String no) {
        this.no = no;
    }
    public SMS no(String no) {
        this.no = no;
        return this;
    }
    public SMS noRes(@StringRes int noRes) {
        this.no = activity.getString(noRes);
        return this;
    }
    public String getCostMessage() {
        return costMessage;
    }
    public void setCostMessage(String costMessage) {
        this.costMessage = costMessage;
    }
    public SMS costMessage(String costMessage) {
        this.costMessage = costMessage;
        return this;
    }
    public SMS costMessageRes(@StringRes int costMessageRes) {
        this.costMessage = activity.getString(costMessageRes);
        return this;
    }
    public SMS onFinish(OnFinish onFinish) {
        this.onFinish=onFinish;
        return this;
    }


    public void send(final String recipient,final String message) {
        PermissionListener listener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted() {
                sendWithPermission(recipient, message,false);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                if(onFinish!=null)
                    onFinish.onPermissionDenied(recipient,message);
            }
        };
        invokePermission(listener);
    }
    public void sendWithFallback(final String recipient,final String message) {
        PermissionListener listener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted() {
                sendWithPermission(recipient, message,true);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                new SMSComposer(activity).open(recipient, message);
                if(onFinish!=null)
                    onFinish.onPermissionDenied(recipient,message);
            }
        };
        invokePermission(listener);
    }

    public void sendWithFallback(final String recipient,final String message,final boolean withFallback) {
        PermissionListener listener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted() {
                sendWithPermission(recipient, message,withFallback);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                new SMSComposer(activity).open(recipient, message);
                if(onFinish!=null)
                    onFinish.onPermissionDenied(recipient,message);
            }
        };
        invokePermission(listener);
    }

    public void sendWithCostAlert(final String recipient, final String message) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences("SMS_SETTINGS", Context.MODE_PRIVATE);
        boolean doNotAskAgain = sharedPreferences.getBoolean("doNotAskAgain", false);
        boolean allow = sharedPreferences.getBoolean("allow", false);
        if (doNotAskAgain == false) {
            View checkBoxView = View.inflate(activity, R.layout._sms_don_not_ask_again, null);
            CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("doNotAskAgain", isChecked).commit();
                }
            });

            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getCostTitle())
                    .setMessage(getCostMessage())
                    .setView(checkBoxView)
                    .setCancelable(false)
                    .setPositiveButton(getYes(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("allow", true).commit();
                            send(recipient, message);
                        }
                    })
                    .setNegativeButton(getNo(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("allow", false).commit();
                            if (onFinish != null)
                                onFinish.fail(recipient, message);
                        }
                    })
                    .show();
        } else if (doNotAskAgain && allow)
            send(recipient, message);
    }
    public void sendWithCostAlertWithFallBack(final String recipient, final String message) {
        final SharedPreferences sharedPreferences = activity.getSharedPreferences("SMS_SETTINGS", Context.MODE_PRIVATE);
        boolean doNotAskAgain = sharedPreferences.getBoolean("doNotAskAgain", false);
        final boolean allow = sharedPreferences.getBoolean("allow", false);
        if (doNotAskAgain == false) {
            View checkBoxView = View.inflate(activity, R.layout._sms_don_not_ask_again, null);
            CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPreferences.edit().putBoolean("doNotAskAgain", isChecked).commit();
                }
            });

            new AlertDialog.Builder(activity)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getCostTitle())
                    .setMessage(getCostMessage())
                    .setView(checkBoxView)
                    .setCancelable(false)
                    .setPositiveButton(getYes(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("allow", true).commit();
                            sendWithFallback(recipient, message);
                        }
                    })
                    .setNegativeButton(getNo(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putBoolean("allow", false).commit();
                            if (onFinish != null)
                                onFinish.fail(recipient, message);
                            new SMSComposer(activity).open(recipient, message);
                        }
                    })
                    .show();
        } else if (doNotAskAgain && allow)
            send(recipient, message);
    }

    protected void sendWithPermission(String recipient, String message,boolean sendWithFallback) {
        try {
            if(recipient!=null && message!=null) {
                SmsManager manager = SmsManager.getDefault();
                PendingIntent sentIntent = PendingIntent.getActivity(activity, 0, new Intent(), 0);
                String msg = message;
                if(msg==null)
                    msg="";
                manager.sendTextMessage(recipient, null, msg, sentIntent, null);
                if(onFinish!=null)
                    onFinish.success(recipient,message);
            }
        }
        catch (Exception e)
        {
            if(sendWithFallback)
                new SMSComposer(activity).open(recipient, message);

            if(onFinish!=null)
                onFinish.fail(recipient,message);
        }
    }

    @Override
    public String[] requirePermissions() {
        return new String[]{Manifest.permission.SEND_SMS};
    }
    @Override
    public String[] getFriendlyPermissionsMeaning() {return new String[]{"Send SMS"};}
    public interface OnFinish {
        void success(String recipient,String message);
        void fail(String recipient,String message);
        void onPermissionDenied(String recipient,String message);
    }
}
