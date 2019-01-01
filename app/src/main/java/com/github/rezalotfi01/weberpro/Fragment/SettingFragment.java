package com.github.rezalotfi01.weberpro.Fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;

import com.github.rezalotfi01.weberpro.Activity.AboutActivity;
import com.github.rezalotfi01.weberpro.Activity.ClearActivity;
import com.github.rezalotfi01.weberpro.Activity.TokenActivity;
import com.github.rezalotfi01.weberpro.Activity.WhitelistActivity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Task.ExportBookmarksTask;
import com.github.rezalotfi01.weberpro.Task.ExportWhitelistTask;
import com.github.rezalotfi01.weberpro.Utils.IntentUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String LICENSE_TITLE = "LICENSE_TITLE";
    private static final String LICENSE_CONTENT = "LICENSE_CONTENT";
    private static final String LICENSE_AUTHOR = "LICENSE_AUTHOR";
    private static final String LICENSE_URL = "LICENSE_URL";
    public static final int LOCATION_PERMISSION_CODE = 2345;

    private ListPreference searchEngine;
    private ListPreference notiPriority;
    private ListPreference tabPosition;
    private ListPreference volumeControl;
    private ListPreference userAgent;
    private ListPreference rendering;
    private ListPreference bubbleButtonList;
    SwitchPreference locationPreference;

    private String[] seEntries;
    private String[] npEntries;
    private String[] tpEntries;
    private String[] vcEntries;
    private String[] ucEntries;
    private String[] rdEntries;


    private boolean spChange = false;

    public SettingFragment() {
    }

    public boolean isSPChange() {
        return spChange;
    }

    private boolean dbChange = false;
    public boolean isDBChange() {
        return dbChange;
    }
    public void setDBChange(boolean dbChange) {
        this.dbChange = dbChange;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_setting);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater,container ,savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        sp.registerOnSharedPreferenceChangeListener(this);
        String summary;

        seEntries = getResources().getStringArray(R.array.setting_entries_search_engine);
        searchEngine = (ListPreference) findPreference(getString(R.string.sp_search_engine));
        int num = Integer.valueOf(sp.getString(getString(R.string.sp_search_engine), "0"));
        if (0 <= num && num <= 3) {
            summary = seEntries[num];
            searchEngine.setSummary(summary);
        } else {
            summary = getString(R.string.setting_summary_search_engine_custom);
            searchEngine.setSummary(summary);
        }

        tpEntries = getResources().getStringArray(R.array.setting_entries_tab_position);
        summary = tpEntries[Integer.valueOf(sp.getString(getString(R.string.sp_anchor), "1"))];
        tabPosition = (ListPreference) findPreference(getString(R.string.sp_anchor));
        tabPosition.setSummary(summary);

        vcEntries = getResources().getStringArray(R.array.setting_entries_volume_control);
        summary = vcEntries[Integer.valueOf(sp.getString(getString(R.string.sp_volume), "1"))];
        volumeControl = (ListPreference) findPreference(getString(R.string.sp_volume));
        volumeControl.setSummary(summary);

        ucEntries = getResources().getStringArray(R.array.setting_entries_user_agent);
        userAgent = (ListPreference) findPreference(getString(R.string.sp_user_agent));
        num = Integer.valueOf(sp.getString(getString(R.string.sp_user_agent), "0"));
        if (0 <= num && num <= 1) {
            summary = ucEntries[num];
            userAgent.setSummary(summary);
        } else {
            summary = getString(R.string.setting_summary_user_agent_custom);
            userAgent.setSummary(summary);
        }
        rdEntries = getResources().getStringArray(R.array.setting_entries_rendering);
        summary = rdEntries[Integer.valueOf(sp.getString(getString(R.string.sp_rendering), "0"))];
        rendering = (ListPreference) findPreference(getString(R.string.sp_rendering));
        rendering.setSummary(summary);

        bubbleButtonList = (ListPreference)findPreference("SP_BUBBLE_BUTTON_STATUS");
