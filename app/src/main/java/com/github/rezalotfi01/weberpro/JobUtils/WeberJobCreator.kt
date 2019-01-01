package com.github.rezalotfi01.weberpro.JobUtils

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

/**
 * Created  on 10/19/2016.
 */
class WeberJobCreator : JobCreator {
    override fun create(s: String): Job? {
        when (s) {
            WeberStartDownloadJob.TAG -> return WeberStartDownloadJob()
            WeberStopDownloadJob.TAG -> return WeberStopDownloadJob()
            else -> return null
        }

    }
}
