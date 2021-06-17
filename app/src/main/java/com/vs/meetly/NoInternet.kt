package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vs.meetly.internetcheck.isNetworkAvailable
import kotlin.system.exitProcess


class NoInternet : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
         var ctx:Context=applicationContext
        val mainHandler = Handler(Looper.getMainLooper())
        val intentl  = Intent(this, LoginActivity::class.java)


        mainHandler.post(object : Runnable {
            var x:Boolean=true

            override fun run() {
                if(isNetworkAvailable(ctx)){
                     startActivity(intentl)
                     finish()
                     mainHandler.removeCallbacksAndMessages(null)
                     mainHandler.looper.quitSafely()
                }
                mainHandler.postDelayed(this, 10000)
            }
        })
    }
}