package com.github.rezalotfi01.weberpro.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadDBUtils;
import com.github.rezalotfi01.weberpro.Database.DownloadsDB.DownloadEntity;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Service.DownloaderService;
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;
import com.github.rezalotfi01.weberpro.Utils.GeneralUtils;
import com.github.rezalotfi01.weberpro.View.WeberToast;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import is.arontibo.library.ElasticDownloadView;

public class SingleDownloadDetailsActivity extends WeberActivity {

    @InjectView(R.id.elastic_download_view)
    ElasticDownloadView mElasticDownloadView;
    ImageView pauseButton;
    private ImageView resumeButton;

    private Context context;
    private Activity thisActivity;
    private Intent receivedIntent;

    private String fileName;
    private String fileAddress;
    private String fileLink;
    private String tempFileLink;

    private DownloadEntity entity;

    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver progressReceiver;
    private BroadcastReceiver completeReceiver;
    private BroadcastReceiver errorReceiver;

    private boolean isElasticStretched;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(Color.parseColor("#A6463A"));
        }

        setContentView(R.layout.activity_single_download_details);
        receivedIntent = getIntent();

        isElasticStretched = false;

        thisActivity = this;
        ButterKnife.inject(thisActivity);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        context = SingleDownloadDetailsActivity.this;
        fileName = receivedIntent.getStringExtra("fileName");
        fileAddress = receivedIntent.getStringExtra("fileAddress");
        fileLink = receivedIntent.getStringExtra("URL");

        Log.e("Weber Tag", "onPostCreate ReceivedLink : "+fileLink);

        final float progressNumber = (float) receivedIntent.getDoubleExtra("percent",0);


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mElasticDownloadView.startIntro();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mElasticDownloadView.setProgress(progressNumber);
                        }catch (Exception e){
                            Log.e("Weber TAG", "Set Percent in first Exception : " + e.toString());
                        }
                        isElasticStretched = true;
                        registerReceivers();
                    }
                },850);
                Log.e("Weber TAG ", "run Elastic Download" );

            }
        }, 1000);


        entity = DownloadDBUtils.getDownloadByURL(context,fileLink);

        initBroadcasts();
        initButtons();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isElasticStretched) {
                registerReceivers();
            }
        }catch (Exception e){
            Log.e("Weber TAG", "onResumeSingleDownloadDetails: "+e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
            unRegisterReceivers();
        }catch (Exception e){
            Log.e("Weber TAG", "onPauseSingleDownloadDetails: "+e.toString());
        }
    }



    //My Utility Methods
    private void initBroadcasts(){
        broadcastManager = LocalBroadcastManager.getInstance(context);

        progressReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Do something
                tempFileLink = intent.getStringExtra(BroadcastUtils.URL_NAME);
                if (tempFileLink.equals(fileLink)) {
                    float receivedPercent = (float) intent.getIntExtra(BroadcastUtils.PERCENT_NAME, 0);
                    try {
                        mElasticDownloadView.setProgress(receivedPercent);
                    }catch (Exception e){
                        Log.e("Weber TAG", "onReceive Set Percent Exception : "+e.toString());
                    }
                }
            }
        };

        completeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tempFileLink = intent.getStringExtra(BroadcastUtils.URL_NAME);
                if (tempFileLink.equals(fileLink)) {
                    float receivedPercent = (float) intent.getIntExtra(BroadcastUtils.PERCENT_NAME, 100);
                    mElasticDownloadView.setProgress(receivedPercent);
                    mElasticDownloadView.success();
                    resumeButton.setImageResource(R.drawable.ic_download_detail_resume);
                    resumeButton.setEnabled(false);
                }
            }
        };

        errorReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tempFileLink = intent.getStringExtra(BroadcastUtils.URL_NAME);
                if (tempFileLink.equals(fileLink)) {
                    mElasticDownloadView.fail();
                    resumeButton.setImageResource(R.drawable.ic_download_detail_resume);
                }
            }
        };

        BroadcastReceiver pausedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tempFileLink = intent.getStringExtra(BroadcastUtils.URL_NAME);
                if (tempFileLink.equals(fileLink)) {
                    resumeButton.setImageResource(R.drawable.ic_download_detail_resume);
                }
            }
        };

    }

    private void registerReceivers(){
        broadcastManager.registerReceiver(progressReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_PROGRESS));
        broadcastManager.registerReceiver(completeReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_FINISH));
        broadcastManager.registerReceiver(errorReceiver, new IntentFilter(BroadcastUtils.INTENT_FILTER_DOWNLOAD_ERROR));
    }

    private void unRegisterReceivers(){
        broadcastManager.unregisterReceiver(progressReceiver);
        broadcastManager.unregisterReceiver(completeReceiver);
        broadcastManager.unregisterReceiver(errorReceiver);
    }

    private void initButtons(){
        ImageView shareButton = findViewById(R.id.button_download_detail_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_PAUSED) || entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_RESUMING))
                {
                    //share download link to other apps
                    String title = fileName;
                    String link = fileLink;
                    String shared = getString(R.string.share_page_sent);
                    String intentText = getString(R.string.intent_choose);
                    ShareCompat.IntentBuilder
                            .from(thisActivity) // getActivity() or activity field if within Fragment
                            .setText(title + "\n" + link + "\n" + "\n" + shared)
                            .setType("text/plain") // most general text sharing MIME type
                            .setChooserTitle(intentText)
                            .startChooser();
                }
            }
        });

        ImageView deleteButton = findViewById(R.id.button_download_detail_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show Dialog and delete Download

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.download_detail_are_you_sure))
                        .setPositiveButton(getString(R.string.download_details_dialog_delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on OK

                                Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                                pleasePauseIntent.putExtra("URL",fileLink);
                                broadcastManager.sendBroadcast(pleasePauseIntent);
                                broadcastManager.sendBroadcast(pleasePauseIntent);


                                entity.setStatus(DownloadEntity.FIELD_VALUE_STATUS_PAUSED);
                                //DownloadDBUtils.createOrUpdateInDatabase(entity,context);

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        DownloadDBUtils.deleteDownloadByURL(context,fileLink);

                                        finish();
                                    }
                                },250);
                            }
                        })
                        .setNeutralButton(getString(R.string.dialog_button_negative), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //  Your code when user clicked on cancel
                            }
                        })
                        .setNegativeButton(getString(R.string.download_details_dialog_is_delete_file), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on Delete with file
                                try {
                                    Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                                    pleasePauseIntent.putExtra("URL", fileLink);
                                    broadcastManager.sendBroadcast(pleasePauseIntent);
                                    broadcastManager.sendBroadcast(pleasePauseIntent);

                                    entity.setStatus(DownloadEntity.FIELD_VALUE_STATUS_PAUSED);
                                    //DownloadDBUtils.createOrUpdateInDatabase(entity,context);

                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            DownloadDBUtils.deleteDownloadByURL(context,fileLink);
                                            File dFile = new File(fileAddress + File.separator + fileName + ".temp");
                                            dFile.delete();

                                            finish();
                                        }
                                    },250);

                                }catch (Exception e){
                                    Log.e("Weber TAG", "onClick Delete Download Exception : "+e.toString());
                                }
                            }
                        }).create();
                dialog.show();
            }
        });



