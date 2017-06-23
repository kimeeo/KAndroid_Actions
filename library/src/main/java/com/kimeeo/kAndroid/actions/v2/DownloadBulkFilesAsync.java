package com.kimeeo.kAndroid.actions.v2;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpa001 on 6/21/17.
 */

public class DownloadBulkFilesAsync extends AsyncTask<DownloadBulkFilesAsync.DownloadFile, String, List<File>> {

    DownloadWatcher downloadWatcher;
    public DownloadBulkFilesAsync(DownloadWatcher downloadWatcher) {
        this.downloadWatcher=downloadWatcher;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(downloadWatcher!=null)
            downloadWatcher.start();
    }
    @Override
    protected List<File> doInBackground(DownloadFile... list) {
        int count;
        try {
            List<File> files = new ArrayList<>();
            for (int i = 0; i < list.length; i++) {
                URL url = new URL(list[i].getUrl());
                URLConnection connection= url.openConnection();
                connection.connect();
                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                if(!list[i].getTarget().exists())
                    list[i].getTarget().createNewFile();
                OutputStream output = new FileOutputStream(list[i].getTarget());
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1)
                {
                    total += count;
                    int totalProgress= 100/list.length;
                    int totalDone = i*totalProgress;

                    int percentage=(int)((total*100)/lenghtOfFile);
                    totalDone +=percentage/totalProgress;

                    publishProgress(""+totalDone);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
                files.add(list[i].getTarget());
            }
            return files;

        } catch (Exception e) {}
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
    protected void onPostExecute(List<File> files) {
        if(downloadWatcher!=null) {
            if (files!=null)
                downloadWatcher.success(files);
            else
                downloadWatcher.fail(null);
        }
    }
    public static class DownloadFile {
        protected String url;
        protected File target;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public File getTarget() {
            return target;
        }

        public void setTarget(File target) {
            this.target = target;
        }

        public DownloadFile(){}
        public DownloadFile(String url,File target)
        {
            this.url=url;
            this.target=target;
        }

    }
    public static interface DownloadWatcher {
        void start();
        void onProgressUpdate(int progressVal);
        void success(List<File> files);
        void fail(Object file);
    }

}
