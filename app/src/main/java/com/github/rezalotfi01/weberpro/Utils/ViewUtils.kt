package com.github.rezalotfi01.weberpro.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView

import com.github.rezalotfi01.weberpro.Application.WeberApplication
import com.github.rezalotfi01.weberpro.R

object ViewUtils {

    const val fontPath = "OpenSans-Light.ttf"
    val typeface: Typeface = Typeface.createFromAsset(WeberApplication.context!!.assets, fontPath)
    fun setViewFont(view: View) {
        if (view is TextView) {
            view.typeface = typeface
        }else if (view is Button){
            view.typeface = typeface
        }
    }

    fun bound(context: Context, view: View) {
        val windowWidth = getWindowWidth(context)
        val windowHeight = getWindowHeight(context)
        val statusBarHeight = getStatusBarHeight(context)
        val dimen48dp = context.resources.getDimensionPixelOffset(R.dimen.layout_height_48dp)

        val widthSpec = View.MeasureSpec.makeMeasureSpec(windowWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(windowHeight - statusBarHeight - dimen48dp, View.MeasureSpec.EXACTLY)

        view.measure(widthSpec, heightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    fun capture(view: View, width: Float, height: Float, scroll: Boolean, config: Bitmap.Config): Bitmap {
        if (!view.isDrawingCacheEnabled) {
            view.isDrawingCacheEnabled = true
        }

        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), config)
        bitmap.eraseColor(Color.WHITE)

        val canvas = Canvas(bitmap)
        var left = view.left
        var top = view.top
        if (scroll) {
            left = view.scrollX
            top = view.scrollY
        }
        val status = canvas.save()
        canvas.translate((-left).toFloat(), (-top).toFloat())

        val scale = width / view.width
        canvas.scale(scale, scale, left.toFloat(), top.toFloat())

        view.draw(canvas)
        canvas.restoreToCount(status)

        val alphaPaint = Paint()
        alphaPaint.color = Color.TRANSPARENT

        canvas.drawRect(0f, 0f, 1f, height, alphaPaint)
        canvas.drawRect(width - 1f, 0f, width, height, alphaPaint)
        canvas.drawRect(0f, 0f, width, 1f, alphaPaint)
        canvas.drawRect(0f, height - 1f, width, height, alphaPaint)
        canvas.setBitmap(null)

        return bitmap
    }

    fun dp2px(context: Context, dp: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
    }

    fun getDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    fun getDrawable(context: Context, id: Int): Drawable? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.resources.getDrawable(id, null)
        } else {
            ContextCompat.getDrawable(context, id)
        }
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0

    }

    fun getWindowHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getWindowWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun setElevation(view: View, elevation: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.elevation = elevation
        }
    }
}