package com.vs.meetly.daos

import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.modals.Meeting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MeetingDao {
    private val db = FirebaseFirestore.getInstance()
    private val meetingsCollection = db.collection("meetings")

    fun addMeeting(meeting: Meeting?){
        meeting?.let {
            GlobalScope.launch(Dispatchers.IO) {
                meetingsCollection.document().set(meeting)
            }
        }
    }
}