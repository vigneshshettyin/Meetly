package com.vs.meetly

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.UsersListAdapter
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.Meeting
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*
import kotlinx.android.synthetic.main.dialog_new_user_meeting.*
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.jitsi.meet.sdk.*
import timber.log.Timber
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class MeetingViewDetail : AppCompatActivity() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

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

        meeting_info_name.text = meeting.title + meeting.meeting_link

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

        // Initialize default options for Jitsi Meet conferences.

        val serverURL: URL
        serverURL = try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL

            URL("https://meet.jit.si")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }

        val userInfo = JitsiMeetUserInfo()

        GlobalScope.launch(Dispatchers.IO) {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            userInfo.avatar = URL(user.imageUrl)
            userInfo.displayName = user.displayName
            userInfo.email = user.email
        }


        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setSubject(meeting.title)
            .setUserInfo(userInfo)
            // When using JaaS, set the obtained JWT here
            //.setToken("MyJWT")
            // Different features flags can be set
            //.setFeatureFlag("toolbox.enabled", false)
            //.setFeatureFlag("filmstrip.enabled", false)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()



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
        SupportClass.alertUserDanger(this, "User don't have a account at Meetly!")
    }
    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }


    fun onButtonClick(v: View?) {
        val text = meeting.meeting_link
        if (text.length > 0) {
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(text)
                // Settings for audio and video
                //.setAudioMuted(true)
                //.setVideoMuted(true)
                .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options)
        }
    }


    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
                ... other events
         */
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    // Example for handling different JitsiMeetSDK events
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.getType()) {
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i("Conference Joined with url%s", event.getData().get("url"))
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i("Participant joined%s", event.getData().get("name"))
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(org.webrtc.ContextUtils.getApplicationContext()).sendBroadcast(hangupBroadcastIntent)
        finish()
    }


}
