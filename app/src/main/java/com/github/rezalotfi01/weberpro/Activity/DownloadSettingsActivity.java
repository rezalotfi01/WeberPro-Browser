package com.github.rezalotfi01.weberpro.Activity;


import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.github.rezalotfi01.weberpro.Application.WeberApplication;
import com.github.rezalotfi01.weberpro.BaseClasses.WeberPreferenceActivity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Service.StartDownloadJobService;
import com.github.rezalotfi01.weberpro.Utils.FilesUtils;
import com.github.rezalotfi01.weberpro.Utils.WeberTimeUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;


public class DownloadSettingsActivity extends WeberPreferenceActivity {



    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            String preferenceKey = preference.getKey();
/*            if(preferenceKey.equals("file_picker_pref"))
            {
                String arr[]=stringValue.split(":");
                preference.setSummary(arr[0]);

                new FilesUtils().moveAllFiles(arr[0]);
            }else*/ if (preferenceKey.equals("multi_threading_type"))
            {
                int index = Integer.parseInt(stringValue) - 1;
                String smart = getContext().getString(R.string.pref_multi_threading_summary_smart);
                String normal = getContext().getString(R.string.pref_multi_threading_summary_normal);
                String multiThreadingTypes[] = new String[]{smart , normal};
                preference.setSummary(multiThreadingTypes[index]);
            }
            return true;
        }
    };


    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DownloadingPreferenceFragment downloadingPreferenceFragment = new DownloadingPreferenceFragment();

        getFragmentManager().beginTransaction().replace(android.R.id.content, downloadingPreferenceFragment).commit();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setupActionBar() {
        Toolbar toolbar = findViewById(R.id.app_bar_base);
        setSupportActionBar(toolbar);
    }



    private static Context getContext() {
        return WeberApplication.Companion.getContext();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }


    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


/*    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }*/


    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || DownloadingPreferenceFragment.class.getName().equals(fragmentName)
                || PlaningPreferenceFragment.class.getName().equals(fragmentName);
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DownloadingPreferenceFragment extends PreferenceFragment {
        Context context;
        String latestDownloadSaveAddress;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_downloading);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("file_picker_pref"));
            bindPreferenceSummaryToValue(findPreference("multi_threading_type"));

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
            latestDownloadSaveAddress = prefs.getString("file_picker_pref","");

            Preference filePickerPreference = findPreference("file_picker_pref");
            filePickerPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {

                    String stringValue = o.toString();
                    String arr[]=stringValue.split(":");
                    preference.setSummary(arr[0]);

                    new FilesUtils().moveAllFiles(latestDownloadSaveAddress.split(":")[0],arr[0].replace(":",""));

                    return true;
                }
            });

            final EditTextPreference perDownloadLimitPreference = (EditTextPreference)findPreference("max_volume_size_each_download");
            perDownloadLimitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newString = (String) newValue;
                    if (newValue != null){

                        if (!newString.trim().equals("")){
                            preference.setSummary(newString + " " + "MB");
                        }else {
                            ((EditTextPreference)preference).setText("0");
                        }

                    }else {
                        ((EditTextPreference)preference).setText("0");
                    }


                    return true;
                }
            });

            final EditTextPreference allDownloadLimitPreference = (EditTextPreference) findPreference("max_volume_size_all_downloads");
            allDownloadLimitPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String newString = (String) newValue;
                    SwitchPreference isSynchronousPref = (SwitchPreference)findPreference("downloads_number");
                    if (newValue != null){
                        if (!isSynchronousPref.isChecked()) {

                            if (!newString.trim().equals("")) {
                                preference.setSummary(newString + " " + "MB");
                            } else {
                                return false;
                            }

                        }else {
                            WeberToast.Companion.showLongPrettyToast(getActivity(),R.string.toast_please_disable_synchronous_download,TastyToast.LENGTH_LONG,TastyToast.WARNING);
                            return false;
                        }

                    }else {
                        return false;
                    }

                    return true;
                }
            });


            CheckBoxPreference isActiveDownloadSizeLimit = (CheckBoxPreference)findPreference("check_box_download_limit");
            isActiveDownloadSizeLimit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (newValue.equals(false)){
                        //set subPreferences to default
                        perDownloadLimitPreference.setText("0");
                        perDownloadLimitPreference.setSummary(getString(R.string.pref_downloading_size_each_download_summary));
                        allDownloadLimitPreference.setText("0");
                        allDownloadLimitPreference.setSummary(getString(R.string.pref_downloading_size_all_downloads_summary));
                        preference.setSummary(getString(R.string.pref_downloading_size_limits_checkbox_summary));
                    }else {
                        preference.setSummary(getString(R.string.pref_downloading_size_limits_checkbox_active));
                    }
                    return true;
                }
            });
            if ( isActiveDownloadSizeLimit.isChecked()){
                perDownloadLimitPreference.setSummary(perDownloadLimitPreference.getSharedPreferences().getString("max_volume_size_each_download","...")+" MB");
                allDownloadLimitPreference.setSummary(allDownloadLimitPreference.getSharedPreferences().getString("max_volume_size_all_downloads","...")+" MB");
            }

            //Preference Planing Codes
            //--------------------------------------------------------------------------------------------------
            context = getActivity();
