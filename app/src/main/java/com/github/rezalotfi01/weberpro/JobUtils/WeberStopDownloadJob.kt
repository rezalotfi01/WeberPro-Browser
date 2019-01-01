package com.github.rezalotfi01.weberpro.JobUtils

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log

import com.evernote.android.job.Job
import com.evernote.android.job.JobManager
import com.github.rezalotfi01.weberpro.Service.StartDownloadJobService
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils
import com.github.rezalotfi01.weberpro.Utils.WeberTimeUtils

import java.util.Timer
import java.util.TimerTask

/**
 * Created  on 10/20/2016.
 */
class WeberStopDownloadJob : Job() {

    override fun onRunJob(params: Job.Params): Job.Result {
        //run job here
        val context = context
        val timeUnit = WeberTimeUtils()

        val broadcastManager = LocalBroadcastManager.getInstance(context)
        broadcastManager.sendBroadcast(Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_ALL_DOWNLOADS))

        Log.e("TAG", "onRunJob: Download Job stopped  for first!" + timeUnit.currentHour + ":" + timeUnit.currentMinute)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                broadcastManager.sendBroadcast(Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_ALL_DOWNLOADS))
            }
        }, 500)

        checkAndTurnOffData(context)
        checkAndDeactivateJobs(context)

        Log.e("TAG", "onRunJob: Download Job stopped  for second!" + timeUnit.currentHour + ":" + timeUnit.currentMinute)
        return Job.Result.SUCCESS
    }

    private fun checkAndTurnOffData(context: Context) {
        val isActiveThisPreference = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("check_box_turn_off_wifi", false)
        if (isActiveThisPreference) {
            GeneralUtils.changeWiFiConnection(context, false)
            //            GeneralUtils.changeMobileDataConnection(context,false);
        }
    }

    private fun checkAndDeactivateJobs(context: Context) {
        val startJobToken = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).getInt("start_job_id", -34)
        val stopJobToken = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE).getInt("stop_job_id", -34)
        var isCanceledStart = false
        var isCanceledStop = false
        if (startJobToken >= 0) {
            isCanceledStart = JobManager.instance().cancel(startJobToken)
        }
        if (stopJobToken >= 0) {
            isCanceledStop = JobManager.instance().cancel(stopJobToken)
        }
        Log.e("Weber TAG", "onPreferenceChange is Canceled StartJobID : $isCanceledStart StopJobID : $isCanceledStop")
        JobManager.instance().cancelAll()

        val intent = Intent(context, StartDownloadJobService::class.java)
        intent.putExtra("is_stop", true)
        context.startService(intent)
    }

    companion object {
        const val TAG = "job_stop_download_tag"
    }
}
