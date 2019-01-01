package com.github.rezalotfi01.weberpro.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils;
import com.github.rezalotfi01.weberpro.Utils.WeberTimeUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.github.rezalotfi01.weberpro.Activity.DownloadActivity;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Task.TimerHandler;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class DownloaderService extends Service {
    private Context context;
    private LocalBroadcastManager broadcastManager;
    private TimerHandler timerHandler;

    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    private final String TAG = "Weber TAG";

    private String fileSaveAddress;
    private int initialSize;
    private int autoRetryTimes;
    private int mainErrorsNumber;
    private boolean isAutoResume;
    private final long[] maxSizeAllForSave;
    private int currentDownloadsNumber;

    private FileDownloadListener listener;

    private BroadcastReceiver pauseRequestReceiver;
    private BroadcastReceiver pauseAllReceiver;
    public DownloaderService() {
        maxSizeAllForSave = new long[]{0};
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        broadcastManager = LocalBroadcastManager.getInstance(context);
        timerHandler = new TimerHandler();
        initBroadcastReceivers();
        registerReceivers();

        isAutoResume = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("switch_auto_resume",false);
        mainErrorsNumber = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context)
                .getString("download_main_errors_number","5"));
        currentDownloadsNumber = 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterReceivers();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        //Try-Catch for exit when Service started unreasonable
        String fileURL;
        try {
            fileURL = intent.getStringExtra("url");
        }catch (Exception e){
            return START_NOT_STICKY;
        }
        try {
            initialSize = (int) intent.getLongExtra("size",0);
        }catch (Exception e){
            initialSize = 0;
        }
        String fileName = intent.getStringExtra("name");
        fileSaveAddress = intent.getStringExtra("save_address");
        Boolean isForNow = intent.getBooleanExtra("is_for_now",true);
        Boolean isResume = intent.getBooleanExtra("is_resume",false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean synchronicDownload = prefs.getBoolean("downloads_number", false);
        final long maxSizePerDownload = Long.valueOf(prefs.getString("max_volume_size_each_download","0").trim())*1048576; //convert MB to B
        final long maxSizeForAllDownloads = Long.valueOf(prefs.getString("max_volume_size_all_downloads","0").trim())*1048576;//convert MB to B

        autoRetryTimes = Integer.valueOf(prefs.getString("download_errors_number","1024"));
        int threadsNumber = Integer.valueOf(prefs.getString("parts_number","5"));
        Log.e(TAG, "onStartCommand File Save Address : "+fileSaveAddress);

        currentDownloadsNumber++;

        FileDownloader downloader = FileDownloader.getImpl();
        downloader.setMaxNetworkThreadCount(threadsNumber);

        final int[] notifID = {GeneralUtils.getRandomIntNumber(10, 400)};
        final Notification[] notification = new Notification[1];
        final Intent myNotifIntent = new Intent(context, DownloadActivity.class);
        final PendingIntent myNotifPendingIntent = PendingIntent.getActivity(context, 0, myNotifIntent, 0);

        final int[] percent = {0};



        listener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                notifID[0] = task.getId();
                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                myNotifIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setContentTitle(task.getFilename())
                        .setContentText(getString(R.string.toast_download_starts))
                        .setContentIntent(myNotifPendingIntent)
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher_small_notif);
                mBuilder.setProgress(100, 0, false);
                notification[0] = mBuilder.build();
                mNotifyManager.notify(notifID[0], notification[0]);

                startForeground(notifID[0], notification[0]);



                String speed = GeneralUtils.readableFileSize(task.getSpeed()* 1000)+"/S";
                String soFarDownloaded = GeneralUtils.readableFileSize(soFarBytes)
                        +"/"+GeneralUtils.readableFileSize(totalBytes);
                String remainingTime = "00:00";
                int percent = 0;

                DownloadDBUtils.createOrUpdateInDatabase(context,task.getFilename(),task.getUrl(),soFarDownloaded,remainingTime,fileSaveAddress,speed,percent,false,DownloadEntity.FIELD_VALUE_STATUS_RESUMING,task.getId());


                Intent pendIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PENDING);
                pendIntent.putExtra(BroadcastUtils.FILE_NAME_NAME,task.getFilename())
                        .putExtra(BroadcastUtils.PERCENT_NAME,percent)
                        .putExtra(BroadcastUtils.SPEED_NAME,speed)
                        .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME,soFarDownloaded)
                        .putExtra(BroadcastUtils.REMAINING_TIME_NAME,remainingTime);
                broadcastManager.sendBroadcast(pendIntent);
                //Log.e(TAG, "pending: ");
            }

            @Override
            protected void started(BaseDownloadTask task) {
                super.started(task);
                timerHandler.start();
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if (totalBytes > 0) {
                    totalBytes = task.getSmallFileTotalBytes();
                }else if (initialSize > 0){
                    totalBytes = initialSize;
                }

                if (maxSizePerDownload > 0){
                    if (maxSizePerDownload <= soFarBytes){
                        FileDownloader.getImpl().pause(task.getId());
                        Log.e(TAG, "progress pause one for size limit, size equals : "+String.valueOf(maxSizePerDownload/1000)+" KB");
                        WeberToast.Companion.showLongPrettyToast(context, getString(R.string.toast_download_pausing_because_limits)
                                , TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
                    }
                }

                if (maxSizeForAllDownloads > 0){
                   // maxSizeAllForSave[0] += soFarBytes;
                    if (maxSizeForAllDownloads <= (maxSizeAllForSave[0]+soFarBytes)){
                        FileDownloader.getImpl().pauseAll();
                        Log.e(TAG, "progress pause all for size limit, all size equals : "+String.valueOf(maxSizeAllForSave[0])+" KB");
                        WeberToast.Companion.showLongPrettyToast(context, getString(R.string.toast_download_pausing_because_all_limits)
                                , TastyToast.LENGTH_LONG, TastyToast.DEFAULT);
                    }
                }

                percent[0] = (int) ((  ((double) soFarBytes) / ((double) totalBytes)  )* 100);
                String whiteSpace = "  --  ";
                String speed = GeneralUtils.readableFileSize(task.getSpeed()* 1000)+"/S";
                String soFarDownloaded = GeneralUtils.readableFileSize(soFarBytes)
                        +"/"+GeneralUtils.readableFileSize(totalBytes);
                String remainingTime;
                try {
                    remainingTime = WeberTimeUtils.Companion.convertSecondsToHMmSs((totalBytes - soFarBytes) / (task.getSpeed()*1000));
                }catch (Exception e){
                    remainingTime = " ";
                }
                mBuilder.setContentTitle(task.getFilename())
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher_small_notif)
                        .setProgress(100,percent[0],false)
                        .setContentIntent(myNotifPendingIntent)
                        .setContentText(String.valueOf(percent[0])+"%"+ whiteSpace + soFarDownloaded + whiteSpace
                                + speed + whiteSpace + remainingTime);
                mNotifyManager.notify(notifID[0], mBuilder.build());


                Intent progIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PROGRESS);
                progIntent.putExtra(BroadcastUtils.PERCENT_NAME,percent[0])
                        .putExtra(BroadcastUtils.FILE_NAME_NAME,task.getFilename())
                        .putExtra(BroadcastUtils.URL_NAME,task.getUrl())
                        .putExtra(BroadcastUtils.SPEED_NAME,speed)
                        .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME,soFarDownloaded)
                        .putExtra(BroadcastUtils.REMAINING_TIME_NAME,remainingTime)
                        .putExtra(BroadcastUtils.ID_NAME,task.getId());
                broadcastManager.sendBroadcast(progIntent);

                //Log.e(TAG, "progress: " + percent[0] + " percent");
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                try {
                    mNotifyManager.cancel(task.getId());
                    mBuilder.setContentTitle(task.getFilename())
                            .setContentText(getString(R.string.toast_download_successful))
                            .setContentIntent(myNotifPendingIntent)
                            .setOngoing(false)
                            .setSmallIcon(R.mipmap.ic_launcher_small_notif);
                    notification[0] = mBuilder.build();
                    mNotifyManager.notify(notifID[0], notification[0]);

                    //Sum size for All downloads Size limit
                    maxSizeAllForSave[0] = maxSizeAllForSave[0] + task.getLargeFileSoFarBytes();

                    String soFarDownloaded = GeneralUtils.readableFileSize(task.getLargeFileSoFarBytes())
                            + "/" + GeneralUtils.readableFileSize(task.getLargeFileSoFarBytes());
                    String elapsedTime = WeberTimeUtils.Companion.convertSecondsToHMmSs(timerHandler.endAndGetElapsedTime());
                    String speedWord = getString(R.string.download_progress_completed);


                    DownloadDBUtils.createOrUpdateInDatabase(context,task.getFilename(),task.getUrl(), soFarDownloaded, elapsedTime, fileSaveAddress, speedWord, 100, true, DownloadEntity.FIELD_VALUE_STATUS_COMPLETED, task.getId());

                    WeberToast.Companion.showPrettyToast(context, getString(R.string.toast_download_successful) + "\n" + task.getFilename()
                            , TastyToast.LENGTH_LONG, TastyToast.SUCCESS);

                    Intent completeIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_FINISH);
                    completeIntent.putExtra(BroadcastUtils.PERCENT_NAME, 100)
                            .putExtra(BroadcastUtils.FILE_NAME_NAME, task.getFilename())
                            .putExtra(BroadcastUtils.URL_NAME,task.getUrl())
                            .putExtra(BroadcastUtils.SPEED_NAME, speedWord)
                            .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME, soFarDownloaded)
                            .putExtra(BroadcastUtils.REMAINING_TIME_NAME, elapsedTime);
                    broadcastManager.sendBroadcast(completeIntent);
                    Log.e(TAG, "completed: ");

                    stopForeground(true);

                    currentDownloadsNumber--;

                    downloadNextInQueue();

                }catch (Exception e){
                    Log.e(TAG, "completed Exception : "+e.toString());
                }
            }



            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                mNotifyManager.cancel(task.getId());


                String soFarDownloaded = GeneralUtils.readableFileSize(soFarBytes)
                        +"/"+GeneralUtils.readableFileSize(totalBytes);
                String elapsedTime = WeberTimeUtils.Companion.convertSecondsToHMmSs(timerHandler.endAndGetElapsedTime());
                String speedWord = getString(R.string.download_progress_paused);
                double dPercent = ((  ((float) soFarBytes) / ((float) totalBytes)  )* 100);


                DownloadDBUtils.createOrUpdateInDatabase(context,task.getFilename(),task.getUrl(),soFarDownloaded
                        ,elapsedTime,fileSaveAddress,speedWord,dPercent,false,DownloadEntity.FIELD_VALUE_STATUS_PAUSED,task.getId());

                Intent pauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PAUSED);
                pauseIntent.putExtra(BroadcastUtils.PERCENT_NAME,dPercent)
                        .putExtra(BroadcastUtils.FILE_NAME_NAME,task.getFilename())
                        .putExtra(BroadcastUtils.URL_NAME,task.getUrl())
                        .putExtra(BroadcastUtils.SPEED_NAME,speedWord)
                        .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME,soFarDownloaded)
                        .putExtra(BroadcastUtils.REMAINING_TIME_NAME,elapsedTime);
                broadcastManager.sendBroadcast(pauseIntent);

                WeberToast.Companion.showPrettyToast(context, getString(R.string.toast_download_paused) + "\n" + task.getFilename()
                        , TastyToast.LENGTH_LONG, TastyToast.DEFAULT);

                stopForeground(true);

                currentDownloadsNumber--;

                downloadNextInQueue();
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                mNotifyManager.cancel(task.getId());
                Log.e(TAG, "error: ");

                mainErrorsNumber--;
                if (mainErrorsNumber <= 0 || !isAutoResume) {
                    String soFarDownloaded = GeneralUtils.readableFileSize(task.getLargeFileSoFarBytes())
                            + "/" + GeneralUtils.readableFileSize(task.getLargeFileTotalBytes());
                    String speedWord = getString(R.string.download_progress_error);
                    double dPercent = ((((float) task.getLargeFileSoFarBytes()) / ((float) task.getLargeFileTotalBytes())) * 100);
                    String elapsedTime = WeberTimeUtils.Companion.convertSecondsToHMmSs(timerHandler.endAndGetElapsedTime());


                    DownloadDBUtils.createOrUpdateInDatabase(context, task.getFilename(), task.getUrl(), soFarDownloaded
                            , elapsedTime, fileSaveAddress, speedWord, dPercent, false, DownloadEntity.FIELD_VALUE_STATUS_ERROR, task.getId());

                    Intent errorIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_ERROR);
                    errorIntent.putExtra(BroadcastUtils.PERCENT_NAME, dPercent)
                            .putExtra(BroadcastUtils.FILE_NAME_NAME, task.getFilename())
                            .putExtra(BroadcastUtils.URL_NAME, task.getUrl())
                            .putExtra(BroadcastUtils.SPEED_NAME, speedWord)
                            .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME, soFarDownloaded)
                            .putExtra(BroadcastUtils.REMAINING_TIME_NAME, elapsedTime);
                    broadcastManager.sendBroadcast(errorIntent);

                    WeberToast.Companion.showPrettyToast(context, getString(R.string.toast_download_failed) + "\n" + task.getFilename()
                            , TastyToast.LENGTH_LONG, TastyToast.ERROR);

                    stopForeground(true);

                    currentDownloadsNumber--;

                    downloadNextInQueue();
                }else {
                    WeberToast.Companion.showPrettyToast(context, getString(R.string.toast_retrying_download) + "\n" + task.getFilename()
                            , TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    startDownload(task.getUrl(),task.getPath(),task.getFilename(),autoRetryTimes,this);
                }
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                Log.e(TAG, "warn: ");
            }



           /* @Override
            protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
                super.retry(task, ex, retryingTimes, soFarBytes);

                int totalBytes = task.getSmallFileTotalBytes();
                percent[0] = (int) ((  (new Float(soFarBytes)) / (new Float(totalBytes))  )* 100);
                String whiteSpace = "  --  ";
                String speed = GeneralUtils.readableFileSize(task.getSpeed()* 1000)+"/S";
                String soFarDownloaded = GeneralUtils.readableFileSize(soFarBytes)
                        +"/"+GeneralUtils.readableFileSize(totalBytes);
                String remainingTime;
                try {
                    remainingTime = WeberTimeUtils.convertSecondsToHMmSs((totalBytes - soFarBytes) / (task.getSpeed()*1000));
                }catch (Exception e){
                    remainingTime = " ";
                }
                mBuilder.setContentTitle(task.getFilename())
                        .setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher_small_notif)
                        .setProgress(100,percent[0],false)
                        .setContentText(String.valueOf(percent[0])+"%"+ whiteSpace + soFarDownloaded + whiteSpace
                                + speed + whiteSpace + remainingTime);
                mNotifyManager.notify(notifID[0], mBuilder.build());


                Intent progIntent = new Intent(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PROGRESS);
                progIntent.putExtra(BroadcastUtils.PERCENT_NAME,percent[0])
                        .putExtra(BroadcastUtils.FILE_NAME_NAME,task.getFilename())
                        .putExtra(BroadcastUtils.URL_NAME,task.getUrl())
                        .putExtra(BroadcastUtils.SPEED_NAME,speed)
                        .putExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME,soFarDownloaded)
                        .putExtra(BroadcastUtils.REMAINING_TIME_NAME,remainingTime)
                        .putExtra(BroadcastUtils.ID_NAME,task.getId());
                broadcastManager.sendBroadcast(progIntent);


            }*/



        };




        if (isForNow){
            List<DownloadEntity> resumingDownloads = DownloadDBUtils.getDownloadsByStatus(context,DownloadEntity.FIELD_VALUE_STATUS_RESUMING);
            if ((synchronicDownload) || (resumingDownloads.size() <= 0  && currentDownloadsNumber <= 1) ) {
            //if (synchronicDownload || resumingDownloads.size() <= 0) {
                //start download file
                try {
                    int token;
                    if (isResume) {
                        token = startDownload(fileURL, fileSaveAddress, fileName, autoRetryTimes, listener);
                    } else {
                        token = startDownload(fileURL, fileSaveAddress, fileName, autoRetryTimes, listener);
                        WeberToast.Companion.showPrettyToast(context, R.string.toast_download_starts, TastyToast.LENGTH_LONG, TastyToast.INFO);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "onStartCommand start download Exception : " + e.toString());
                }
            }else {
                DownloadEntity inQueueDownload = null;
                try {
                    inQueueDownload = DownloadDBUtils.getDownloadByURL(context, fileURL);
                }catch (Exception e){
                    Log.e(TAG, "onStartCommand getDownloadDetails For Queue Exception : "+e.toString());
                }
                if (inQueueDownload != null) //check is this download exist or new
                {
                    DownloadDBUtils.createOrUpdateInDatabase(context, fileName, fileURL, inQueueDownload.getDownloadedSize() , "N/A",
                            fileSaveAddress, "0KB/S", inQueueDownload.getPercent(), false, DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE, inQueueDownload.getToken());
                }else {
                    DownloadDBUtils.createOrUpdateInDatabase(context, fileName, fileURL, "0", "N/A",
                            fileSaveAddress, "0KB/S", 0, false, DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE, -34);
                }
                WeberToast.Companion.showPrettyToast(context, R.string.toast_download_added_to_queue, TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            }


        }else {
            DownloadDBUtils.createOrUpdateInDatabase(context, fileName, fileURL,"0","N/A",
                    fileSaveAddress,"0KB/S",0,false,DownloadEntity.FIELD_VALUE_STATUS_PAUSED,-34);
            WeberToast.Companion.showPrettyToast(context,R.string.toast_download_added,TastyToast.LENGTH_LONG,TastyToast.SUCCESS);
        }
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void downloadNextInQueue(){
        List<DownloadEntity> inQueueDownloads = DownloadDBUtils.getDownloadsByStatus(context,DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE);
        if (inQueueDownloads.size() > 0){
            DownloadEntity firstDownloadInList = inQueueDownloads.get(0);

            startDownload(firstDownloadInList.getURL(),firstDownloadInList.getPath()
                    ,firstDownloadInList.getFileName(), autoRetryTimes,listener);
        }else {
            maxSizeAllForSave[0] = 0;
        }
    }

    private int startDownload(String fileURL , String fileSaveAddress , String fileName , int autoRetryTimes , FileDownloadListener listener){
        FileDownloader downloader = FileDownloader.getImpl();
        int token = downloader.create(fileURL)
                .setPath(fileSaveAddress + File.separator + fileName, false)
                .setCallbackProgressTimes(100000)
                .setForceReDownload(false)
                .setCallbackProgressMinInterval(350)
                .setMinIntervalUpdateSpeed(250)
                .setAutoRetryTimes(autoRetryTimes)
                .setListener(listener)
                .setTag("xtag")
                .start();

        return token;
    }

    private void initBroadcastReceivers() {
        pauseRequestReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String receivedFileURL = intent.getStringExtra("URL");
                    DownloadEntity entity = DownloadDBUtils.getDownloadByURL(context,receivedFileURL);
                    Log.e(TAG, "onReceivePauseRequest : Received !");
                    if (entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_RESUMING)) {
                        FileDownloader.getImpl().pause(entity.getToken());
                    } else {
                        DownloadDBUtils.createOrUpdateInDatabase(context, entity.getFileName(), entity.getURL(), entity.getDownloadedSize()
                                , entity.getRemainingTime(), entity.getPath(), entity.getSpeed(), entity.getPercent(), false
                                , DownloadEntity.FIELD_VALUE_STATUS_PAUSED, entity.getToken());
                    }
                }catch (Exception e){
                    Log.e(TAG, "onReceive Pause Request Exception : "+e.toString());
                }
            }
        };

        pauseAllReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    DownloadDBUtils.setAllNotFinishedDownloadsStatus(getApplicationContext(),DownloadEntity.FIELD_VALUE_STATUS_PAUSED);
                    FileDownloader.getImpl().pauseAll();
                    currentDownloadsNumber = 0;
                }catch (Exception e){
                    Log.e(TAG, "onReceive Pause All Downloads : " + e.toString());
                }
            }
        };
    }

    private void registerReceivers(){
        broadcastManager.registerReceiver(pauseRequestReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD));
        broadcastManager.registerReceiver(pauseAllReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_ALL_DOWNLOADS));

    }
    private void unRegisterReceivers(){
        broadcastManager.unregisterReceiver(pauseRequestReceiver);
        broadcastManager.unregisterReceiver(pauseAllReceiver);
    }



    private class getFileSizeTask extends AsyncTask<String , String , String>{

        @Override
        protected String doInBackground(String... strings) {
            String contentLengthStr = "-32";
            try {

                final URL uri=new URL(strings[0]);

                URLConnection ucon;
                ucon=uri.openConnection();
                ucon.connect();
                contentLengthStr = ucon.getHeaderField("content-length");
            }
            catch(Exception e1) {
                Log.e(TAG, "onGetFileSizeFromHTTP Exception : "+ e1.toString());
            }
            return contentLengthStr;
        }
    }
}
