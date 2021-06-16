package com.vs.meetly
import android.os.Build
import com.vs.meetly.internetcheck.isNetworkAvailable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.coroutines.coroutineScope

class NoInternet : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)
        val scope = coroutineScope()
        fun repeatFun() {
            return coroutineScope.launch {
                while(isActive) {
                    //do your network request here
                    delay(1000)
                }
            }
        }

//start the loop
        val repeatFun = repeatRequest()

//Cancel the loop
        repeatFun.cancel()

                            if(isNetworkAvailable(this))
                                Toast.makeText(applicationContext,"Working ",Toast.LENGTH_SHORT).show()

    }
}