package com.github.rezalotfi01.weberpro.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Fragment.SettingFragment;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Task.ImportBookmarksTask;
import com.github.rezalotfi01.weberpro.Task.ImportWhitelistTask;
import com.github.rezalotfi01.weberpro.Utils.IntentUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

public class SettingActivity extends WeberActivity {
    private SettingFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
//        ActionBar settingAction = getSupportActionBar();
        ActionBar settingAction = getSupportActionBar();

      //  LayoutInflater mInflater = LayoutInflater.from(this);
      //  View mCustomView = mInflater.inflate(R.layout.toolbar_app, null);
        //settingAction.setCustomView(mCustomView);
      //  settingAction.setDisplayShowCustomEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

/*        Toolbar toolbar = (Toolbar)findViewById(R.id.appBar);
        setSupportActionBar(toolbar);*/
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final int LOCATION_PERMISSION_CODE = SettingFragment.LOCATION_PERMISSION_CODE;
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE : {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    WeberToast.Companion.showPrettyToast(SettingActivity.this, R.string.permission_location_granted, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                }else {
                    // permission denied, boo!
                    WeberToast.Companion.showLongPrettyToast(SettingActivity.this, R.string.permission_location_denied, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    WeberToast.Companion.showPrettyToast(SettingActivity.this, R.string.permission_location_denied, TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("SP_LOCATION_9",false).apply();
                }

            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                IntentUtils.setDBChange(fragment.isDBChange());
                IntentUtils.setSPChange(fragment.isSPChange());
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            IntentUtils.setDBChange(fragment.isDBChange());
            IntentUtils.setSPChange(fragment.isSPChange());
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentUtils.REQUEST_BOOKMARKS) {
            if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
                WeberToast.Companion.show(this, R.string.toast_import_bookmarks_failed);
            } else {
                File file = new File(data.getData().getPath());
                new ImportBookmarksTask(fragment, file).execute();
            }
        } else if (requestCode == IntentUtils.REQUEST_WHITELIST) {
            if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
                WeberToast.Companion.show(this, R.string.toast_import_whitelist_failed);
            } else {
                File file = new File(data.getData().getPath());
                new ImportWhitelistTask(fragment, file).execute();
            }
        } else if (requestCode == IntentUtils.REQUEST_CLEAR) {
            if (resultCode == Activity.RESULT_OK && data != null && data.hasExtra(ClearActivity.DB_CHANGE)) {
                fragment.setDBChange(data.getBooleanExtra(ClearActivity.DB_CHANGE, false));
            }
        }
    }
}
