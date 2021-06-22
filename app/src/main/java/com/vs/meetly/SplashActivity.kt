@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.vs.meetly.internetcheck.isNetworkAvailable

class SplashActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        Handler().postDelayed({
            val intentl = Intent(this, LoginActivity::class.java)
            val intentno = Intent(this, NoInternet::class.java)
            if (isNetworkAvailable(this))
                startActivity(intentl)
            else
                startActivity(intentno)
            finish()
        }, 3000)

    }
}