/*        pauseButton = (ImageView)findViewById(R.id.button_download_detail_pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                pleasePauseIntent.putExtra("fileName",fileName);
                broadcastManager.sendBroadcast(pleasePauseIntent);
            }
        });*/


        resumeButton = findViewById(R.id.button_download_detail_resume);
        if(entity.getStatus().equals(DownloadEntity.FIELD_VALUE_STATUS_RESUMING)){
            resumeButton.setImageResource(R.drawable.ic_download_detail_pause);
        }else {
            resumeButton.setImageResource(R.drawable.ic_download_detail_resume);
        }
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Resume download with opening service
                switch (entity.getStatus()) {
                    case DownloadEntity.FIELD_VALUE_STATUS_PAUSED:
                        if (GeneralUtils.isNetworkAvailable(context)) {
                            Intent openIntent = new Intent(context, DownloaderService.class);
                            openIntent.putExtra("url", entity.getURL());
                            openIntent.putExtra("name", fileName);
                            openIntent.putExtra("save_address", entity.getPath());
                            openIntent.putExtra("is_for_now", true);
                            openIntent.putExtra("is_resume", true);
                            startService(openIntent);
                            resumeButton.setImageResource(R.drawable.ic_download_detail_pause);
                            entity.setStatus(DownloadEntity.FIELD_VALUE_STATUS_RESUMING);
                        } else {
                            WeberToast.Companion.showPrettyToast(context, R.string.toast_download_cant_resume, TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                        }
                        break;
                    case DownloadEntity.FIELD_VALUE_STATUS_RESUMING:
                    case DownloadEntity.FIELD_VALUE_STATUS_IN_QUEUE:
                        Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_PAUSE_DOWNLOAD);
                        pleasePauseIntent.putExtra("URL", entity.getURL());
                        broadcastManager.sendBroadcast(pleasePauseIntent);
                        resumeButton.setImageResource(R.drawable.ic_download_detail_resume);
                        entity.setStatus(DownloadEntity.FIELD_VALUE_STATUS_PAUSED);
                        break;
                    default:
//                    WeberToast.showPrettyToast(context,R.string.toast_download_already_running, TastyToast.LENGTH_LONG,TastyToast.WARNING);
                        Log.e("Weber TAG ", "onClickResume , Download Entity Status : " + entity.getStatus());
                        break;
                }
            }
        });


    }

    private void changeButtonSrcWithAnim(Context c , final ImageView v, final int newImageResource){
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageResource(newImageResource);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }  });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

}
