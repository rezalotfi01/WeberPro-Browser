package com.github.rezalotfi01.weberpro.Service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.evernote.android.job.JobRequest
import com.github.rezalotfi01.weberpro.Activity.DownloadActivity
import com.github.rezalotfi01.weberpro.JobUtils.WeberStartDownloadJob
import com.github.rezalotfi01.weberpro.JobUtils.WeberStopDownloadJob
import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.View.WeberToast
import com.sdsmdg.tastytoast.TastyToast

class StartDownloadJobService : Service() {


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        var isStop = false
        try {
            isStop = intent.getBooleanExtra("is_stop", false)
        } catch (e: Exception) {
            Log.e("Weber TAG", "StartDownloadJobService onStartCommand Exception : " + e.toString())
        }

        if (isStop) {
            try {
                stopForeground(true)
            } catch (e: Exception) {
                Log.e("Weber TAG", "StartDownloadJobService onStartCommand Exception : " + e.toString())
            }

            stopSelf()
            return Service.START_NOT_STICKY
        }

        val jobType = intent.getStringExtra("job_type")
        val timesInterval = intent.getLongExtra("times_interval", -34)

        if (jobType != null && timesInterval > 0) {
            if (jobType == "start") {
                //set start job task
                val request = JobRequest.Builder(WeberStartDownloadJob.TAG)
                        .setExact(timesInterval)//(24 * 60 * 60 * 1000)
                        .setPersisted(true)
                        .build()
                val jobToken = request.schedule()
                startNotification(this@StartDownloadJobService)
                getSharedPreferences(packageName, Context.MODE_PRIVATE).edit().putInt("start_job_id", jobToken).apply()
            } else {
                //set stop job task
                val request = JobRequest.Builder(WeberStopDownloadJob.TAG)
                        .setExact(timesInterval)//(24 * 60 * 60 * 1000)
                        .setPersisted(true)
                        .build()
                val jobToken = request.schedule()
                startNotification(this@StartDownloadJobService)
                getSharedPreferences(packageName, Context.MODE_PRIVATE).edit().putInt("stop_job_id", jobToken).apply()
            }
        } else {
            WeberToast.showPrettyToast(this@StartDownloadJobService, R.string.planing_failed, TastyToast.LENGTH_LONG, TastyToast.ERROR)
        }

        return Service.START_STICKY
    }


    private fun startNotification(context: Context) {
        val notifID = 2469
        var notification = Notification()
        val myNotifIntent = Intent(context, DownloadActivity::class.java)
        val myNotifPendingIntent = PendingIntent.getActivity(context, 0, myNotifIntent, 0)

        val mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        myNotifIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val mBuilder = NotificationCompat.Builder(context)
        mBuilder.setContentTitle(getString(R.string.planing_notification_title))
                .setContentText(getString(R.string.planing_notification_text))
                .setContentIntent(myNotifPendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher_small_notif)
        mBuilder.setProgress(100, 0, false)
        notification = mBuilder.build()
        mNotifyManager.notify(notifID, notification)

        startForeground(notifID, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}
