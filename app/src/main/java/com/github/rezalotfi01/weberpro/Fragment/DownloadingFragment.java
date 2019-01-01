package com.github.rezalotfi01.weberpro.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;
import com.github.rezalotfi01.weberpro.Activity.SingleDownloadDetailsActivity;
import com.github.rezalotfi01.weberpro.Models.Downloadable;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Service.DownloaderService;
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;
import com.github.rezalotfi01.weberpro.View.DownloadsListAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadingFragment extends Fragment{
    FloatingActionButton floatingActionButton;
    FABToolbarLayout toolbarLayout;
    View rootView;
    ListView listView;
    Downloadable[] downloadables;
    DownloadsListAdapter listAdapter;

    Context context;
    AppCompatActivity baseActivity;
    final String TAG = "Weber Tag";

    LocalBroadcastManager broadcastManager;
    BroadcastReceiver progressReceiver , pendingReceiver , pauseReceiver , errorReceiver , completeReceiver;
    public DownloadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        baseActivity = (AppCompatActivity)getActivity();
        initBroadcasts();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            initList();
        }catch (Exception e){
            Log.e(TAG, "onResume initList Excpetion : "+e.toString());
        }
        registerBroadcasts();
    }

    @Override
    public void onPause() {
        super.onPause();

        unRegisterBroadcasts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Alternative code inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.fragment_downloading, container, false);
        rootView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFABToolbar();
    }


    //My Methods

    private void initList(){
        List<DownloadEntity> downloadEntities = DownloadDBUtils.getNotFinishedDownloads(context);
        if (downloadEntities != null) {
            downloadables = new Downloadable[downloadEntities.size()];
            for (int i = 0; i < downloadEntities.size(); i++) {
                DownloadEntity currentEntity = downloadEntities.get(i);
                int drawableIcon = checkStatusAndGetIcon(currentEntity);
                downloadables[i] = new Downloadable(currentEntity.getFileName(),currentEntity.getSpeed()
                        ,currentEntity.getRemainingTime(),currentEntity.getDownloadedSize(),currentEntity.getPercent()
                        , drawableIcon,currentEntity.getToken(),currentEntity.getURL(),currentEntity.getPath());
            }

            listView = rootView.findViewById(R.id.list_downloading);
            listAdapter = new DownloadsListAdapter(context, R.layout.single_download_item, downloadables);
            listView.setAdapter(listAdapter);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Intent switchIntent = new Intent(context, SingleDownloadDetailsActivity.class);
                    switchIntent.putExtra("percent",downloadables[position].getPercent());
                    switchIntent.putExtra("fileName",downloadables[position].getFileName());
                    switchIntent.putExtra("fileAddress",downloadables[position].getFileSavedAddress());
                    switchIntent.putExtra("URL",downloadables[position].getFileURL());

/*                    ActSwitchAnimTool activitySwitcher = new ActSwitchAnimTool(getActivity())
                            .setAnimType(ActSwitchAnimTool.MODE_SPREAD)
                            .target(view)
                            .setmColorStart(getResources().getColor(R.color.blue_500))
                            .setmColorEnd(getResources().getColor(R.color.blue_500))
                            .startActivity(switchIntent,false);
                    activitySwitcher.build();*/

                    startActivity(switchIntent);
                }
            });
        }
    }

    private void initFABToolbar(){
        toolbarLayout = rootView.findViewById(R.id.fabtoolbar);

        ImageView btnAddDownload = rootView.findViewById(R.id.toolbar_add);
        ImageView btnStartAll = rootView.findViewById(R.id.toolbar_start_all);
        ImageView btnStopAll = rootView.findViewById(R.id.toolbar_stop_all);

        floatingActionButton = rootView.findViewById(R.id.fabtoolbar_fab);
        floatingActionButton.bringToFront();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick: Run OnClick");
                toolbarLayout.show();
            }
        });

        if(rootView == null){
            return;
        }
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && toolbarLayout.isToolbar()){
                    // handle back button's click listener
                    toolbarLayout.hide();
                    return true;
                }

                return false;
            }
        });

        btnAddDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show Dialog and Add Download Task
                FragmentManager fm = baseActivity.getSupportFragmentManager();
                DialogDownloadFragment dialogFragment = new DialogDownloadFragment ();
                dialogFragment.setCancelable(true);
                dialogFragment.show(fm, "Download");

                Log.e("TAG", "onClickAdd : Add Download Task Clicked !!");
            }
        });

        btnStartAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start All Download Tasks
                List<DownloadEntity> downloadEntities = DownloadDBUtils.getNotFinishedDownloads(context);
                for (int i = 0; i < downloadEntities.size(); i++) {
                    if (downloadEntities.get(i).getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_PAUSED)) {
                        Intent openIntent = new Intent(context, DownloaderService.class);
                        openIntent.putExtra("url", downloadEntities.get(i).getURL());
                        openIntent.putExtra("name", downloadEntities.get(i).getFileName());
                        openIntent.putExtra("save_address", downloadEntities.get(i).getPath());
                        openIntent.putExtra("is_for_now", true);
                        openIntent.putExtra("is_resume", true);
                        context.startService(openIntent);
                        initList();
                    }

                }

                Log.e("TAG", "onClickStart : Start Download Tasks Clicked !!");
            }
        });

        btnStopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Stop All Download Tasks
                Intent pleasePauseAllIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_ALL_DOWNLOADS);
                broadcastManager.sendBroadcast(pleasePauseAllIntent);

                initList();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            initList();
                        }catch (Exception e){
                            Log.e("Weber TAG", "Set Percent in first Exception : " + e.toString());
                        }
                    }
                },350);

                Log.e("TAG", "onClickStop : Stop Download Tasks Clicked !!");
            }
        });

    }

    private void initBroadcasts(){
        broadcastManager = LocalBroadcastManager.getInstance(context);

        pendingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();
                Log.e(TAG, "onReceive: Pending Listened in Downloading Fragment !");
            }
        };

        progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int index= -1024;
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    if (intent.getStringExtra(BroadcastUtils.URL_NAME).equals(downloadables[i].getFileURL())){
                        index = i;
                    }
                }
                if (index >= 0) {
                    View vi = listView.getChildAt(index - listView.getFirstVisiblePosition());

                    AnimateHorizontalProgressBar progressBarX = vi.findViewById(R.id.progress_download_info);
                    TextView textSpeed = vi.findViewById(R.id.txt_download_progress_speed);
                    TextView textRemaining = vi.findViewById(R.id.txt_download_progress_remaining_time);
                    TextView textSize = vi.findViewById(R.id.txt_download_progress_downloaded_size);

                    progressBarX.setProgress(intent.getIntExtra(BroadcastUtils.PERCENT_NAME, 0)*10);
                    Log.e(TAG, "onReceiveProgress : " + intent.getIntExtra(BroadcastUtils.PERCENT_NAME, 0));
                    textSpeed.setText(intent.getStringExtra(BroadcastUtils.SPEED_NAME));
                    textRemaining.setText(intent.getStringExtra(BroadcastUtils.REMAINING_TIME_NAME));
                    textSize.setText(intent.getStringExtra(BroadcastUtils.SO_FAR_DOWNLOADED_NAME));

                    Log.e(TAG, "onReceive: Progress Listened in Downloading Fragment !");
                }
            }
        };

        pauseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();
                Log.e(TAG, "onReceive: Pause Listened in Downloading Fragment !");
            }
        };

        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //send item to downloaded fragment
                initList();
                Log.e(TAG, "onReceive: Error Listened in Downloading Fragment !");
            }
        };

        completeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();
                Log.e(TAG, "onReceive: Complete Listened in Downloading Fragment !");
            }
        };

    }


    private void registerBroadcasts(){
        broadcastManager.registerReceiver(pendingReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PENDING));
        broadcastManager.registerReceiver(progressReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PROGRESS));
        broadcastManager.registerReceiver(pauseReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PAUSED));
        broadcastManager.registerReceiver(errorReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_ERROR));
        broadcastManager.registerReceiver(completeReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_FINISH));
    }

    private void unRegisterBroadcasts(){
        broadcastManager.unregisterReceiver(pendingReceiver);
        broadcastManager.unregisterReceiver(progressReceiver);
        broadcastManager.unregisterReceiver(pauseReceiver);
        broadcastManager.unregisterReceiver(errorReceiver);
        broadcastManager.unregisterReceiver(completeReceiver);
    }

    private int checkStatusAndGetIcon(DownloadEntity entity){
        int icon = -1;
/*        if ( !(JobManager.instance().getAllJobs().size() <= 0) )
        {
            icon = R.drawable.ic_download_timer;
        }else */
        switch (entity.getStatus()) {
            case DownloadEntity.FIELD_VALUE_STATUS_ERROR:
                icon = R.drawable.ic_download_error;
                Log.e(TAG, "checkStatusAndGetIcon, Status stop detected : " + entity.getStatus());
                break;
            case DownloadEntity.FIELD_VALUE_STATUS_PAUSED:
                icon = R.drawable.ic_download_stop;
                Log.e(TAG, "checkStatusAndGetIcon, Status stop detected : " + entity.getStatus());
                break;
            case DownloadEntity.FIELD_VALUE_STATUS_RESUMING:
                icon = R.drawable.ic_download_resume;
                Log.e(TAG, "checkStatusAndGetIcon, Status resume detected : " + entity.getStatus());
                break;
            case DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE:
                icon = R.drawable.ic_download_queue;
                break;
        }
        if (icon == -1){
            Log.e(TAG, "checkStatusAndGetIcon: failed and imgAddress Set to -1...");
        }
        return icon;
    }
}
