package com.vs.meetly

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.IVdeleteUser
import com.vs.meetly.adapters.UsersListAdapter
import com.vs.meetly.daos.MeetingDao
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.Meeting
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*
import kotlinx.android.synthetic.main.activity_meeting_view_detail.topAppBar
import kotlinx.android.synthetic.main.activity_user_profile.*
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

class MeetingViewDetail : AppCompatActivity(), IVdeleteUser {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

    private lateinit var auth: FirebaseAuth

    private lateinit var localMeeting: Meeting

    lateinit var adapter: UsersListAdapter

    private lateinit var currentMeetingId: String

    private var tempUsersList = mutableListOf<String>()

    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_view_detail)

        currentMeetingId = intent.getStringExtra("meeting_document_id").toString()

        loadCurrentMeetingData(currentMeetingId)

        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()
        val jnmeet: TextView = findViewById(R.id.joinMeet)
        jnmeet.underline()

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        add_new_user.setOnClickListener {
            showEditTextDialog()
        }

        deleteCustomMeeting.setOnClickListener {
            deleteCustomMeeting()
        }

    }


    fun topBarSetup() {
        meeting_info_name.text = localMeeting.title + localMeeting.meeting_link
        topAppBar.title = localMeeting.title
        md_date.text = localMeeting.date
        md_clock.text = localMeeting.time
        md_desc.text = localMeeting.content
        md_attende_count.text = localMeeting.userId.size.toString()
    }


    fun recycleViewSetup() {
        adapter = UsersListAdapter(this, localMeeting.userId, this)
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        meeting_info_recycle_view.layoutManager = mLayoutManager
        meeting_info_recycle_view.itemAnimator = DefaultItemAnimator()
        meeting_info_recycle_view.adapter = adapter
    }


    fun jitsiServerSetup() {

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
            .setSubject(localMeeting.title)
            .setUserInfo(userInfo)
            .setAudioMuted(true)
            .setVideoMuted(true)
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
            setPositiveButton("OK!") { dialog, which ->
//                Toast.makeText(this@MeetingViewDetail, editText.text.toString(), Toast.LENGTH_SHORT)
//                    .show()
                addUserToCurrentMeeting(editText.text.toString(), true)
            }
            setNegativeButton("CANCEL") { dialog, which ->
                Log.d("MAIN-DIALOG", "Canceled")
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun addUserToCurrentMeeting(email: String, flag: Boolean) {

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
                userList.clear()
                userList.addAll(value.toObjects(User::class.java))
                if (userList.isNullOrEmpty()) {
                    Snackbar.make(
                        activity_view_detail_cr,
                        "User doesn't have a account at Meetly!", Snackbar.LENGTH_LONG
                    )
                        .setBackgroundTint(resources.getColor(R.color.snackbar_success))
                        .setTextColor(resources.getColor(R.color.white))
                        .show()

                } else {
                    //Update Recycleview on update operation
                    if (flag) {
                        if (localMeeting.userId.contains(userList[0].uid)) {
                            Snackbar.make(
                                activity_view_detail_cr,
                                "User already exists!!", Snackbar.LENGTH_LONG
                            )
                                .show()
                        } else {
                            tempUsersList.clear()
                            tempUsersList.addAll(localMeeting.userId)
                            tempUsersList.add(userList[0].uid)
                            Snackbar.make(
                                activity_view_detail_cr,
                                "User added successfully!!", Snackbar.LENGTH_LONG
                            )
                                .show()
                            updateMyMeetingData(tempUsersList as ArrayList<String>)
                        }
                    } else if (!flag) {
                        if (userList[0].uid == auth.currentUser!!.uid.toString()) {
                            Snackbar.make(
                                activity_view_detail_cr,
                                "You can't delete yourself!!", Snackbar.LENGTH_LONG
                            )
                                .show()
                        } else {
                            tempUsersList.clear()
                            tempUsersList.addAll(localMeeting.userId)
                            tempUsersList.remove(userList[0].uid)
                            MaterialAlertDialogBuilder(
                                this@MeetingViewDetail,
                                R.style.Base_ThemeOverlay_MaterialComponents_MaterialAlertDialog
                            )
                                .setMessage(resources.getString(R.string.confirm_logout))
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                    // Respond to negative button press
                                }
                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                    Snackbar.make(
                                        activity_view_detail_cr,
                                        "User deleted successfully!!", Snackbar.LENGTH_LONG
                                    )
                                        .setBackgroundTint(resources.getColor(R.color.snackbar_success))
                                        .setTextColor(resources.getColor(R.color.white))
                                        .show()
                                    updateMyMeetingData(tempUsersList as ArrayList<String>)
                                }
                                .show()
                        }
                    }
                }
            }
        }
    }

    fun deleteCustomMeeting() {
        CoroutineScope(Dispatchers.IO).launch {
            val meetingColRef = firestore.collection("meetings")
            withContext(Dispatchers.Main) {
                MaterialAlertDialogBuilder(
                    this@MeetingViewDetail,
                    R.style.Base_ThemeOverlay_MaterialComponents_MaterialAlertDialog
                )
                    .setMessage(resources.getString(R.string.confirm_logout))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                        GlobalScope.launch {
                            meetingColRef.document(currentMeetingId).delete().await()
                            withContext(Dispatchers.Main) {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        }
                    }
                    .show()
            }
        }
    }

    fun updateMyMeetingData(tempUserArrayList: ArrayList<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            val newMeeting: Meeting = Meeting(
                localMeeting.date,
                localMeeting.title,
                localMeeting.content,
                localMeeting.meeting_link,
                localMeeting.time,
                tempUserArrayList,
                localMeeting.color
            )
            val meetingColRef = firestore.collection("meetings")
            meetingColRef.document(currentMeetingId).set(newMeeting)
            loadCurrentMeetingData(currentMeetingId)
            withContext(Dispatchers.Main) {
                loadCurrentMeetingData(currentMeetingId)
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    fun onButtonClick(v: View?) {
        if(!auth.currentUser!!.isEmailVerified){
            Snackbar.make(activity_view_detail_cr, "Please verify your email id!", Snackbar.LENGTH_LONG)
                .setAction(R.string.action_profile) {
                    startActivity(Intent(this, UserProfile::class.java))
                    finish()
                }
                .show()
        }
            val text = localMeeting.meeting_link
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
                BroadcastEvent.Type.CONFERENCE_JOINED -> Timber.i(
                    "Conference Joined with url%s",
                    event.getData().get("url")
                )
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i(
                    "Participant joined%s",
                    event.getData().get("name")
                )
            }
        }
    }

    // Example for sending actions to JitsiMeetSDK
    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(org.webrtc.ContextUtils.getApplicationContext())
            .sendBroadcast(hangupBroadcastIntent)
        finish()
    }

    override fun onItemClicked(email: String) {
        addUserToCurrentMeeting(email, false)
    }

    private fun setHostDetails(hostUIDPass : String){
        GlobalScope.launch {
            // Get Meeting Data
            val hostUID = localMeeting.userId[0]
            // Get particular user data
            val userdao = UserDao()
            val hostUserDetails = userdao.getUserById(hostUID).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                host_name.text = hostUserDetails.displayName
            }
        }
    }

    private fun loadCurrentMeetingData(currentMeetingId: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val meetingDao = MeetingDao()
            localMeeting =
                meetingDao.getMeetingById(currentMeetingId).await().toObject(Meeting::class.java)!!
            withContext(Dispatchers.Main) {
                topBarSetup()
                setHostDetails(currentMeetingId)
                recycleViewSetup()
                jitsiServerSetup()
            }
        }
    }
    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
