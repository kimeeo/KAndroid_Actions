package com.kimeeo.kAndroid.actions.demo;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.kimeeo.kAndroid.actions.v2.Download;
import com.kimeeo.kAndroid.actions.v2.DownloadFileAsync;
import com.kimeeo.kAndroid.actions.v2.ImageSet;
import com.kimeeo.kAndroid.actions.v2.OpenFile;
import com.kimeeo.kAndroid.actions.v2.OpenWebView;
import com.kimeeo.kAndroid.actions.v2.Share;
import com.kimeeo.kAndroid.actions.v2.WebViewActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String link="http://www.wns.com/Portals/0/Images/HeaderBanner/desktop/1933/85/India_HD.jpg";
        DownloadFileAsync.DownloadWatcher watcher= new DownloadFileAsync.DownloadWatcher() {
            @Override
            public void start() {

            }

            @Override
            public void onProgressUpdate(int progressVal) {

            }

            @Override
            public void success(File file) {

            }

            @Override
            public void fail(Object file) {

            }
        };
        Download.DownoladConfig config= new Download.DownoladConfig();
        config.link(link);
        config.progressBarType(Download.DownoladConfig.PROGRESS_BAR_TYPE_NOTIFICATION);
        config.showProgress(true);
        config.watcher(watcher);
        config.uniqueName(true);

        if (id == R.id.action_download) {
            Download download=new Download(this);
            download.startWithConfig(config);
            return true;
        }
        else if(id==R.id.action_download_open)
            new OpenFile(this).openWithDownoladConfig(config);
        else if(id==R.id.action_download_set)
            new ImageSet(this).setWithDownoladConfig(config);
        else if(id==R.id.action_download_share)
            new Share(this).share("Share Data","Subject",false,null,config);
        else if(id==R.id.action_open_web)
        {
            new OpenWebView(this).open(MyWebViewActivity.class,"http://www.google.com","ere");
        }
        return super.onOptionsItemSelected(item);
    }
    public static class MyWebViewActivity extends WebViewActivity {
        @LayoutRes
        protected int getLayoutRes() {
            return com.kimeeo.kAndroid.actions.R.layout._action_activity_web_view;
        }
        @Override
        protected void onViewCreated() {

        }
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
        }

        @Override
        protected void onRestart() {
            super.onRestart();
        }
    }
}
