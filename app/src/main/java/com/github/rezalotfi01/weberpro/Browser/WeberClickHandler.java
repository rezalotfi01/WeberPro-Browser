package com.github.rezalotfi01.weberpro.Browser;

import android.os.Handler;
import android.os.Message;

import com.github.rezalotfi01.weberpro.View.WeberWebView;

public class WeberClickHandler extends Handler {
    private final WeberWebView webView;

    public WeberClickHandler(WeberWebView webView) {
        super();
        this.webView = webView;
    }

    @Override
    public void handleMessage(Message message) {
        super.handleMessage(message);
        webView.getBrowserController().onLongPress(message.getData().getString("url"));
    }
}
