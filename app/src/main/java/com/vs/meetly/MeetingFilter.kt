package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.MeetingAdapter
import com.vs.meetly.modals.Meeting
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_meeting_filter.*

class MeetingFilter : AppCompatActivity() {

    lateinit var adapter: MeetingAdapter

    private var meetingList = mutableListOf<Meeting>()

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_filter)
        val DATE : String = intent.getStringExtra("DATE").toString()
        setUpFireStore(DATE)
        setUpRecyclerView()

        topAppBar.setNavigationOnClickListener {
            finish()
        }


    }

    private fun setUpFireStore(DATE : String) {
        firestore = FirebaseFirestore.getInstance()
        val collectionReference = firestore.collection("meetings").whereEqualTo("date",DATE)
        collectionReference.addSnapshotListener { value, error ->
            if(value == null || error != null){
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA", value.toObjects(Meeting::class.java).toString())
            meetingList.clear()
            meetingList.addAll(value.toObjects(Meeting::class.java))
            adapter.notifyDataSetChanged()
        }
    }

    private fun setUpRecyclerView() {
        adapter = MeetingAdapter(this, meetingList)
        meetingFilterRecyclerView.layoutManager = LinearLayoutManager(this)
        meetingFilterRecyclerView.adapter = adapter
    }

}