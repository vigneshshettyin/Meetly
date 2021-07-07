package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class NoInternet : AppCompatActivity() {
    lateinit var ctx:Context
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
         ctx = applicationContext
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkcon(view: View) {
            val intentl = Intent(this, SplashActivity::class.java)
             startActivity(intentl)
    }
}