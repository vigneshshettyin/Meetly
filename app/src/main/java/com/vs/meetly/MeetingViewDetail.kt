package com.vs.meetly

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.UsersListAdapter
import com.vs.meetly.modals.Meeting
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*
import kotlinx.android.synthetic.main.dialog_new_user_meeting.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import www.sanju.motiontoast.MotionToast

class MeetingViewDetail : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var meeting: Meeting

    lateinit var adapter: UsersListAdapter

    private var tempUsersList = mutableListOf<String>()

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_view_detail)

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()

        meeting = intent.getParcelableExtra("meeting_data")!!

        meeting_info_name.text = meeting.title

        topAppBar.title = meeting.title

        tempUsersList.clear()

        tempUsersList.addAll(meeting.userId)

        adapter = UsersListAdapter(this, tempUsersList)
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        meeting_info_recycle_view.layoutManager = mLayoutManager
        meeting_info_recycle_view.itemAnimator = DefaultItemAnimator()
        meeting_info_recycle_view.adapter = adapter

        add_new_user.setOnClickListener {
            showEditTextDialog()
        }

    }

    private fun showEditTextDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_new_user_meeting, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        with(builder) {
            setTitle(" ")
            setPositiveButton("ADD USER") { dialog, which ->
//                Toast.makeText(this@MeetingViewDetail, editText.text.toString(), Toast.LENGTH_SHORT)
//                    .show()
                addUserToCurrentMeeting(editText.text.toString())
            }
            setNegativeButton("CANCEL") { dialog, which ->
                Log.d("MAIN-DIALOG", "Canceled")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun addUserToCurrentMeeting(email: String) {

        var userList = mutableListOf<User>()

        GlobalScope.launch {
            val collectionReference = firestore.collection("users").whereEqualTo("email", email)
            collectionReference.addSnapshotListener { value, error ->
                if (value == null || error != null) {
                    Toast.makeText(
                        this@MeetingViewDetail,
                        "Error fetching data",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }
                Log.d("DATA", value.toObjects(User::class.java).toString())
                userList.clear()
                userList.addAll(value.toObjects(User::class.java))
                Log.d("USER_LIST", userList.toString())

                if (userList.isNullOrEmpty()) {
                    alertUserNotPresent()
                    tempUsersList.clear()
                    tempUsersList.addAll(meeting.userId)

                } else {

                    //Update Recycleview on update operation
                    tempUsersList.clear()
                    tempUsersList.addAll(meeting.userId)
                    tempUsersList.add(userList[0].uid)
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val newMeeting: Meeting = Meeting(
                meeting.date,
                meeting.title,
                meeting.content,
                meeting.meeting_link,
                meeting.time,
                tempUsersList as ArrayList<String>
            )

            val meetingColRef = firestore.collection("meetings")
            val meetingQuery = meetingColRef
                .whereEqualTo("content", meeting.content)
                .whereEqualTo("title", meeting.title)
                .whereEqualTo("meeting_link", meeting.meeting_link)
                .whereEqualTo("date", meeting.date)
                .whereEqualTo("time", meeting.time)
                .whereEqualTo("userId", meeting.userId)
                .get()
                .await()
            if (meetingQuery.documents.isNotEmpty()) {
                for (document in meetingQuery) {
                    meetingColRef.document(document.id).set(newMeeting)
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }

            }
        }


    }

    private fun alertUserNotPresent() {
        MotionToast.createToast(
            this,
            "Failed ☹️",
            "User don't have a account at Meetly!",
            MotionToast.TOAST_ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, R.font.helvetica_regular)
        )
    }
}
