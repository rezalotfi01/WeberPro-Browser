package com.github.rezalotfi01.weberpro.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.Fragment.DownloadedFragment;
import com.github.rezalotfi01.weberpro.Fragment.DownloadingFragment;
import com.github.rezalotfi01.weberpro.R;
import com.github.rezalotfi01.weberpro.Utils.BroadcastUtils;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends WeberActivity {

    private ImageView buttonSettingDelete;
    private ImageView buttonShare;

    LocalBroadcastManager broadcastManager;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_download);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        broadcastManager = LocalBroadcastManager.getInstance(context);

        unInitForSelected();

        ViewPager viewPager = findViewById(R.id.downloadsViewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.blue_400));

    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DownloadingFragment(), getString(R.string.downloading_caption));
        adapter.addFragment(new DownloadedFragment(), getString(R.string.downloaded_caption));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void initForSelected(){
        buttonSettingDelete.setImageResource(R.drawable.ic_delete);
        buttonSettingDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete items
/*                for (int i = 0; i < downloadables.size(); i++) {
                    DownloadDBUtils.deleteDownloadByURL(context,downloadables.get(i).getFileURL());
                }*/

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.download_detail_are_you_sure))
                        .setPositiveButton(getString(R.string.download_details_dialog_delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //  Your code when user clicked on OK

                                Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_DELETE_DOWNLOADS_WITHOUT_FILE);
                                broadcastManager.sendBroadcast(pleasePauseIntent);
                                unInitForSelected();
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
                                    Intent pleasePauseIntent = new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_DELETE_DOWNLOADS_WITH_FILE);
                                    broadcastManager.sendBroadcast(pleasePauseIntent);
                                    unInitForSelected();
                                }catch (Exception e){
                                    Log.e("Weber TAG", "onClick Send Broadcast Delete Download In Toolbar Exception : "+e.toString());
                                }
                            }
                        }).create();
                dialog.show();
            }
        });

        buttonShare.setVisibility(View.VISIBLE);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                broadcastManager.sendBroadcast(new Intent(BroadcastUtils.INTENT_FILTER_PLEASE_SHARE_DOWNLOADED_FILES));
                unInitForSelected();
            }
        });
    }

    public void unInitForSelected(){
        buttonSettingDelete = findViewById(R.id.toolbar_setting_button);
        buttonSettingDelete.setImageResource(R.drawable.ic_d_setting);
        buttonSettingDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DownloadSettingsActivity.class);
                startActivity(intent);
            }
        });

        buttonShare = findViewById(R.id.toolbar_share_button);
        buttonShare.setVisibility(View.INVISIBLE);
    }


}

