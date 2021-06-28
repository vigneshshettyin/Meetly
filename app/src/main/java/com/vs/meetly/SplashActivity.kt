package com.vs.meetly

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.vs.meetly.internetcheck.isNetworkAvailable

@Suppress("DEPRECATION")
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

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET),
                111
            )
        } else {
            moveToNextIntent()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun moveToNextIntent() {
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            moveToNextIntent()
        }
    }
}