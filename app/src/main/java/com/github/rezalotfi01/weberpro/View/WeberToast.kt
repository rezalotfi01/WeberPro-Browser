package com.github.rezalotfi01.weberpro.View

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

import com.sdsmdg.tastytoast.TastyToast
import es.dmoral.toasty.Toasty

class WeberToast {
    companion object {

        fun show(context: Context, stringResId: Int) {
            show(context, context.getString(stringResId))
        }

        fun show(context: Context, text: String) {
            try {
                Toasty.info(context,text,Toast.LENGTH_LONG,true).show()
            } catch (e: Exception) {
                Log.e("TAG", "Weber Toast show Exception : " + e.toString())
            }

        }

        fun showPrettyToast(context: Context, text: String, length: Int, messageType: Int) {
            try {
                val handler = Handler(Looper.getMainLooper())
                handler.post { TastyToast.makeText(context, text, length, messageType).show() }
            } catch (e: Exception) {
                Log.e("TAG", "Weber Pretty Toast show Exception : " + e.toString())
            }

        }

        fun showPrettyToast(context: Context, textResId: Int, length: Int, messageType: Int) {
            val txt = context.getString(textResId)
            showPrettyToast(context, txt, length, messageType)
        }

        fun showLongPrettyToast(context: Context, text: String, length: Int, messageType: Int) {
            try {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    for(x in 1..2)
                        TastyToast.makeText(context, text, length, messageType).show()
                }
            } catch (e: Exception) {
                Log.e("TAG", "Weber Pretty Toast show Exception : " + e.toString())
            }

        }

        fun showLongPrettyToast(context: Context, textResId: Int, length: Int, messageType: Int) {
            val txt = context.getString(textResId)
            showLongPrettyToast(context, txt, length, messageType)
        }

    }
}
