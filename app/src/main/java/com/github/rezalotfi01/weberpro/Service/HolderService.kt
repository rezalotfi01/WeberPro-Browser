package com.github.rezalotfi01.weberpro.Service

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.os.Message
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.github.rezalotfi01.weberpro.Browser.AlbumController
import com.github.rezalotfi01.weberpro.Browser.BrowserContainer
import com.github.rezalotfi01.weberpro.Browser.BrowserController
import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.*
import com.github.rezalotfi01.weberpro.View.WeberContextWrapper
import com.github.rezalotfi01.weberpro.View.WeberWebView

class HolderService : Service(), BrowserController {
    override fun updateAutoComplete() {}

    override fun updateBookmarks() {}

    override fun updateInputBox(query: String) {}

    override fun updateProgress(progress: Int) {}

    override fun showAlbum(albumController: AlbumController, anim: Boolean, expand: Boolean, capture: Boolean) {}

    override fun removeAlbum(albumController: AlbumController) {}

    override fun openFileChooser(uploadMsg: ValueCallback<Uri>) {}

    override fun showFileChooser(filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams) {}

    override fun onCreateView(view: WebView, resultMsg: Message) {}

    override fun onShowCustomView(view: View, requestedOrientation: Int, callback: WebChromeClient.CustomViewCallback): Boolean {
        return true
    }

    override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback): Boolean {
        return true
    }

    override fun onHideCustomView(): Boolean {
        return true
    }

    override fun onLongPress(url: String) {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val webView = WeberWebView(WeberContextWrapper(this))
        webView.browserController = this
        webView.flag = BrowserUtils.FLAG_WEBER
        webView.setAlbumCover(null)
        webView.albumTitle = getString(R.string.album_untitled)
        ViewUtils.bound(this, webView)

        webView.loadUrl(RecordUtils.holder?.url)
        webView.deactivate()

        BrowserContainer.add(webView)
        updateNotification()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        if (IntentUtils.isClear()) {
            BrowserContainer.clear()
        }
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun updateNotification() {
        val notification = NotificationUtils.getHBuilder(this).build()
        startForeground(NotificationUtils.HOLDER_ID, notification)
    }
}
