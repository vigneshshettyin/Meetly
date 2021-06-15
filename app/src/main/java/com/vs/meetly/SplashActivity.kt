package com.vs.meetly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        hideDefaultUI()

        Handler().postDelayed({
            val intent  = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000 )

    }
    private fun hideDefaultUI(){
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}