package com.github.rezalotfi01.weberpro.Task

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask

import com.github.rezalotfi01.weberpro.Fragment.SettingFragment
import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils
import com.github.rezalotfi01.weberpro.View.WeberToast

import java.io.File

@SuppressLint("StaticFieldLeak")
class ImportWhitelistTask(private val fragment: SettingFragment, private val file: File) : AsyncTask<Void, Void, Boolean>() {
    private val context: Context
    private var dialog: ProgressDialog? = null
    private var count: Int = 0

    init {
        this.context = fragment.activity
        this.dialog = null
        this.count = 0
    }

    override fun onPreExecute() {
        dialog = ProgressDialog(context)
        dialog!!.setCancelable(false)
        dialog!!.setMessage(context.getString(R.string.toast_wait_a_minute))
        dialog!!.show()
    }

    override fun doInBackground(vararg params: Void): Boolean? {
        count = BrowserUtils.importWhitelist(context, file)

        return if (isCancelled) {
            false
        } else count >= 0
    }

    override fun onPostExecute(result: Boolean?) {
        dialog!!.hide()
        dialog!!.dismiss()

        if (result!!) {
            fragment.isDBChange = true
            WeberToast.show(context, context.getString(R.string.toast_import_whitelist_successful) + count)
        } else {
            WeberToast.show(context, R.string.toast_import_whitelist_failed)
        }
    }
}
