package com.vs.meetly

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_meeting_filter.*


class DevsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devs)

        loadImage()

        topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadImage() {
        val vdp = findViewById<ImageView>(R.id.vdp) as ImageView
        val sdp = findViewById<ImageView>(R.id.sdp) as ImageView

        // TODO: Change to dev images at last!

        val url_vdp = "https://res.cloudinary.com/vigneshshettyin/image/upload/v1624102716/dfgmvgwtlyc08gcfmtly.png"

        val url_sdp = "https://res.cloudinary.com/vigneshshettyin/image/upload/v1624102716/dfgmvgwtlyc08gcfmtly.png"

        Glide.with(vdp).load(url_vdp).circleCrop().into(vdp)
        Glide.with(sdp).load(url_sdp).circleCrop().into(sdp)
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
