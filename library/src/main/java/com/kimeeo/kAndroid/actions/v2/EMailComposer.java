package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v4.app.ShareCompat;

import com.kimeeo.kAndroid.actions.v2.BaseAction;
import com.kimeeo.kAndroid.actions.R;

import java.io.File;

/**
 * Created by bpa001 on 6/21/17.
 */

public class EMailComposer extends BaseAction {
    private String chooserTitle;
    public EMailComposer(Activity activity) {
        super(activity);
        chooserTitle=activity.getString(R.string._mail_using_app);
    }
    public String getChooserTitle() {
        return chooserTitle;
    }
    public void setChooserTitle(String chooserTitle) {
        this.chooserTitle = chooserTitle;
    }
    public EMailComposer chooserTitle(String chooserTitle) {
        this.chooserTitle=chooserTitle;
        return this;
    }
    public EMailComposer chooserTitleRes(@StringRes int chooserTitleRes) {
        this.chooserTitle=activity.getString(chooserTitleRes);
        return this;
    }
    public void open(String url) {
        try
        {
            String link=url;
            if(url.startsWith("mailto:")==false)
                link="mailto:"+url;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            activity.startActivity(browserIntent);
        }
        catch (Exception e){}
    }
    public void open(String[] to,String subject,String body, boolean isHTML, String[] cc, String[] bcc,File[] attachment){
        openWithApp(null,to,subject,body,isHTML,cc,bcc,attachment);
    }
    public void open(String[] to,String subject,String body, boolean isHTML, String[] cc,File[] attachment){
        openWithApp(null,to,subject,body,isHTML,cc,null,attachment);
    }
    public void open(String[] to,String subject,String body, boolean isHTML, String[] cc, String[] bcc){
        openWithApp(null,to,subject,body,isHTML,cc,bcc,null);
    }
    public void open(String[] to,String subject,String body, boolean isHTML){
        openWithApp(null,to,subject,body,isHTML,null,null,null);
    }
    public void open(String[] to,String subject, String body){
        openWithApp(null,to,subject,body,false,null,null,null);
    }
    public void open(String[] to,String subject){
        openWithApp(null,to,subject,null,false,null,null,null);
    }
    public void openWithApp(String appID,String[] to,String subject,String body, boolean isHTML, String[] cc, String[] bcc, File[] attachment){
        try
        {

            ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(activity);

            if(attachment!=null && attachment.length!=0) {
                if(attachment.length==1)
                {
                    Uri uri = Uri.fromFile(attachment[0]);
                    activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intentBuilder.setStream(uri);
                }
                else {
                    for (int i = 0; i < attachment.length; i++) {
                        Uri uri = Uri.fromFile(attachment[i]);
                        activity.grantUriPermission(activity.getPackageName(), uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intentBuilder.addStream(uri);
                    }
                }
            }
            intentBuilder.setType("text/plain");
            intentBuilder.setChooserTitle(getChooserTitle());

            if (subject != null)
                intentBuilder.setSubject(subject);

            if(to!=null && to.length!=0)
                intentBuilder.setEmailTo(to);

            if(cc!=null && cc.length!=0)
                intentBuilder.setEmailCc(cc);

            if(bcc!=null && bcc.length!=0)
                intentBuilder.setEmailCc(bcc);

            if(body!=null && body.indexOf("<a href")!=-1)
                isHTML=true;

            if (body != null && isHTML)
                intentBuilder.setHtmlText(body);
            else if (body != null)
                intentBuilder.setText(body);

            if(appID!=null)
                intentBuilder.getIntent().setPackage(appID);

            intentBuilder.startChooser();
        }
        catch (Exception e)
        {

        }
    }
}
