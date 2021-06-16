package com.vs.meetly
import android.os.Build
import com.vs.meetly.internetcheck.isNetworkAvailable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi


class NoInternet : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)


                            if(isNetworkAvailable(this))
                                Toast.makeText(applicationContext,"Working ",Toast.LENGTH_SHORT).show()

    }
}