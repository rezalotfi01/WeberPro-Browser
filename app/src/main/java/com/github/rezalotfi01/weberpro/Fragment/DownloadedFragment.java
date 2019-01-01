package com.github.rezalotfi01.weberpro.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.rezalotfi01.weberpro.Activity.DownloadActivity;
import com.github.rezalotfi01.weberpro.Models.Downloadable;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils;
import com.github.rezalotfi01.weberpro.View.DownloadsListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadedFragment extends Fragment {

    private View rootView;
    private Downloadable[] downloadables;

    private final String TAG = "Weber Tag";

    private boolean isItemSelected;
    private ArrayList<Integer> selectedPositions;
    private DownloadActivity baseActivity;
    private Context context;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver errorReceiver;
    private BroadcastReceiver completeReceiver;
    private BroadcastReceiver pleaseDeleteReceiver;
    private BroadcastReceiver pleaseDeleteWithFileReceiver;
    private BroadcastReceiver pleaseShareReceiver;
    public DownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        baseActivity = (DownloadActivity)getActivity();

        selectedPositions = new ArrayList<>();

        initBroadcasts();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_downloaded, container, false);
        rootView.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.white));

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Do Something
                        if (isItemSelected){
                            isItemSelected = false;
                            initList();
                            baseActivity.unInitForSelected();

                            return true;
                        }
                    }
                }
                return false;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        initList();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterReceivers();
    }

    private void initList() {
        selectedPositions = new ArrayList<>();


        List<DownloadEntity> downloadEntities = DownloadDBUtils.getFinishedDownloads(context);
        if (downloadEntities != null) {
            downloadables = new Downloadable[downloadEntities.size()];
            for (int i = 0; i < downloadEntities.size(); i++) {
                DownloadEntity currentEntity = downloadEntities.get(i);
                int drawableIcon = checkStatusAndGetIcon(currentEntity);
                downloadables[i] = new Downloadable(currentEntity.getFileName(), currentEntity.getSpeed()
                        , currentEntity.getRemainingTime(), currentEntity.getDownloadedSize(), currentEntity.getPercent()
                        , drawableIcon, currentEntity.getToken(), currentEntity.getURL(), currentEntity.getPath());
            }
            ListView listView = rootView.findViewById(R.id.list_downloaded);
            DownloadsListAdapter listAdapter = new DownloadsListAdapter(context, R.layout.single_download_item, downloadables);
            listView.setAdapter(listAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    if (isItemSelected)
                    {
                        if (selectedPositions.contains(position)){

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                view.setBackgroundColor(context.getColor(R.color.transparent));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.transparent));
                            }
                            selectedPositions.remove(Integer.valueOf(position));
                            if (selectedPositions.size() <= 0){
                                baseActivity.unInitForSelected();
                            }

                            Log.e(TAG, "onItemClick: Set Original Background" );
                        }else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                view.setBackgroundColor(context.getColor(R.color.blue_300));
                            } else {
                                view.setBackgroundColor(getResources().getColor(R.color.blue_300));
                            }
                            selectedPositions.add(position);
                        }

                    }else {
                        String filePath = downloadables[position].getFileSavedAddress()+ File.separator+downloadables[position].getFileName();
                        GeneralUtils.openFile(baseActivity, filePath);
                    }
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    isItemSelected = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        view.setBackgroundColor(context.getColor(R.color.blue_300));
                    }else {
                        view.setBackgroundColor(getResources().getColor(R.color.blue_300));
                    }

                    selectedPositions.add(position);
                    baseActivity.initForSelected();

                    return true;
                }
            });


        }
    }

    private void initBroadcasts(){
        broadcastManager = LocalBroadcastManager.getInstance(context);

        completeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();
                Log.e(TAG, "onReceive: Complete Listened in Downloaded Fragment !");
            }
        };
        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initList();
                Log.e(TAG, "onReceive: Error Listened in Downloaded Fragment !");
            }
        };

        pleaseDeleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //delete from db
                int index = -1024;
                for (int i = 0; i < selectedPositions.size(); i++) {
                    index = selectedPositions.get(i);
                    DownloadDBUtils.deleteDownloadByURL(context,downloadables[index].getFileURL());
                }
                initList();
                Log.e(TAG, "onReceive:PleaseDelete Listened in Downloaded Fragment !");
            }
        };

        pleaseDeleteWithFileReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //delete with File
                int index = -1024;
                for (int i = 0; i < selectedPositions.size(); i++) {
                    index = selectedPositions.get(i);
                    DownloadDBUtils.deleteDownloadByURL(context,downloadables[index].getFileURL());

                    File dFile = new File(downloadables[index].getFileSavedAddress()
                            + File.separator + downloadables[index].getFileName());
                    dFile.delete();
                }
                initList();
                Log.e(TAG, "onReceive: PleaseDeleteWithFile Listened in Downloaded Fragment !");
            }
        };

        pleaseShareReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //share files
                int index = selectedPositions.get(0);
                File sharingFile = new File(downloadables[index].getFileSavedAddress()
                        +File.separator+downloadables[index].getFileName());
                Uri fileUri = Uri.fromFile(sharingFile);

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                sharingIntent.setType("*/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM,fileUri);

                context.startActivity(Intent.createChooser(sharingIntent,"").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        };
    }

    private void registerReceivers(){
        broadcastManager.registerReceiver(completeReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_FINISH));
        broadcastManager.registerReceiver(errorReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_ERROR));
        broadcastManager.registerReceiver(pleaseDeleteReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_PLEASE_DELETE_DOWNLOADS_WITHOUT_FILE));
        broadcastManager.registerReceiver(pleaseDeleteWithFileReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_PLEASE_DELETE_DOWNLOADS_WITH_FILE));
        broadcastManager.registerReceiver(pleaseShareReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_PLEASE_SHARE_DOWNLOADED_FILES));
    }
    private void unRegisterReceivers() {
        broadcastManager.unregisterReceiver(errorReceiver);
        broadcastManager.unregisterReceiver(completeReceiver);
        broadcastManager.unregisterReceiver(pleaseDeleteWithFileReceiver);
        broadcastManager.unregisterReceiver(pleaseDeleteReceiver);
        broadcastManager.unregisterReceiver(pleaseShareReceiver);
    }

    private int checkStatusAndGetIcon(DownloadEntity entity){
        int icon = -1;
/*        if (entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_ERROR)){
            icon = R.drawable.ic_download_error;
            Log.e(TAG, "checkStatusAndGetIcon, Status stop detected : "+entity.getStatus());
        }else*/ if (entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_COMPLETED)){
            icon = R.drawable.ic_download_success;
            Log.e(TAG, "checkStatusAndGetIcon, Status resume detected : "+entity.getStatus());
        }
        if (icon == -1){
            Log.e(TAG, "checkStatusAndGetIcon: failed in downloaded...imgResource set to -1");
        }
        return icon;
    }




}
