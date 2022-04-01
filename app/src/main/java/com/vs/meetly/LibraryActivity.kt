package com.vs.meetly

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_library.*

class LibraryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        retrofitBtn.setOnClickListener {
            openLink("https://github.com/square/retrofit")
        }
        jitsiBtn.setOnClickListener {
            openLink("https://github.com/jitsi/jitsi-meet")
        }
        glideBtn.setOnClickListener {
            openLink("https://github.com/bumptech/glide")
        }
        lottieBtn.setOnClickListener {
            openLink("https://github.com/airbnb/lottie-android")
        }
        libTopAppBar.setNavigationOnClickListener {
            finish()
        }

    }

    private fun openLink(link : String){
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(link)
        startActivity(openURL)
    }
}