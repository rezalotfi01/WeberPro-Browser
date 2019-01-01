package com.github.rezalotfi01.weberpro.Service

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater

import com.github.rezalotfi01.weberpro.Activity.BrowserActivity
import com.github.rezalotfi01.weberpro.Activity.FloatingActivity
import com.github.rezalotfi01.weberpro.R
import com.github.rezalotfi01.weberpro.View.WeberToast
import com.txusballesteros.bubbles.BubbleLayout
import com.txusballesteros.bubbles.BubblesManager

class BubbleButtonService : Service() {

    private var bubblesManager: BubblesManager? = null
    private var bubbleView: BubbleLayout? = null
    private var addTime: Int = 0
    private var isOpenWithText: Boolean = false
    private var isFromOnlyCopy: Boolean = false
    private var isRemoveFromCopyAnim: Boolean = false

    private val context: Context

    init {
        context = this
    }

    override fun onCreate() {
        super.onCreate()
        bubbleView = LayoutInflater
                .from(this).inflate(R.layout.bubble_layout, null) as BubbleLayout
        addTime = 0
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Log.e("Weber TAG", "Initializing Bubble button...!")
        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        val bubbleStatus = Integer.valueOf(sp.getString("SP_BUBBLE_BUTTON_STATUS", "2")!!)

        bubblesManager = BubblesManager.Builder(this).setTrashLayout(R.layout.bubble_trash_layout).build()
        bubblesManager!!.initialize()


        if (bubbleView!!.childCount > 1) {
            Log.e("Weber TAG", "onStartCommand Bubble child count : " + bubbleView!!.childCount)

            return Service.START_NOT_STICKY
        }


        val bubbleClickListener = BubbleLayout.OnBubbleClickListener {
            if (isOpenWithText) {
                Log.e("Weber TAG", "onBubbleClicked With Text...! ")
                val openIntent = Intent(context, FloatingActivity::class.java)
                openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                openIntent.putExtra("CopyText", "google it !")
                startActivity(openIntent)
            } else {
                Log.e("Weber TAG", "onBubbleClicked Without Text...! ")
                val openIntent = Intent(context, FloatingActivity::class.java)
                openIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(openIntent)
            }
        }

        val bubbleRemoveListener = BubbleLayout.OnBubbleRemoveListener {
            if (!isRemoveFromCopyAnim) {
                if (addTime >= 2) {
                    //bubblesManager.removeBubble(bubbleView);
                    addTime--
                }

                WeberToast.show(context, R.string.bubble_removed)
                sp.edit().putString("SP_BUBBLE_BUTTON_STATUS", "2").apply()

                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.addPrimaryClipChangedListener {
                    //Nothing!
                }

                try {
                    bubblesManager!!.recycle()
                } catch (e: Exception) {
                    Log.e("Weber TAG", "onBubbleRemoved Exception : " + e.toString())
                }

                stopForeground(true)
                stopSelf()
            }
        }



        isFromOnlyCopy = false

        val timeHandler = Handler()
        val timeRunnable = Runnable {
            isOpenWithText = false
            if (isFromOnlyCopy && addTime >= 2) {
                try {
                    //bubbleView.removeAllViews();
                    //bubblesManager.recycle();
                    isRemoveFromCopyAnim = true
                    bubblesManager!!.removeBubble(bubbleView)
                    addTime--
                    Log.e("WeberTAG", "Time run ended !")
                } catch (e: Exception) {
                    Log.e("Weber TAG", "Timer End Works Exception : " + e.toString())
                }

            }

            Log.e("WeberTAG", "Time run !!!")
        }

        //final Timer setCopyFlagTimer = new Timer();

        Log.e("Weber TAG", "Bubble status is : " + bubbleStatus.toString())

        when (bubbleStatus) {
            0 ->
                //always show bubble button (add to screen and listen copy receiver in service)
            {
                if (addTime <= 1) {
                    bubblesManager!!.addBubble(bubbleView, 5, 100)
                    addTime++
                }
                bubbleView!!.setOnBubbleClickListener(bubbleClickListener)
                bubbleView!!.setOnBubbleRemoveListener(bubbleRemoveListener)

                val changedListener = ClipboardManager.OnPrimaryClipChangedListener {
                    playClickAnim(bubbleView!!, context)
                    isOpenWithText = true
                    try {
                        timeHandler.postDelayed(timeRunnable, COPY_TIME)
                    } catch (e: Exception) {
                        Log.e("Weber TAG", "onPrimaryClipChanged Timer Set Exception:  " + e.toString())
                    }
                }

                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.addPrimaryClipChangedListener(changedListener)
            }
            1 ->
                //show only when copy link (listen to copy receiver)
            {
                if (addTime >= 2) {
                    bubblesManager!!.removeBubble(bubbleView)
                    addTime--
                }
                val changedListener = ClipboardManager.OnPrimaryClipChangedListener {
                    try {
                        if (addTime < 2) {
                            bubblesManager!!.addBubble(bubbleView, 5, 100)
                            addTime++
                        }
                    } catch (e: Exception) {
                        Log.e("Weber TAG", "Bubble add Exception " + e.toString())
                    }

                    isOpenWithText = true
                    isFromOnlyCopy = true
                    bubbleView!!.setOnBubbleClickListener(bubbleClickListener)
                    bubbleView!!.setOnBubbleRemoveListener(bubbleRemoveListener)

                    playClickAnim(bubbleView!!, context)
                    try {
                        //setCopyFlagTimer.schedule(setFlagTask, COPY_TIME);
                        timeHandler.postDelayed(timeRunnable, COPY_TIME)
                    } catch (e: Exception) {
                        Log.e("Weber Tag", "onPrimaryClipChanged Timer Set Exception : " + e.toString())
                    }
                }

                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.addPrimaryClipChangedListener(changedListener)
            }
            2 ->
                //never show bubble button (Nothing!)
            {
                if (addTime >= 2) {
                    bubblesManager!!.removeBubble(bubbleView)
                    addTime--
                }
                bubblesManager!!.recycle()
                stopForeground(true)

                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.addPrimaryClipChangedListener {
                    //Nothing!
                }
                stopSelf()
            }
            else -> {
            }
        }

        val xIntent = Intent(this, BrowserActivity::class.java)
        val resultPendingIntent = PendingIntent.getActivity(this, 0, xIntent, 0)
        xIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val notification = Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_small_notif)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentIntent(resultPendingIntent)
                .setContentTitle(getString(R.string.bubble_notification_title))
                .setContentText(getString(R.string.bubble_notification_content))
                .build()
        startForeground(NOTIFICATION_ID, notification)

        return Service.START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun playClickAnim(bubbleView: BubbleLayout, ctx: Context) {
        if (!bubbleView.isInEditMode) {
            val animator = AnimatorInflater
                    .loadAnimator(ctx, com.txusballesteros.bubbles.R.animator.bubble_down_click_animator) as AnimatorSet
            animator.setTarget(bubbleView)
            animator.duration = 1200
            animator.start()

            Handler().postDelayed({ animator.start() }, 2100)

            bubbleView.animate().rotationBy(360f).setDuration(2000).start()
        }
    }

    companion object {
        private const val COPY_TIME = 5000L
        private const val NOTIFICATION_ID = 3473
    }


}
