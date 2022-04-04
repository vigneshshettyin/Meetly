package com.vs.meetly

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.internetcheck.isNetworkAvailable

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = FirebaseAuth.getInstance()

        checkPermission()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
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
            if (isNetworkAvailable(this)) {
                if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
//                    Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    startActivity(intentl)
                    finish()
                }
            } else {
                startActivity(intentno)
                finish()
            }
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
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.alertDialogPermissionText)
                    .setCancelable(false)
                    .setPositiveButton("Next") { _, _ ->
                        checkPermission()
                    }
                    .show()
            } else {
                MaterialAlertDialogBuilder(this)
                    .setMessage(R.string.alertDialogDeniedPermissionText)
                    .setCancelable(false)
                    .setPositiveButton(R.string.permit) { _, _ ->
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        finish()
                    }
                    .setNeutralButton(R.string.exit) { _, _ -> finishAffinity() }
                    .show()
            }
        }
    }
}