package com.github.rezalotfi01.weberpro.Browser;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.github.rezalotfi01.weberpro.View.WeberWebView;

public class WeberGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final WeberWebView webView;
    private boolean longPress = true;

    public WeberGestureListener(WeberWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (longPress) {
            webView.onLongPress();
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        longPress = false;
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        longPress = true;
    }
}
