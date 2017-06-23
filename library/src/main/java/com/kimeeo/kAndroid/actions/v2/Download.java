package com.kimeeo.kAndroid.actions.v2;

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
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.widget.*;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.kimeeo.kAndroid.actions.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by bpa001 on 6/21/17.
 */

public class Download extends BaseAction {
    private File defaultDownloadFolder;


    public DownoladConfig getDownoladConfig() {
        return downoladConfig;
    }

    public void setDownoladConfig(DownoladConfig downoladConfig) {
        this.downoladConfig = downoladConfig;
    }

    DownoladConfig downoladConfig;

    public DownloadFileAsync.DownloadWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(DownloadFileAsync.DownloadWatcher watcher) {
        this.watcher = watcher;
        if(downoladConfig!=null)
            downoladConfig.watcher(watcher);
    }

    DownloadFileAsync.DownloadWatcher watcher;



    private Map<Integer,ProgressDialog> progressDialogMap=new HashMap<>();

    public Download(Activity activity) {
        super(activity);
        downoladConfig =new DownoladConfig();
        downoladConfig.successMsg(activity.getString(R.string._action_download_success_msg));
        downoladConfig.failMsg(activity.getString(R.string._action_download_fail_msg));
        downoladConfig.progressBarType(DownoladConfig.PROGRESS_BAR_TYPE_NOTIFICATION);
        downoladConfig.notificationTitle(activity.getString(R.string._action_download_notification_title));
        downoladConfig.notificationDetails(activity.getString(R.string._action_download_notification_details));
        downoladConfig.uniqueName(false);
        downoladConfig.overWrite(false);
    }
    public Download downloadFolder(File downloadFolder) {
        this.defaultDownloadFolder = downloadFolder;
        return this;
    }
    public Download folderLocation(String defaultFolderPath) {
        if(defaultFolderPath==null)
            defaultFolderPath = activity.getPackageName()+"/data";
        this.defaultFolderPath = defaultFolderPath;

        return this;
    }
    public File getDefaultDownloadFolder() {
        if(defaultDownloadFolder==null)
            defaultDownloadFolder = Environment.getExternalStorageDirectory();
        return defaultDownloadFolder;
    }
    public void setDefaultDownloadFolder(File defaultDownloadFolder) {
        this.defaultDownloadFolder = defaultDownloadFolder;
        if(defaultDownloadFolder==null)
            this.defaultDownloadFolder = Environment.getExternalStorageDirectory();
    }

    private String defaultFolderPath;
    public String getDefaultFolderPath() {
        if(defaultFolderPath==null)
            defaultFolderPath = activity.getPackageName()+"/data";
        return defaultFolderPath;
    }
    public void setDefaultFolderPath(String defaultFolderPath) {
        this.defaultFolderPath = defaultFolderPath;
    }

