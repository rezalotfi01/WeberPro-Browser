package com.github.rezalotfi01.weberpro.Task

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask

import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils
import com.github.rezalotfi01.weberpro.View.WeberToast

@SuppressLint("StaticFieldLeak")
class ExportWhitelistTask(private val context: Context) : AsyncTask<Void, Void, Boolean>() {
    private var dialog: ProgressDialog? = null
    private var path: String? = null

    init {
        this.dialog = null
        this.path = null
    }

    override fun onPreExecute() {
        dialog = ProgressDialog(context)
        dialog!!.setCancelable(false)
        dialog!!.setMessage(context.getString(R.string.toast_wait_a_minute))
        dialog!!.show()
    }

    override fun doInBackground(vararg params: Void): Boolean? {
        path = BrowserUtils.exportWhitelist(context)

        return if (isCancelled) {
            false
        } else path != null && !path!!.isEmpty()
    }

    override fun onPostExecute(result: Boolean?) {
        dialog!!.hide()
        dialog!!.dismiss()

        if (result!!) {
            WeberToast.show(context, context.getString(R.string.toast_export_whitelist_successful) + path!!)
        } else {
            WeberToast.show(context, R.string.toast_export_whitelist_failed)
        }
    }
}
