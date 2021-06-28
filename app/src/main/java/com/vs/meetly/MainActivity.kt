package com.vs.meetly

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
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
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), IMeetingRVAdapter {

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth

    private lateinit var user: User

    lateinit var adapter: MeetingAdapter

    private var meetingList = mutableListOf<Meeting>()

    private var tempMeetingList = mutableListOf<Meeting>()

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

        loadUserData()

        displayCalender.setOnClickListener {

            val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

            val datePicker =
                MaterialDatePicker.Builder.datePicker().setSelection(selectedDateInMillis).build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener { dateInMillis ->
                onDateSelected(dateInMillis)
            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun loadUserData() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUserId = auth.currentUser!!.uid
            val userDao = UserDao()
            user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                header_title.text = user.displayName
                loadImage(user.imageUrl)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            loadUserData()
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == MEETING_FILTER_REQUEST_CODE
        ) {
            Toast.makeText(this, "<- pressed!!", Toast.LENGTH_SHORT).show()
            setUpFireStore()
            setUpRecyclerView()
            adapter.notifyDataSetChanged()
        }
        else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun setUpFireStore() {
        val collectionReference =
            firestore.collection("meetings").whereArrayContains("userId", auth.currentUser!!.uid)
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            Log.d("DATA", value.toObjects(Meeting::class.java).toString())
            meetingList.clear()
            meetingList.addAll(value.toObjects(Meeting::class.java))
            if (meetingList.isEmpty()) {
                Log.d("DATA-LIST_EMPTY", "List is empty")
                mainNoData.visibility = View.VISIBLE
            } else {
                mainNoData.visibility = View.GONE
                Log.d("DATA-LIST_EMPTY", meetingList.toString())
                adapter.notifyDataSetChanged()
            }
            tempMeetingList.clear()
            tempMeetingList.addAll(meetingList)
        }
    }

    private fun setUpRecyclerView() {
        adapter = MeetingAdapter(this, tempMeetingList, this)
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
                R.id.profile -> {
                    val intent = Intent(this, UserProfile::class.java)
                    startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
                    true
                }

                R.id.newMeeting -> {
                    val intent = Intent(this, NewMeeting::class.java)
                    startActivity(intent)
                    true
                }
                R.id.devSupport -> {
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.meetDevs -> {
                    val intent = Intent(this, DevsActivity::class.java)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.right_menu_search_bar, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as androidx.appcompat.widget.SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempMeetingList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    meetingList.forEach {
                        if (it.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempMeetingList.add(it)
                            mainNoData.visibility = View.GONE
                        }
                        else{
                            mainNoData.visibility = View.VISIBLE
                        }
                    }
                    adapter.notifyDataSetChanged()
                }
                else if(meetingList.isEmpty()){
                    mainNoData.visibility = View.VISIBLE
                }
                else {
                    mainNoData.visibility = View.GONE
                    tempMeetingList.clear()
                    tempMeetingList.addAll(meetingList)
                    adapter.notifyDataSetChanged()
                }
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
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
                .whereEqualTo("title", meeting.title)
                .whereEqualTo("meeting_link", meeting.meeting_link)
                .whereEqualTo("date", meeting.date)
                .whereEqualTo("time", meeting.time)
                .whereEqualTo("userId", meeting.userId)
                .get()
                .await()
            if (meetingQuery.documents.isNotEmpty()) {
                for (document in meetingQuery) {
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
                                        withContext(Dispatchers.Main) {
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
                    Toast.makeText(
                        this@MainActivity,
                        "No meetings matched the query.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getIntoActivity(meeting: Meeting) {
        Toast.makeText(this, "${meeting.title}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MeetingViewDetail::class.java)
        intent.putExtra("meeting_data", meeting)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDateSelected(dateTimeStampInMillis: Long) {
        currentSelectedDate = dateTimeStampInMillis
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
                currentSelectedDate!!
            ), ZoneId.systemDefault()
        )
        val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val intent = Intent(this, MeetingFilter::class.java)
        intent.putExtra("DATE", dateAsFormattedText)
        startActivityForResult(intent, MEETING_FILTER_REQUEST_CODE)
    }

    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11

        const val MEETING_FILTER_REQUEST_CODE : Int = 22
    }
}