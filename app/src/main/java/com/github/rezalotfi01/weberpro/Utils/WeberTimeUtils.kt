package com.github.rezalotfi01.weberpro.Utils

import android.annotation.SuppressLint
import android.util.Log

import java.util.Calendar

class WeberTimeUtils {
    private val calendar: Calendar = Calendar.getInstance()

    val currentHour: Long
        get() = calendar.get(Calendar.HOUR_OF_DAY).toLong()

    val currentMinute: Long
        get() = calendar.get(Calendar.MINUTE).toLong()

    private val currentTimeMillis: Long
        get() = currentHour * 60 * 60 * 1000 + currentMinute * 60 * 1000

    fun getYourAndCurrentTimeInterval(yourTime: Long): Long {
        val currentTime = currentTimeMillis
        val intervalTime: Long
        intervalTime = if (yourTime > currentTime) {
            yourTime - currentTime
        } else {
            24 * 60 * 60 * 1000 - (currentTime - yourTime)
        }
        return intervalTime
    }

    companion object {
        @SuppressLint("DefaultLocale")
        fun convertSecondsToHMmSs(seconds: Long): String {
            val s = seconds % 60
            val m = seconds / 60 % 60
            val h = seconds / (60 * 60) % 24
            return String.format("%d:%02d:%02d", h, m, s)
        }
    }

}
