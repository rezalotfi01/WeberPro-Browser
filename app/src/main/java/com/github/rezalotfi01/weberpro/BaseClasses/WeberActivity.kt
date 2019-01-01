package com.github.rezalotfi01.weberpro.BaseClasses

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

@SuppressLint("Registered")
open class WeberActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}