/*        bubbleButtonList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                try {
                    //String newString = String.valueOf(newValue);
                    int newItem = (int)newValue; //Integer.valueOf(newString);
                    String[] array = getResources().getStringArray(R.array.setting_entries_bubble_button_status);
                    preference.setSummary(array[newItem]);
                    WeberToast.show(getActivity(), R.string.toast_need_restart);
                }catch (Exception e){
                    Log.e("Weber TAG", "onPreferenceChange Exception : "+e.toString());
                }
                return true;
            }
        });*/
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        switch (preference.getTitleRes()) {
            case R.string.setting_title_whitelist:
                Intent toWhitelist = new Intent(getActivity(), WhitelistActivity.class);
                getActivity().startActivity(toWhitelist);
                break;
            case R.string.setting_title_export_whilelist:
                new ExportWhitelistTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_whilelist:
                Intent importWhitelist = new Intent(Intent.ACTION_GET_CONTENT);
                importWhitelist.setType(IntentUtils.INTENT_TYPE_TEXT_PLAIN);
                importWhitelist.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importWhitelist, IntentUtils.REQUEST_WHITELIST);
                break;
            case R.string.setting_title_token:
                Intent toToken = new Intent(getActivity(), TokenActivity.class);
                getActivity().startActivity(toToken);
                break;
            case R.string.setting_title_export_bookmarks:
                new ExportBookmarksTask(getActivity()).execute();
                break;
            case R.string.setting_title_import_bookmarks:
                Intent importBookmarks = new Intent(Intent.ACTION_GET_CONTENT);
                importBookmarks.setType(IntentUtils.INTENT_TYPE_TEXT_PLAIN);
                importBookmarks.addCategory(Intent.CATEGORY_OPENABLE);
                getActivity().startActivityForResult(importBookmarks, IntentUtils.REQUEST_BOOKMARKS);
                break;
            case R.string.setting_title_clear_control:
                Intent clearControl = new Intent(getActivity(), ClearActivity.class);
                getActivity().startActivityForResult(clearControl, IntentUtils.REQUEST_CLEAR);
                break;
            case R.string.setting_title_version:
                break;
            case R.string.setting_title_about:
                Intent openAboutIntent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(openAboutIntent);
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        spChange = true;
        if (key.equals(getString(R.string.sp_search_engine))) {
            int num = Integer.valueOf(sp.getString(key, "0"));
            if (0 <= num && num <= 4) {
                searchEngine.setSummary(seEntries[num]);
            } else {
                searchEngine.setValue("5");
                searchEngine.setSummary(R.string.setting_summary_search_engine_custom);
            }
        } else if (key.equals(getString(R.string.sp_notification_priority))) {
            String summary = npEntries[Integer.valueOf(sp.getString(key, "0"))];
            notiPriority.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_anchor))) {
            String summary = tpEntries[Integer.valueOf(sp.getString(key, "1"))];
            tabPosition.setSummary(summary);
            WeberToast.Companion.show(getActivity(), R.string.toast_need_restart);
        } else if (key.equals(getString(R.string.sp_volume))) {
            String summary = vcEntries[Integer.valueOf(sp.getString(key, "1"))];
            volumeControl.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_user_agent))) {
            int num = Integer.valueOf(sp.getString(key, "0"));
            if (0 <= num && num <= 1) {
                userAgent.setSummary(ucEntries[num]);
            } else {
                userAgent.setValue("2");
                userAgent.setSummary(R.string.setting_summary_user_agent_custom);
            }
        } else if (key.equals(getString(R.string.sp_rendering))) {
            String summary = rdEntries[Integer.valueOf(sp.getString(key, "0"))];
            rendering.setSummary(summary);
        } else if (key.equals(getString(R.string.sp_cookies))) {
            CookieManager manager = CookieManager.getInstance();
            manager.setAcceptCookie(sp.getBoolean(getString(R.string.sp_cookies), true));
        } else if (key.equals("SP_BUBBLE_BUTTON_STATUS")){
            try {
                String newString = sp.getString(key,"-1");
                int newItem =  Integer.valueOf(newString);
                if (newItem > -1) {
                    if (newItem < 2) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Settings.canDrawOverlays(getActivity())) {

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("")
                                        .setMessage(getString(R.string.permission_draw_request))
                                        .setPositiveButton(R.string.dialog_button_positive, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // request draw permission
                                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                        Uri.parse("package:" + getActivity().getPackageName()));
                                                startActivityForResult(intent, 7194);
                                            }
                                        })
                                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                        Uri.parse("package:" + getActivity().getPackageName()));
                                                startActivityForResult(intent, 7194);
                                            }
                                        })
                                        .show();
                            }
                        }
                        String[] array = getResources().getStringArray(R.array.setting_entries_bubble_button_status);
                        bubbleButtonList.setSummary(array[newItem]);
                        WeberToast.Companion.show(getActivity(), R.string.toast_need_restart);
                    }else {
                        String[] array = getResources().getStringArray(R.array.setting_entries_bubble_button_status);
                        bubbleButtonList.setSummary(array[newItem]);
                        WeberToast.Companion.show(getActivity(), R.string.toast_need_restart);
                    }

                }else {
                    bubbleButtonList.setSummary(getString(R.string.setting_summary_bub));
                }
            }catch (Exception e){
                Log.e("Weber TAG", "onPreferenceChange Exception : "+e.toString());
            }
        }else if(key.equals("SP_LOCATION_9")){
            boolean newValue = sp.getBoolean(key,false);
            if (newValue){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if ((getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                            (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) )
                    {
                        //permission check and request
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_CODE);
                    }
                }

            }
        }
    }

}
