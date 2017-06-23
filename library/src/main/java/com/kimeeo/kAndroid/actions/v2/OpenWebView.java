package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by bpa001 on 6/23/17.
 */

public class OpenWebView extends BaseAction {
    public OpenWebView(Activity activity) {
        super(activity);
    }
    public void open(String url,String title)
    {
        activity.startActivity(new Intent(getActivity(),WebViewActivity.class).putExtra(WebViewActivity.URL,url).putExtra(WebViewActivity.TITLE,title));
    }
    public void open(String url)
    {
        activity.startActivity(new Intent(getActivity(),WebViewActivity.class).putExtra(WebViewActivity.URL,url));
    }
    public void open(Class clazz,String url,String title)
    {
        activity.startActivity(new Intent(getActivity(),clazz).putExtra(WebViewActivity.URL,url).putExtra(WebViewActivity.TITLE,title));
    }
    public void open(Class clazz,String url)
    {
        activity.startActivity(new Intent(getActivity(),clazz).putExtra(WebViewActivity.URL,url));
    }

}
