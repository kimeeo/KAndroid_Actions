package com.kimeeo.kAndroid.actions.v2;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by bpa001 on 6/21/17.
 */

public class DownloadFileAsync extends AsyncTask<String, String, String> {

    File target;
    DownloadWatcher downloadWatcher;
    public DownloadFileAsync(File target, DownloadWatcher downloadWatcher) {
        this.target = target;
        this.downloadWatcher=downloadWatcher;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(downloadWatcher!=null)
            downloadWatcher.start();
    }
    @Override
    protected String doInBackground(String... urls) {
        int count;
        try {
            URL url = new URL(urls[0]);
            URLConnection connection= url.openConnection();
            connection.connect();
            int lenghtOfFile = connection.getContentLength();
            InputStream input = new BufferedInputStream(url.openStream());
            if(!target.exists())
                target.createNewFile();
            OutputStream output = new FileOutputStream(target);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1)
            {
                total += count;
                publishProgress(""+(int)((total*100)/lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;

    }
    @Override
    protected void onProgressUpdate(String... progress) {
        if(downloadWatcher!=null) {
            int progressVal = Integer.parseInt(progress[0]);
            downloadWatcher.onProgressUpdate(progressVal );
        }
    }

    @Override
    protected void onPostExecute(String unused) {
            if(downloadWatcher!=null) {
            if (target != null && target.exists())
                downloadWatcher.success(target);
            else
                downloadWatcher.fail(null);
        }
    }
    public static interface DownloadWatcher {
        void start();
        void onProgressUpdate(int progressVal);
        void success(File file);
        void fail(Object file);
    }

}
