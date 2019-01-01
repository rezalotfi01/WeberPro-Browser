package com.github.rezalotfi01.weberpro.Service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager

import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils

class ClearService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        System.exit(0) // For remove all WebView thread
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        clear()
        stopSelf()
        return Service.START_STICKY
    }

    private fun clear() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val clearBookmarks = sp.getBoolean(getString(R.string.sp_clear_bookmarks), false)
        val clearCache = sp.getBoolean(getString(R.string.sp_clear_cache), true)
        val clearCookie = sp.getBoolean(getString(R.string.sp_clear_cookie), false)
        val clearFormData = sp.getBoolean(getString(R.string.sp_clear_form_data), false)
        val clearHistory = sp.getBoolean(getString(R.string.sp_clear_history), true)
        val clearPasswords = sp.getBoolean(getString(R.string.sp_clear_passwords), false)

        if (clearBookmarks) {
            BrowserUtils.clearBookmarks(this)
        }
        if (clearCache) {
            BrowserUtils.clearCache(this)
        }
        if (clearCookie) {
            BrowserUtils.clearCookie(this)
        }
        if (clearFormData) {
            BrowserUtils.clearFormData(this)
        }
        if (clearHistory) {
            BrowserUtils.clearHistory(this)
        }
        if (clearPasswords) {
            BrowserUtils.clearPasswords(this)
        }
    }
}
