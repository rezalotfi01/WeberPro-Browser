package com.github.rezalotfi01.weberpro.View

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.github.rezalotfi01.weberpro.Browser.AlbumController
import com.github.rezalotfi01.weberpro.Browser.BrowserController
import com.github.rezalotfi01.weberpro.R

internal class Album(private val context: Context, private var albumController: AlbumController?, private var browserController: BrowserController?) {

    var albumView: View? = null
        private set

    private var albumCover: ImageView? = null

    private var albumTitle: TextView? = null
    fun setAlbumCover(bitmap: Bitmap?) {
        albumCover!!.setImageBitmap(bitmap)
    }

    fun getAlbumTitle(): String {
        return albumTitle!!.text.toString()
    }

    fun setAlbumTitle(title: String?) {
        albumTitle!!.text = title
    }

    fun setAlbumController(albumController: AlbumController) {
        this.albumController = albumController
    }

    fun setBrowserController(browserController: BrowserController?) {
        this.browserController = browserController
    }

    init {
        initUI()
    }

    private fun initUI() {
        albumView = LayoutInflater.from(context).inflate(R.layout.album,
                null, false)

        albumView!!.setOnTouchListener(SwipeToDismissListener(
                albumView!!, null,
                object : SwipeToDismissListener.DismissCallback {
                    override fun canDismiss(token: Any?): Boolean {
                        return true
                    }

                    override fun onDismiss(view: View, token: Any?) {
                        browserController!!.removeAlbum(albumController)
                    }
                }
        ))

        albumView!!.setOnClickListener { browserController!!.showAlbum(albumController, false, false, false) }

        albumView!!.setOnLongClickListener {
            WeberToast.show(context, albumTitle!!.text.toString())
            true
        }

        albumCover = albumView!!.findViewById(R.id.album_cover)
        albumTitle = albumView!!.findViewById(R.id.album_title)
        albumTitle!!.text = context.getString(R.string.album_untitled)
    }

    fun activate() {
        albumView!!.setBackgroundResource(R.drawable.album_shape_blue)
    }

    fun deactivate() {
        albumView!!.setBackgroundResource(R.drawable.album_shape_dark)
    }
}
