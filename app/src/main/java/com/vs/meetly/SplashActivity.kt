package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.vs.meetly.internetcheck.isNetworkAvailable

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        hideDefaultUI()

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

    private fun hideDefaultUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}