package com.vs.meetly

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.android.synthetic.main.activity_meeting_filter.*


class DevsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devs)

        topAppBar.setNavigationOnClickListener {
            finish()
        }


    }

    fun iVclick(view: View) {
        var url=""
        when(view.id){
            R.id.viVgithub->{ url="https://github.com/vigneshshettyin" }
            R.id.viVgmail->{url="mailto:vigneshshetty.in@gmail.com" }
            R.id.viVlinkedin->{url="https://www.linkedin.com/in/vigneshshettyin/"}
            R.id.siVgithub->{url="https://github.com/Xfinity-bot" }
            R.id.siVgmail->{url="mailto:sriganesh7334@gmail.com" }
            R.id.siVlinkedin->{url="https://www.linkedin.com/in/sriganesh-rao-1b6a921a5/"}
        }
        val builder = CustomTabsIntent.Builder()
        val CustomTabsIntent = builder.build()
        CustomTabsIntent.launchUrl(this, Uri.parse(url))
    }
}