/*            final Preference startTimePref = findPreference("planing_start_time");
            startTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    loadStartTimePicker(preference);
                    return true;
                }
            });

            final Preference stopTimePref = findPreference("planing_stop_time");
            stopTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    loadStopTimePicker(preference);
                    return true;
                }
            });

            Preference isActivePlaning = findPreference("check_box_planing_download");
            isActivePlaning.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (((Boolean)newValue).equals(false)) {
                        try {
                            int startJobToken = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).getInt("start_job_id",-34);
                            int stopJobToken = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE).getInt("stop_job_id",-34);
                            boolean isCanceledStart = false;
                            boolean isCanceledStop = false;
                            if (startJobToken >= 0) {
                                isCanceledStart  = JobManager.instance().cancel(startJobToken);
                            }
                            if (stopJobToken >= 0){
                                isCanceledStop = JobManager.instance().cancel(stopJobToken);
                            }
                            Log.e("Weber TAG", "onPreferenceChange is Canceled StartJobID : " + isCanceledStart +" StopJobID : "+isCanceledStop);
                            JobManager.instance().cancelAll();
                        } catch (Exception e) {
                            JobManager.instance().cancelAll();
                            Log.e("Weber TAG", "onPreferenceChange cancel job when active change Exception : " + e.toString());
                        }
                        startTimePref.setSummary(getString(R.string.pref_planing_start_time_summary));
                        stopTimePref.setSummary(getString(R.string.pref_planing_stop_time_summary));
                        startTimePref.getEditor().putString("time_value_start",getString(R.string.pref_planing_start_time_summary)).commit();
                        stopTimePref.getEditor().putString("time_value_stop",getString(R.string.pref_planing_stop_time_summary)).commit();

                        Intent intent = new Intent(context, StartDownloadJobService.class);
                        intent.putExtra("is_stop",true);
                        context.startService(intent);
                    }
                    return true;
                }
            });

            if ( ((CheckBoxPreference)isActivePlaning).isChecked()){
                startTimePref.setSummary(startTimePref.getSharedPreferences().getString("time_value_start"," "));
                stopTimePref.setSummary(stopTimePref.getSharedPreferences().getString("time_value_stop"," "));
            }*/
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {

                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


        public  void loadStartTimePicker(final Preference startTimePreference){
            final Context ctx = getActivity();
            Calendar now = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    //set timer for start download in download service

                    long pickedTimeMilli;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pickedTimeMilli = (timePicker.getHour() * 60 * 60 * 1000) + (timePicker.getMinute() * 60 * 1000);
                    }else{
                        pickedTimeMilli = (timePicker.getCurrentHour() * 60 * 60 * 1000) + (timePicker.getCurrentMinute() * 60 * 1000);
                    }
                    long timesInterval = new WeberTimeUtils().getYourAndCurrentTimeInterval(pickedTimeMilli);

                    Intent intent = new Intent(context, StartDownloadJobService.class);
                    intent.putExtra("times_interval",timesInterval);
                    intent.putExtra("job_type","start");
                    context.startService(intent);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        startTimePreference.setSummary(String.valueOf(timePicker.getHour())+":"+String.valueOf(timePicker.getMinute()));
                        startTimePreference.getEditor().putString("time_value_start",String.valueOf(timePicker.getHour())+":"+String.valueOf(timePicker.getMinute())).commit();
                    }else {
                        startTimePreference.setSummary(String.valueOf(timePicker.getCurrentHour())+":"+String.valueOf(timePicker.getCurrentMinute()));
                        startTimePreference.getEditor().putString("time_value_start",String.valueOf(timePicker.getCurrentHour())+":"+String.valueOf(timePicker.getCurrentMinute())).commit();
                    }
                    //Log.e("TAG", "onTimeSet: " + timePicker.getCurrentHour().toString()+":"+timePicker.getCurrentMinute().toString());
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }

        public  void loadStopTimePicker(final Preference stopTimePreference){
            Calendar now = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    //set timer for stop download in download service
                    int pickedHour = -43;
                    int pickedMinute = -43;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        pickedHour = timePicker.getHour();
                        pickedMinute = timePicker.getMinute();
                    }else {
                        pickedHour = timePicker.getCurrentHour();
                        pickedMinute = timePicker.getCurrentMinute();
                    }

                    long pickedTimeMilli = (pickedHour * 60 * 60 * 1000) + (pickedMinute * 60 * 1000);
                    long timesInterval = new WeberTimeUtils().getYourAndCurrentTimeInterval(pickedTimeMilli);

                    Intent intent = new Intent(context, StartDownloadJobService.class);
                    intent.putExtra("times_interval",timesInterval);
                    intent.putExtra("job_type","stop");
                    context.startService(intent);

                    stopTimePreference.setSummary(String.valueOf(pickedHour)+":"+String.valueOf(pickedMinute));
                    stopTimePreference.getEditor().putString("time_value_stop",String.valueOf(pickedHour)+":"+String.valueOf(pickedMinute)).commit();

                    Log.e("TAG", "onStopTimeSet: " +String.valueOf(pickedHour)+":"+String.valueOf(pickedMinute));
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);

            timePickerDialog.show();
        }

    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PlaningPreferenceFragment extends PreferenceFragment {
        Context context;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_planing);
            setHasOptionsMenu(true);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), DownloadSettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
