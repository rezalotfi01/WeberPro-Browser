package com.github.rezalotfi01.weberpro.Utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log

import java.io.File

class FilesUtils {
    val weberBrowserFolderAddress: String = Environment.getExternalStorageDirectory().toString() + File.separator + "WeberPro Browser"
    private val defaultDownloadFolderAddress: String
    val savedPagesFolderAddress: String

    init {
        defaultDownloadFolderAddress = weberBrowserFolderAddress + File.separator + "Download"
        savedPagesFolderAddress = weberBrowserFolderAddress + File.separator + "PDF and Pictures"
    }

    fun createFolders() {
        val folderWeberBrowser = File(weberBrowserFolderAddress)
        var success = true
        if (!folderWeberBrowser.exists()) {
            success = folderWeberBrowser.mkdirs()
        }
        Log.e("TAG", "createFolders is folder created : " + success.toString())


        val folderDownload = File(defaultDownloadFolderAddress)
        if (!folderDownload.exists()) {
            folderDownload.mkdirs()
        }

        val folderSavedPages = File(savedPagesFolderAddress)
        if (!folderSavedPages.exists()) {
            folderSavedPages.mkdirs()
        }

    }

    fun moveAllFiles(from: String, to: String) {
        try {
            val dir1 = File(from)
            if (dir1.isDirectory) {
                val content = dir1.listFiles()
                for (aContent in content) {
                    //move content[i]
                    if (aContent.isFile) {
                        aContent.renameTo(File(to + File.separator + aContent.name))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Weber TAG", "moveAllFiles Exception : " + e.toString())
        }

    }

    fun writeIfHasBadSharedValues(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val filePickerAddress = prefs.getString("file_picker_pref", "")
        if (filePickerAddress == null || filePickerAddress == "") {
            val shared: SharedPreferences = if (android.os.Build.VERSION.SDK_INT >= 24) {
                context.getSharedPreferences(PreferenceManager.getDefaultSharedPreferencesName(context), Context.MODE_PRIVATE)
            } else {
                context.getSharedPreferences(context.packageName + "_preferences", Context.MODE_PRIVATE)
            }
            val editor = shared.edit()
            editor.putString("file_picker_pref", defaultDownloadFolderAddress)
            if (!shared.contains("runningDownloadNumbers")) {
                editor.putInt("runningDownloadNumbers", 0)
            }
            editor.apply()
        }
    }
}
