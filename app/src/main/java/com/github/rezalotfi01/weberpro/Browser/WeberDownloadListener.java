package com.github.rezalotfi01.weberpro.Browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import com.github.rezalotfi01.weberpro.Fragment.DialogDownloadFragment;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils;
import com.github.rezalotfi01.weberpro.Utils.IntentUtils;

public class WeberDownloadListener implements DownloadListener {
    private final Context context;

    public WeberDownloadListener(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
        final Context holder = IntentUtils.getContext();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultDownloader = Integer.valueOf((sp.getString("SP_DEFAULT_DOWNLOADER","0")));

        if (!(holder instanceof AppCompatActivity) || !(holder instanceof Activity)) {
            BrowserUtils.download(context, url, contentDisposition, mimeType);
        } else if (defaultDownloader == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder);
            builder.setCancelable(false);

            builder.setTitle(R.string.dialog_title_download);
            builder.setMessage(URLUtil.guessFileName(url, contentDisposition, mimeType));

            builder.setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BrowserUtils.download(holder, url, contentDisposition, mimeType);
                }
            });
            builder.setNegativeButton(R.string.dialog_button_negative, null);
            builder.create().show();

        }else {

            final AppCompatActivity activity = (AppCompatActivity) context;

            FragmentManager fm = activity . getSupportFragmentManager();
            DialogDownloadFragment dialogFragment = new DialogDownloadFragment ();

            Bundle argumentsBundle = new Bundle();
            argumentsBundle.putString("url",url);
            argumentsBundle.putString("content_dis",contentDisposition);
            argumentsBundle.putString("mime",mimeType);

            dialogFragment.setArguments(argumentsBundle);
            dialogFragment.setCancelable(true);
            dialogFragment.show(fm, "Download");

        }

    }
}
