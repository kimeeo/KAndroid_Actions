package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.kimeeo.kAndroid.actions.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String URL ="url";
    public static final String TITLE ="title";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        //Get webview
        webView = (WebView) findViewById(R.id.webViewOpenWebPage);
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        String title;
        if(getIntent().getExtras().containsKey(TITLE))
        {
            title = getIntent().getExtras().getString(TITLE);
        }
        else if(getIntent().getExtras().containsKey(URL))
        {
            title = getIntent().getExtras().getString(URL);
        }
        else
            title=" ";
        getSupportActionBar().setTitle(title);

        if(getIntent().getExtras().containsKey(URL))
        {
            String webLink = getIntent().getExtras().getString(URL);
            startWebView((webLink != null)?webLink:"");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onViewCreated();
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout._action_activity_web_view;
    }

    protected void onViewCreated() {

    }

    //private Button button;
    private WebView webView;

    private void startWebView(String url) {

        //Create new webview Client to show progress dialog
        //When opening a url or click on link


        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(url);
    }

    // Open previous opened link from history on webview when back button pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        webView.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}