    public void startWithDefaultConfig(String link) {
        start(link,downoladConfig.getFolder(),
                downoladConfig.getDownloadToFolder(),
                downoladConfig.getShowProgress(),
                downoladConfig.getWatcher(),
                downoladConfig.getFileName(),
                downoladConfig.isOverWrite(),
                downoladConfig.isUniqueName(),
                downoladConfig.getNotificationTitle(),
                downoladConfig.getNotificationDetails(),
                downoladConfig.getProgressBarType(),
                downoladConfig.getSuccessMsg(),
                downoladConfig.getFailMsg());
    }
    public void startWithConfig(DownoladConfig value) {

        start(value.getLink(),
                value.getFolder(),
                value.getDownloadToFolder(),
                value.getShowProgress(),
                value.getWatcher(),
                value.getFileName(),
                value.isOverWrite(),
                value.isUniqueName(),
                value.getNotificationTitle(),
                value.getNotificationDetails(),
                value.getProgressBarType(),
                value.getSuccessMsg(),
                value.getFailMsg());

    };
    public void start(final String link,
                      final String folder,
                      final File downloadToFolder,
                      final Boolean showProgress,
                      final DownloadFileAsync.DownloadWatcher downloadWatcher,
                      final String fileName,
                      final boolean overWrite,
                      final boolean uniqueName,
                      final String notificationTitle,
                      final String notificationDetails,
                      final int progressBarType,
                      final String successMsg,
                      final String failMsg){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                permissionGranted(link, folder, downloadToFolder, showProgress, downloadWatcher, fileName,overWrite,uniqueName,notificationTitle,notificationDetails,progressBarType,successMsg,failMsg);
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                if(downloadWatcher!= null)
                    downloadWatcher.fail(arrayList);
            }
        };
        invokePermission(permissionListener);
    }


    public void start(final String link,
                      final String folder,
                      final File downloadToFolder,
                      final DownloadFileAsync.DownloadWatcher downloadWatcher,
                      final boolean overWrite,
                      final boolean uniqueName){

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                permissionGranted(link,
                        folder,
                        downloadToFolder,
                        true,
                        downloadWatcher,
                        null,
                        overWrite,
                        uniqueName,
                        activity.getString(R.string._action_download_notification_title),
                        activity.getString(R.string._action_download_notification_details),
                        DownoladConfig.PROGRESS_BAR_TYPE_NOTIFICATION,
                        activity.getString(R.string._action_download_success_msg),
                        activity.getString(R.string._action_download_fail_msg));
            }
            @Override
            public void onPermissionDenied(ArrayList<String> arrayList) {
                if(downloadWatcher!= null)
                    downloadWatcher.fail(arrayList);
            }
        };
        invokePermission(permissionListener);
    }

    protected void permissionGranted(DownoladConfig value){
        permissionGranted(value.getLink(), value.getFolder(), value.getDownloadToFolder(),
                value.getShowProgress(),
                value.getWatcher(),
                value.getFileName(),
                value.isOverWrite(),
                value.isUniqueName(),
                value.getNotificationTitle(),
                value.getNotificationDetails(),
                value.getProgressBarType(),
                value.getSuccessMsg(),
                value.getFailMsg());
    }

    protected void permissionGranted(final String link,
                                  String folder,
                                  File downloadToFolder,
                                  final Boolean showProgress,
                                  final DownloadFileAsync.DownloadWatcher downloadWatcher,
                                  String fileName,
                                  boolean overWrite,
                                  boolean uniqueName,
                                  final String notificationTitle,
                                  final String notificationDetails,
                                  final int progressBarType,
                                  final String successMsg,
                                  final String failMsg){

        if(folder==null || folder.equals(""))
            folder=getDefaultFolderPath();
        if(folder.startsWith("/"))
            folder=folder.substring(1);
        if(downloadToFolder==null)
            downloadToFolder = getDefaultDownloadFolder();
        if(fileName==null)
        {
            try {
                fileName = link.substring(link.lastIndexOf("/") + 1, link.length());
                if (fileName.indexOf(".") == -1)
                    fileName = fileName + "_" + System.currentTimeMillis() + "";
            } catch (Exception e) {
                fileName = System.currentTimeMillis() + "";
            }
        }

        if(uniqueName)
            fileName = System.currentTimeMillis()+"_"+fileName;
        final DownloadFileAsync.DownloadWatcher downloadWatcherLocal=new DownloadFileAsync.DownloadWatcher(){

            int showProgressID=-1;
            @Override
            public void start() {
                if(downloadWatcher!=null)
                    downloadWatcher.start();
                if(showProgress)
                    showProgressID=showNotificationProgress(-1,0,notificationTitle,notificationDetails,progressBarType);
            }
            @Override
            public void onProgressUpdate(int progressVal) {
                if(showProgress)
                    showProgressID=showNotificationProgress(showProgressID,progressVal,notificationTitle,notificationDetails,progressBarType);
                if(downloadWatcher!=null)
                    downloadWatcher.onProgressUpdate(progressVal);
            }

            @Override
            public void success(File file) {
                if(downloadWatcher!=null)
                    downloadWatcher.success(file);

                if (showProgress && showProgressID!=-1)
                    hideNotificationProgress(showProgressID,progressBarType);

                showProgressID=-1;

                if(successMsg!=null)
                    android.widget.Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail(Object file) {
                if(downloadWatcher!=null)
                    downloadWatcher.fail(file);

                if (showProgress && showProgressID!=-1)
                    hideNotificationProgress(showProgressID,progressBarType);

                showProgressID=-1;
                if(failMsg!=null)
                    android.widget.Toast.makeText(activity, failMsg, Toast.LENGTH_SHORT).show();
            }
        };
        File targetDir = new File(downloadToFolder, folder);
        if(!targetDir.exists())
            targetDir.mkdirs();
        File target = new File(downloadToFolder, folder+"/"+fileName);
        if (target.exists() && downloadWatcher!=null && !overWrite)
            downloadWatcher.success(target);
        else if (target.exists() && overWrite)
            new DownloadFileAsync(target,downloadWatcherLocal).execute(link);
        else
            new DownloadFileAsync(target,downloadWatcherLocal).execute(link);
    }

    @Override
    public String[] requirePermissions() {
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    }
    public String[] getFriendlyPermissionsMeaning() {return new String[]{"Storage"};}
    public void clear() {
        if(progressDialogMap!=null) {
            for (Map.Entry<Integer, ProgressDialog> entry : progressDialogMap.entrySet()) {
                entry.getValue().dismiss();
                progressDialogMap.remove(entry.getKey());
            }
        }
        defaultDownloadFolder=null;
        progressDialogMap=null;
        super.clear();
    }
    public void hideNotificationProgress(int id,int progressBarType) {
        if(progressBarType==DownoladConfig.PROGRESS_BAR_TYPE_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
        else if(progressBarType==DownoladConfig.PROGRESS_BAR_TYPE_POPUP)
        {
            ProgressDialog  progressDialog =progressDialogMap.remove(id);
            if(progressDialog!=null)
                progressDialog.dismiss();
        }
    }
    public int showNotificationProgress(int id,int progress,String title,String text,int progressBarType) {
        if(id==-1)
            id =generateId();

        if(progressBarType==DownoladConfig.PROGRESS_BAR_TYPE_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.getApplicationContext().NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
            if(title!=null)
                mBuilder.setContentTitle(title);

            if(text!=null)
                mBuilder.setContentText(text);

            mBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
            mBuilder.setLargeIcon(getActivityIcon(activity, activity.getPackageName()));
            if(progress!=0)
                mBuilder.setProgress(100, progress, false);
            else
                mBuilder.setProgress(100, progress, true);

            mBuilder.setOngoing(true);
            notificationManager.notify(id, mBuilder.build());
        }
        else if(progressBarType==DownoladConfig.PROGRESS_BAR_TYPE_POPUP){
            ProgressDialog progressDialog=progressDialogMap.get(id);
            if(progressDialog==null) {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setCancelable(false);
                if(title!=null)
                    progressDialog.setTitle(title+"");

                if(text!=null)
                    progressDialog.setMessage(text + "");
                progressDialog.show();
                progressDialogMap.put(id, progressDialog);
            }
            progressDialog.setProgress(progress);


        }
        return id;
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
    public Bitmap getActivityIcon(Context context, String packageName) {

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
            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            else
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }catch (Exception e){}
        return null;
    }
    public static class DownoladConfig {
        public static int PROGRESS_BAR_TYPE_NOTIFICATION=1;
        public static int PROGRESS_BAR_TYPE_POPUP=2;

        private String link;
        private String folder;

        private File downloadToFolder;
        private Boolean showProgress=true;
        private DownloadFileAsync.DownloadWatcher watcher;
        private String fileName;
        private boolean overWrite=false;
        private boolean uniqueName=true;
        private String notificationTitle="Downloading";
        private String notificationDetails;
        private String successMsg;
        private String failMsg;
        private int progressBarType=PROGRESS_BAR_TYPE_NOTIFICATION;


        public void setWatcher(DownloadFileAsync.DownloadWatcher downloadWatcher) {
            this.watcher = downloadWatcher;
        }

        public String getFailMsg() {
            return failMsg;
        }
        public String getSuccessMsg() {
            return successMsg;
        }
        public DownoladConfig(Context context, String link)
        {
            this.link=link;
        }
        public DownoladConfig(String link)
        {
            this.link=link;
        }
        public DownoladConfig(){}
        public DownoladConfig folder(String value) {
            this.folder=value;
            return this;
        }
        public DownoladConfig link(String value) {
            this.link=value;
            return this;
        }
        public DownoladConfig downloadToFolder(File value) {
            this.downloadToFolder=value;
            return this;
        }
        public DownoladConfig showProgress(Boolean value) {
            this.showProgress=value;
            return this;
        }
        public DownoladConfig watcher(DownloadFileAsync.DownloadWatcher value) {
            this.watcher=value;
            return this;
        }
        public DownoladConfig fileName(String value) {
            this.fileName=value;
            return this;
        }
        public DownoladConfig overWrite(boolean value) {
            this.overWrite=value;
            return this;
        }
        public DownoladConfig uniqueName(boolean value) {
            this.uniqueName=value;
            return this;
        }
        public DownoladConfig successMsg(String value) {
            this.successMsg=value;
            return this;
        }
        public DownoladConfig failMsg(String value) {
            this.failMsg=value;
            return this;
        }
        public DownoladConfig notificationTitle(String value) {
            this.notificationTitle=value;
            return this;
        }
        public DownoladConfig notificationDetails(String value) {
            this.notificationDetails=value;
            return this;
        }
        public DownoladConfig progressBarType(int value) {
            if(value!=PROGRESS_BAR_TYPE_NOTIFICATION && value!=PROGRESS_BAR_TYPE_POPUP)
                value=PROGRESS_BAR_TYPE_NOTIFICATION;
            this.progressBarType=value;
            return this;
        }
        public String getLink() {
            return link;
        }
        public String getFolder() {
            return folder;
        }
        public File getDownloadToFolder() {
            return downloadToFolder;
        }
        public Boolean getShowProgress() {
            return showProgress;
        }
        public DownloadFileAsync.DownloadWatcher getWatcher() {
            return watcher;
        }
        public String getFileName() {
            return fileName;
        }
        public boolean isOverWrite() {
            return overWrite;
        }
        public boolean isUniqueName() {
            return uniqueName;
        }
        public String getNotificationTitle() {
            return notificationTitle;
        }
        public String getNotificationDetails() {
            return notificationDetails;
        }
        public int getProgressBarType() {
            return progressBarType;
        }
    }



    public static String getMimeType(String filename) {

        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        if(extension.equals("apk"))
            return "application/vnd.android.package-archive";
        else if(extension.equals("pdf"))
            return "application/pdf";
        else if(extension.equals("txt"))
            return "text/plain";
        else if(extension.equals("csv"))
            return "text/csv";
        else if(extension.equals("xml"))
            return "text/xml";
        else if(extension.equals("htm"))
            return "text/html";
        else if(extension.equals("html"))
            return "text/html";
        else if(extension.equals("php"))
            return "text/php";
        else if(extension.equals("png"))
            return "image/*";
        else if(extension.equals("gif"))
            return "image/*";
        else if(extension.equals("jpg"))
            return "image/*";
        else if(extension.equals("jpeg"))
            return "image/*";
        else if(extension.equals("bmp"))
            return "image/*";
        else if(extension.equals("mp3"))
            return "audio/mp3";
        else if(extension.equals("wav"))
            return "audio/wav";
        else if(extension.equals("ogg"))
            return "audio/x-ogg";
        else if(extension.equals("mid"))
            return "audio/mid";
        else if(extension.equals("midi"))
            return "audio/midi";
        else if(extension.equals("amr"))
            return "audio/AMR";
        else if(extension.equals("mpeg"))
            return "video/mpeg";
        else if(extension.equals("3gp"))
            return "video/3gpp";
        else if(extension.equals("mp4"))
            return "video/*";
        else if(extension.equals("jar"))
            return "application/java-archive";
        else if(extension.equals("zip"))
            return "application/zip";
        else if(extension.equals("rar"))
            return "application/x-rar-compressed";
        else if(extension.equals("gz"))
            return "application/gzip";
        return "";
    }
    public static Boolean isVideoURL(String filename) {
        String filenameArray[] = filename.split("\\.");
        if(filenameArray!=null && filenameArray.length>1)
        {
            String extension = filenameArray[filenameArray.length-1];
            if(extension.equals("wav"))
                return true;
            else if(extension.equals("ogg"))
                return true;
            else if(extension.equals("mid"))
                return true;
            else if(extension.equals("midi"))
                return true;
            else if(extension.equals("amr"))
                return true;
            else if(extension.equals("mpeg"))
                return true;
            else if(extension.equals("3gp"))
                return true;
            else if(extension.equals("mp4"))
                return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(Context context, File file)
    {
        return getPath(context, Uri.fromFile(file));
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
}
