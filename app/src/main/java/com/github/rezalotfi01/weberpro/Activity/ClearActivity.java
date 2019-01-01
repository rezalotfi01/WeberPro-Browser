package com.github.rezalotfi01.weberpro.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Fragment.ClearFragment;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Utils.BrowserUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;

public class ClearActivity extends WeberActivity {
    public static final String DB_CHANGE = "DB_CHANGE";
    private boolean dbChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar clearActivityAction = getSupportActionBar();

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.toolbar_app, null);
//        clearActivityAction.setCustomView(mCustomView);
        clearActivityAction.setDisplayHomeAsUpEnabled(true);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ClearFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra(DB_CHANGE, dbChange);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.clear_menu_done_all:
                clear();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        Intent intent = new Intent();
        intent.putExtra(DB_CHANGE, dbChange);
        setResult(Activity.RESULT_OK, intent);
        finish();
        return true;
    }

    private void clear() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearBookmarks = sp.getBoolean(getString(R.string.sp_clear_bookmarks), false);
        boolean clearCache = sp.getBoolean(getString(R.string.sp_clear_cache), true);
        boolean clearCookie = sp.getBoolean(getString(R.string.sp_clear_cookie), false);
        boolean clearFormData = sp.getBoolean(getString(R.string.sp_clear_form_data), false);
        boolean clearHistory = sp.getBoolean(getString(R.string.sp_clear_history), true);
        boolean clearPasswords = sp.getBoolean(getString(R.string.sp_clear_passwords), false);

        ProgressDialog dialog;
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(getString(R.string.toast_wait_a_minute));
        dialog.show();

        if (clearBookmarks) {
            BrowserUtils.clearBookmarks(this);
        }
        if (clearCache) {
            BrowserUtils.clearCache(this);
        }
        if (clearCookie) {
            BrowserUtils.clearCookie(this);
        }
        if (clearFormData) {
            BrowserUtils.clearFormData(this);
        }
        if (clearHistory) {
            BrowserUtils.clearHistory(this);
        }
        if (clearPasswords) {
            BrowserUtils.clearPasswords(this);
        }

        dialog.hide();
        dialog.dismiss();

        dbChange = true;
        WeberToast.Companion.show(this, R.string.toast_clear_successful);
    }
}
