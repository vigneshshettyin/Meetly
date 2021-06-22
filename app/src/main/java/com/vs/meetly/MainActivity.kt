package com.vs.meetly

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.IMeetingRVAdapter
import com.vs.meetly.adapters.MeetingAdapter
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.Meeting
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_new_meeting.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), IMeetingRVAdapter {

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth

    private lateinit var user: User

    lateinit var adapter: MeetingAdapter

    private var meetingList = mutableListOf<Meeting>()

    lateinit var firestore: FirebaseFirestore

    private var currentSelectedDate: Long? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()

//        hideDefaultUI()
        setUpViews()
        setUpFireStore()
        setUpRecyclerView()

        testProgress.visibility = View.VISIBLE

        GlobalScope.launch(Dispatchers.IO) {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                header_title.text = user.displayName
                user_id.text = user.uid
                loadImage(user.imageUrl)
            }
        }

        displayCalender.setOnClickListener {

            val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

            val datePicker = MaterialDatePicker.Builder.datePicker().setSelection(selectedDateInMillis).build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                dateInMillis -> onDateSelected(dateInMillis)
            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun setUpFireStore() {
      val collectionReference = firestore.collection("meetings").whereEqualTo("userId", auth.currentUser!!.uid)
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA", value.toObjects(Meeting::class.java).toString())
            meetingList.clear()
            meetingList.addAll(value.toObjects(Meeting::class.java))
            if(meetingList.isEmpty()){
                Log.d("DATA-LIST_EMPTY", "List is empty")
            }
            else{
                Log.d("DATA-LIST_EMPTY", meetingList.toString())
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun setUpRecyclerView() {
        adapter = MeetingAdapter(this, meetingList, this)
        meetingRecyclerview.layoutManager = LinearLayoutManager(this)
        meetingRecyclerview.adapter = adapter
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).circleCrop().into(header_image)
        testProgress.visibility = View.GONE
    }

    private fun setUpViews() {
        setUpDrawerLayout()
    }

    private fun setUpDrawerLayout() {
        setSupportActionBar(appBar)
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, mainDrawer, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.syncState()
        navigationList.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile ->{
                    val intent = Intent(this, UserProfile::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.newMeeting -> {
                    val intent = Intent(this, NewMeeting::class.java)
                    startActivity(intent)
                    true
                }
                R.id.devSupport ->{
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.logout -> {
                    mainDrawer.closeDrawers()
                    MaterialAlertDialogBuilder(
                        this,
                        R.style.Base_ThemeOverlay_MaterialComponents_MaterialAlertDialog
                    )
                        .setMessage(resources.getString(R.string.confirm_logout))
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton(resources.getString(R.string.logout)) { dialog, which ->
                            // Respond to positive button press
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideDefaultUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    override fun onItemClicked(meeting: Meeting) {
//        Toast.makeText(this, meeting.content, Toast.LENGTH_SHORT).show()
        val meetingColRef = firestore.collection("meetings")
        CoroutineScope(Dispatchers.IO).launch {
            val meetingQuery = meetingColRef
                .whereEqualTo("content", meeting.content)
                .whereEqualTo("date", meeting.date)
                .whereEqualTo("time", meeting.time)
                .whereEqualTo("userId", meeting.userId)
                .get()
                .await()
            if(meetingQuery.documents.isNotEmpty()) {
                for(document in meetingQuery) {
                    try {
                        withContext(Dispatchers.Main) {
                            MaterialAlertDialogBuilder(
                                this@MainActivity,
                                R.style.Base_ThemeOverlay_MaterialComponents_MaterialAlertDialog
                            )
                                .setMessage(resources.getString(R.string.confirm_logout))
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                    // Respond to negative button press
                                }
                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                        GlobalScope.launch {
                                            meetingColRef.document(document.id).delete().await()
                                            withContext(Dispatchers.Main){
                                                adapter.notifyDataSetChanged()
                                            }
                                        }
                                }
                                .show()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "No meetings matched the query.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDateSelected(dateTimeStampInMillis: Long) {
        currentSelectedDate = dateTimeStampInMillis
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
            currentSelectedDate!!
        ), ZoneId.systemDefault())
        val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val intent = Intent(this, MeetingFilter::class.java)
        intent.putExtra("DATE", dateAsFormattedText)
        startActivity(intent)

    }
}