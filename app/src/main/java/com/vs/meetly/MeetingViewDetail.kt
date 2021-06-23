package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*

class MeetingViewDetail : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var meetingName : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_view_detail)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()

        meetingName = intent.getStringExtra("meetingName").toString()

        meeting_info_name.text = meetingName

        topAppBar.title = meetingName


    }
}