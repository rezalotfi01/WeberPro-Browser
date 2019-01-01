package com.github.rezalotfi01.weberpro.View;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

import com.github.rezalotfi01.weberpro.R;

public class WeberContextWrapper extends ContextWrapper {
    private final Context context;

    public WeberContextWrapper(Context context) {
        super(context);
        this.context = context;
        this.context.setTheme(R.style.BrowserActivityTheme);
    }

    @Override
    public Resources.Theme getTheme() {
        return context.getTheme();
    }
}
