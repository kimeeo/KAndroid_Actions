package com.kimeeo.kAndroid.actions.v1;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.gun0912.tedpermission.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bhavinpadhiyar on 3/7/16.
 */
public class Download extends BaseAction {

    public Download(Activity activity) {
        super(activity);
    }
    private String folderLocation=null;
    private int progressBarType=PROGRESS_BAR_TYPE_NOTIFICATION;
    public static int PROGRESS_BAR_TYPE_NOTIFICATION=1;
    public static int PROGRESS_BAR_TYPE_POPUP=2;
    private Map<Integer,ProgressDialog> progressDialogMap=new HashMap<>();

    private File downloadFolder;
    public File getDownloadFolder() {
        if(downloadFolder==null)
            downloadFolder = Environment.getExternalStorageDirectory();
        return downloadFolder;
    }
    public void setDownloadFolder(File downloadFolder) {
        this.downloadFolder = downloadFolder;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String title="Downloading";
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }

    private String details="Downloading is in progress";

    public Download(Activity activity,String folderLocation)
    {
        super(activity);
        setFolderLocation(folderLocation);
    }

    public String getFolderLocation() {
        if(folderLocation==null)
            folderLocation = activity.getPackageName()+"/data";
        return folderLocation;
    }
    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
    }

    public int getProgressBarType() {
        return progressBarType;
    }
    public void setProgressBarType(int type) {
        progressBarType=type;
    }

    public void clear()
    {
        if(progressDialogMap!=null) {
            for (Map.Entry<Integer, ProgressDialog> entry : progressDialogMap.entrySet()) {
                entry.getValue().dismiss();
                progressDialogMap.remove(entry.getKey());
            }
        }
        downloadFolder=null;
        progressDialogMap=null;
        super.clear();
    }
    public void perform(String link,String location, boolean showProgress,final String success,final String fail,final DownloadResult downloadResult) {
        perform(link, location, showProgress, new DownloadResult() {
            public void success(File file) {
                if (downloadResult != null)
                    downloadResult.success(file);
                if (success != null)
                    new Toast(activity).perform(success);
            }

            public void fail(Object data) {
                if (downloadResult != null)
                    downloadResult.fail(data);
                if (fail != null)
                    new Toast(activity).perform(fail);
            }
        });
    }

    public void perform(String link,String location,Boolean showProgress, DownloadResult downloadResult)
    {
        String fileName =System.currentTimeMillis()+"";
        try
        {
            fileName = link.substring(link.lastIndexOf("/")+1, link.length());
            if(fileName.indexOf(".")==-1)
                fileName=fileName+"_"+System.currentTimeMillis()+"";
        }catch (Exception e)
        {
            fileName =System.currentTimeMillis()+"";
        }
        perform(link, location, showProgress, downloadResult, fileName);
    }
    public void perform(String link,String location,Boolean showProgress, DownloadResult downloadResult,String fileName)
    {
        perform(link,location,null,showProgress, downloadResult,fileName);
    }
    public void perform(final String link,final String location,final File targetFolder,final Boolean showProgress,final DownloadResult downloadResult,final String fileName)
    {
        PermissionListener permissionListener = new PermissionListener()
        {
            @Override
            public void onPermissionGranted() {
                permissionGranted(link, location, targetFolder, showProgress, downloadResult, fileName);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                downloadResult.fail(arrayList);
            }
        };
        invokePermission(permissionListener);
    }
    @Override
    public String[] requirePermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }
    public String[] getFriendlyPermissionsMeaning() {return new String[]{"Storage"};}

    public void permissionGranted(final String link, String location, File targetFolder,final Boolean showProgress,final DownloadResult downloadResult,final String fileName)
    {
        try
        {
            if(location==null || location.equals(""))
                location=getFolderLocation();

            if(location.startsWith("/"))
                location=location.substring(1);

            if(targetFolder==null)
                targetFolder = getDownloadFolder();

            File file = new File(targetFolder.getPath()+"/"+location+"/"+fileName);
            final File target = new File(targetFolder, location+"/"+fileName);

            if (file.exists())
                downloadResult.success(file);
            else {
                new DownloadFileAsync(target,downloadResult,showProgress).execute(link);
            }
        }
        catch (Exception e){}
    }


    public int showNotificationProgress(String title,String text)
    {
        int id =generateId();
        if(getProgressBarType()==PROGRESS_BAR_TYPE_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
            mBuilder.setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(getActivityIcon(activity, activity.getPackageName()));

            mBuilder.setProgress(0, 0, true);
            mBuilder.setOngoing(true);
            notificationManager.notify(id, mBuilder.build());
        }
        else if(getProgressBarType()==PROGRESS_BAR_TYPE_POPUP)
        {
            ProgressDialog progressDialog= new ProgressDialog(activity);
            progressDialog.setTitle(title+"");
            progressDialog.setMessage(text + "");
            progressDialog.setCancelable(false);
            progressDialog.show();
            progressDialogMap.put(id,progressDialog);
        }
        return id;
    }
    public void hideNotificationProgress(int id)
    {
        if(getProgressBarType()==PROGRESS_BAR_TYPE_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
        else if(getProgressBarType()==PROGRESS_BAR_TYPE_POPUP)
        {
            ProgressDialog  progressDialog =progressDialogMap.remove(id);
            if(progressDialog!=null)
                progressDialog.dismiss();
        }
    }
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    public static int generateId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    public Bitmap getActivityIcon(Context context,String packageName) {

        try
        {
            Drawable drawable=context.getPackageManager().getApplicationIcon(packageName);
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }catch (Exception e)
        {

        }
        return null;
    }



    public static interface DownloadResult
    {
        void success(File file);
        void fail(Object file);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, File file)
    {
        return getPath(context,Uri.fromFile(file));
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri)
    {

        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection,String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    class DownloadFileAsync extends AsyncTask<String, String, String> {

        private boolean showProgress;
        File target;
        DownloadResult downloadResult;
        int showProgressID=-1;
        public DownloadFileAsync(File target,DownloadResult downloadResult,boolean showProgress)
        {
            this.target = target;
            this.downloadResult=downloadResult;
            this.showProgress = showProgress;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(showProgress)
                showProgressID=showNotificationProgress(getTitle(),getDetails());

        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                int lenghtOfFile = conexion.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(target);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {

            }
            return null;

        }
        protected void onProgressUpdate(String... progress) {
            int progressVal = Integer.parseInt(progress[0]);
        }

        @Override
        protected void onPostExecute(String unused) {

            if (showProgress && showProgressID!=-1)
                hideNotificationProgress(showProgressID);

            if (target != null && target.exists())
                downloadResult.success(target);
            else
                downloadResult.fail(null);
        }
    }

}
