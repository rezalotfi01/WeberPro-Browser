package com.github.rezalotfi01.weberpro.Application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.evernote.android.job.JobManager
import com.github.rezalotfi01.weberpro.Utils.ViewUtils
import com.liulishuo.filedownloader.FileDownloader
import com.onesignal.OneSignal
import com.github.rezalotfi01.weberpro.JobUtils.WeberJobCreator
import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.FilesUtils

import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created  on 09/09/2016.
 */
class WeberApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        FileDownloader.init(applicationContext)
        MainWorksTasks().execute()
    }

    @SuppressLint("StaticFieldLeak")
    internal inner class MainWorksTasks : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg strings: String): String? {
            val filesUtils = FilesUtils()
            filesUtils.createFolders()
            filesUtils.writeIfHasBadSharedValues(context!!)

            CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                    .setDefaultFontPath(ViewUtils.fontPath)
                    .setFontAttrId(R.attr.fontPath)
                    .build()
            )

            JobManager.create(context!!).addJobCreator(WeberJobCreator())

            OneSignal.startInit(context).init()

            Log.e("WeberTAG", "MainWorkAsyncTask: Finished !")
            return null
        }
    }

    companion object {
        /**
         * Returns a "static" application context. Don't try to create dialogs on
         * this, it's not gonna work!
         */
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }

}

