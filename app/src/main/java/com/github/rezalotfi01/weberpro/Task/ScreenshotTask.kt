package com.github.rezalotfi01.weberpro.Task

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask

import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils
import com.github.rezalotfi01.weberpro.Utils.ViewUtils
import com.github.rezalotfi01.weberpro.View.WeberToast
import com.github.rezalotfi01.weberpro.View.WeberWebView

@SuppressLint("StaticFieldLeak")
class ScreenshotTask(private val context: Context, private val webView: WeberWebView, private val format: String) : AsyncTask<Void, Void, Boolean>() {
    private var dialog: ProgressDialog? = null
    private var windowWidth: Int = 0
    private var contentHeight: Float = 0.toFloat()
    private var title: String? = null
    private var path: String? = null

    init {
        this.dialog = null
        this.windowWidth = 0
        this.contentHeight = 0f
        this.title = null
        this.path = null
    }

    override fun onPreExecute() {

        dialog = ProgressDialog(context)
        dialog!!.setCancelable(false)
        dialog!!.setMessage(context.getString(R.string.toast_wait_a_minute))
        dialog!!.show()

        windowWidth = ViewUtils.getWindowWidth(context)
        contentHeight = webView.contentHeight * ViewUtils.getDensity(context)
        title = webView.title
    }

    override fun doInBackground(vararg params: Void): Boolean? {
        try {
            val bitmap = ViewUtils.capture(webView, windowWidth.toFloat(), contentHeight, false, Bitmap.Config.ARGB_8888)
            if (format == "PIC") {
                path = BrowserUtils.screenshot(context, bitmap, title)
            } else {
                path = BrowserUtils.saveAsPDF(bitmap, title)
            }
        } catch (e: Exception) {
            path = null
        }

        return path != null && !path!!.isEmpty()
    }

    override fun onPostExecute(result: Boolean?) {

        dialog!!.hide()
        dialog!!.dismiss()

        if (result!!) {
            WeberToast.show(context, context.getString(R.string.toast_screenshot_successful) + path!!)
        } else {
            WeberToast.show(context, R.string.toast_screenshot_failed)
        }
    }
}
