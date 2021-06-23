package com.vs.meetly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.adapters.UsersListAdapter
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*

class MeetingViewDetail : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var meetingName : String

    private lateinit var usersList : MutableList<String>

    lateinit var adapter: UsersListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_view_detail)

        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()

        meetingName = intent.getStringExtra("meetingName").toString()

        usersList = intent.getStringArrayListExtra("usersList") as ArrayList<String>

        meeting_info_name.text = meetingName

        topAppBar.title = meetingName

        adapter = UsersListAdapter(this, usersList)
        meeting_info_recycle_view.layoutManager = LinearLayoutManager(this)
        meeting_info_recycle_view.adapter = adapter

    }
}