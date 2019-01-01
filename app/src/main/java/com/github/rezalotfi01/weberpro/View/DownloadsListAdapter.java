package com.github.rezalotfi01.weberpro.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daasuu.ahp.AnimateHorizontalProgressBar;
import com.github.rezalotfi01.weberpro.Models.Downloadable;
import com.github.rezalotfi01.weberpro.R;

/**
 * Created  on 11/03/2016.
 */
public class DownloadsListAdapter extends ArrayAdapter<Downloadable> {
    private final Context context;
    BroadcastReceiver progressReceiver;

    private final Downloadable[] downloadObjects;
    private final int layoutResourceId;

    public DownloadsListAdapter(Context context, int layoutResourceId, Downloadable[] data) {
        super(context, layoutResourceId, data);
        // TODO Auto-generated constructor stub
        this.context = context;
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
        downloadObjects = data;
        this.layoutResourceId = layoutResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(layoutResourceId,parent,false);
        //Alternative inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final Downloadable downloadableObj = downloadObjects[position];
        //Alternative Downloadable downloadableObj = getItem(position);


        ImageView imgDownloadStatus = convertView.findViewById(R.id.img_download_status);
        AnimateHorizontalProgressBar progressDownload = convertView.findViewById(R.id.progress_download_info);
        TextView txtFileName = convertView.findViewById(R.id.txt_download_progress_file_name);
        TextView txtSpeed = convertView.findViewById(R.id.txt_download_progress_speed);
        TextView txtRemaining = convertView.findViewById(R.id.txt_download_progress_remaining_time);
        TextView txtDownloadedSize = convertView.findViewById(R.id.txt_download_progress_downloaded_size);

        imgDownloadStatus.setImageResource(downloadableObj.getImgStatusResource());

        progressDownload.setMax(1000);
        progressDownload.setProgress((int)(downloadableObj.getPercent() * 10));

        txtFileName.setText(downloadableObj.getFileName().trim());
        txtSpeed.setText(downloadableObj.getSpeed());
        txtRemaining.setText(downloadableObj.getRemainingTime());
        txtDownloadedSize.setText(downloadableObj.getDownloadedSize());

 /*       progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getIntExtra(BroadcastUtils.ID_NAME, -34) == downloadableObj.getToken()) {
                        progressDownload.setProgress((intent.getIntExtra(BroadcastUtils.PERCENT_NAME, -34)));
                    }
                }catch (Exception e){
                    Log.e("Weber TAG", "onReceiveProgress in List Adapter : "+e.toString());
                }
            }
        };

        try {
            broadcastManager.registerReceiver(progressReceiver,new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PROGRESS));
        }catch (Exception e){
            Log.e("Weber TAG ", "Register Receiver in Lost Adapter Exception : "+e.toString());
        }*/

        return convertView;
    }


}
