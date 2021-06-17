package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.vs.meetly.internetcheck.isNetworkAvailable


class NoInternet : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
         var ctx:Context=applicationContext
        val mainHandler = Handler(Looper.getMainLooper())
        val intentl  = Intent(this, LoginActivity::class.java)

        mainHandler.post(object : Runnable {
            override fun run() {
                if(isNetworkAvailable(ctx)){
                    startActivity(intentl)
                }
                mainHandler.postDelayed(this, 10000)
            }
        })













    }


}