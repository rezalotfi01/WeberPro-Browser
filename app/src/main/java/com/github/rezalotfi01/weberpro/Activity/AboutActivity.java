package com.github.rezalotfi01.weberpro.Activity;

import android.os.Bundle;
import android.view.View;

import com.github.rezalotfi01.weberpro.BaseClasses.WeberActivity;
import com.github.rezalotfi01.weberpro.BuildConfig;
import com.github.rezalotfi01.weberpro.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends WeberActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element versionElement = new Element();
        versionElement.setTitle(getString(R.string.about_version_title) +" "+ BuildConfig.VERSION_NAME);
        versionElement.setIconDrawable(R.drawable.about_icon_google_play);
        View aboutPage = new AboutPage(this)
                .setDescription(getString(R.string.about_title)+"\n"+getString(R.string.about_description))
                .addEmail("mr.developerx@outlook.com")
                .addGitHub("rezalotfi01")
                .addWebsite("rezalotfi.info")
                .addItem(versionElement)
                .setImage(R.drawable.ic_launcher)
                .create();

        setContentView(aboutPage);
    }
}
