package com.vs.meetly

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class DevsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devs)
    }

    fun iVclick(view: View) {
        var url=""
        when(view.id){
            R.id.viVgithub->{ url="https://github.com/vigneshshettyin" }
            R.id.viVgmail->{url="mailto:vigneshshettyalike@gmail.com" }
            R.id.viVlinkedin->{url="https://www.linkedin.com/in/vigneshshettyin/"}
            R.id.siVgithub->{url="https://github.com/Xfinity-bot" }
            R.id.siVgmail->{url="mailto:sriganesh7334@gmail.com" }
            R.id.siVlinkedin->{url="https://www.linkedin.com/in/sriganesh-rao-1b6a921a5/"}


        }
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}