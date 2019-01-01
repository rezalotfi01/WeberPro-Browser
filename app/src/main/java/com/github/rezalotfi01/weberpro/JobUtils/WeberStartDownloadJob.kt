package com.github.rezalotfi01.weberpro.JobUtils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log

import com.evernote.android.job.Job
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity
import com.github.rezalotfi01.weberpro.Service.DownloaderService
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils
import com.github.rezalotfi01.weberpro.Utils.WeberTimeUtils

/**
 * Created  on 10/19/2016.
 */
class WeberStartDownloadJob : Job() {

    override fun onRunJob(params: Job.Params): Job.Result {
        // run your job here
        val timeUnit = WeberTimeUtils()
        Log.e("TAG", "onRunJob: Download Job started " + timeUnit.currentHour + ":" + timeUnit.currentMinute)

        checkAndTurnOnData(context)

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({ startPausedDownloads(context) }, 7000)

        return Job.Result.SUCCESS
    }

    private fun checkAndTurnOnData(context: Context) {
        val isActivePreference = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("check_box_turn_on_wifi", false)
        if (isActivePreference) {
            GeneralUtils.changeWiFiConnection(context, true)
        }
    }

    private fun startPausedDownloads(context: Context?) {

        val downloadEntities = DownloadDBUtils.getNotFinishedDownloads(context)
        for (i in downloadEntities!!.indices) {
            if (downloadEntities[i].status == DownloadEntity.FIELD_VALUE_STATUS_PAUSED) {
                val openIntent = Intent(context, DownloaderService::class.java)
                openIntent.putExtra("url", downloadEntities[i].url)
                openIntent.putExtra("name", downloadEntities[i].fileName)
                openIntent.putExtra("save_address", downloadEntities[i].path)
                openIntent.putExtra("is_for_now", true)
                openIntent.putExtra("is_resume", true)
                context!!.startService(openIntent)
            }
        }
    }

    companion object {
        const val TAG = "job_start_download_tag"
    }

}
