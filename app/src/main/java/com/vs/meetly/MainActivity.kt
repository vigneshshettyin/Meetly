package com.vs.meetly

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vs.meetly.adapters.IMeetingRVAdapter
import com.vs.meetly.adapters.MeetingAdapter
import com.vs.meetly.daos.MeetingDao
import com.vs.meetly.daos.UserDao
import com.vs.meetly.miscellaneous.ColorPicker
import com.vs.meetly.miscellaneous.LinkPicker
import com.vs.meetly.modals.Meeting
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.dialog_bottom_navigation.*
import kotlinx.android.synthetic.main.header_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), IMeetingRVAdapter {

    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    private lateinit var auth: FirebaseAuth

    private var dateTimeSelectorFlag : Boolean = false

    private var dateSelected : String = ""

    private var timeSelected : String = ""

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

        mainPreloader.visibility = View.VISIBLE
        setUpViews()
        setUpFireStore()
        setUpRecyclerView()
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
//                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnCancelListener {
//                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (mainDrawer.isDrawerOpen(GravityCompat.START)) {
            mainDrawer.closeDrawer(GravityCompat.START)
            Snackbar.make(
                mainActivitySnackbar,
                "Press back once more to exit!", Snackbar.LENGTH_LONG
            )
                .show()

        } else {
            MaterialAlertDialogBuilder(
                this@MainActivity,
                R.style.Base_ThemeOverlay_MaterialComponents_MaterialAlertDialog
            )
                .setMessage(resources.getString(R.string.exitMessage))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                    super.onBackPressed()
                }
                .show()
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
//            Toast.makeText(this, "<- pressed!!", Toast.LENGTH_SHORT).show()
            setUpFireStore()
            setUpRecyclerView()
            adapter.notifyDataSetChanged()
        } else if (resultCode == Activity.RESULT_OK
            && requestCode == MEETING_VIEW_DETAIL_CODE
        ) {
//            Toast.makeText(this, "Meeting View Detail!", Toast.LENGTH_SHORT).show()
            setUpFireStore()
            setUpRecyclerView()
            adapter.notifyDataSetChanged()
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    private fun setUpFireStore() {
        val collectionReference =
            firestore.collection("meetings").whereArrayContains("userId", auth.currentUser!!.uid)
        collectionReference.addSnapshotListener { value, error ->
            if (value == null || error != null) {
//                Toast.makeText(this, "Error fetching data", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            meetingList.clear()
            meetingList.addAll(value.toObjects(Meeting::class.java))
            if (meetingList.isEmpty()) {
                tempMeetingList.clear()
                adapter.notifyDataSetChanged()
                mainNoData.visibility = View.VISIBLE
            } else {
                mainNoData.visibility = View.GONE
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
        mainPreloader.visibility = View.GONE
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
                    mainDrawer.closeDrawers()
                    val intent = Intent(this, UserProfile::class.java)
                    startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
                    true
                }

                R.id.todayMeeting -> {
                    mainDrawer.closeDrawers()
                    val date = Calendar.getInstance().time
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val formatedDate = sdf.format(date)
//                    Toast.makeText(this, formatedDate.toString(), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MeetingFilter::class.java)
                    intent.putExtra("DATE", formatedDate)
                    startActivityForResult(intent, MEETING_FILTER_REQUEST_CODE)
                    true
                }

                R.id.newMeeting -> {
                    mainDrawer.closeDrawers()
                    val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
                    // on below line we are inflating a layout file which we have created.
                    val view = layoutInflater.inflate(R.layout.dialog_bottom_navigation, null)
                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()
                    // Getting all data
                    val finalMeetingLink = LinkPicker.getLink() + "-r-" + getRandomString(6)
                    val title = view.findViewById<EditText>(R.id.meeting_title_v2) as EditText
                    val content = view.findViewById<EditText>(R.id.meeting_content_v2) as EditText
                    val dateTimeSelect = view.findViewById<EditText>(R.id.date_time_new_meeting_v2) as EditText
                    val submitButtonV2 = view.findViewById<Button>(R.id.buttonSubmitNewMeetingV2) as Button
                    submitButtonV2.setOnClickListener {
                        if(title.text.isEmpty() || content.text.isEmpty() || !dateTimeSelectorFlag ){
                            if(title.text.isEmpty()){
                                title.setError("Title is required!")
                            }
                            else if ( content.text.isEmpty()){
                                content.setError("Content is required!")
                            }
                            else{
                                dateTimeSelect.setError("Date & Time is required!")
                            }
                        }
                        else{
                            val userId: ArrayList<String> = ArrayList()
                            userId.add(auth.currentUser!!.uid)
                            val newMeeting = Meeting(
                                dateSelected,
                                title.text.toString(),
                                content.text.toString(),
                                finalMeetingLink,
                                timeSelected,
                                userId,
                                ColorPicker.getColor()
                            )
                            dialog.dismiss()
                            GlobalScope.launch {
                                val meetingDao = MeetingDao()
                                meetingDao.addMeeting(newMeeting)
                            }

                            Snackbar.make(
                                mainActivitySnackbar,
                                "Meeting added successfully!", Snackbar.LENGTH_LONG
                            )
                                .show()

                        }
                    }
                    true
                }
                R.id.raiseRequest -> {
                    mainDrawer.closeDrawers()
                    val url = "https://meetly.tawk.help"
                    val builder = CustomTabsIntent.Builder()
                    val CustomTabsIntent = builder.build()
                    CustomTabsIntent.launchUrl(this, Uri.parse(url))
                    true
                }
                R.id.devSupport -> {
                    mainDrawer.closeDrawers()
                    val intent = Intent(this, SupportActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.meetDevs -> {
                    mainDrawer.closeDrawers()
                    val intent = Intent(this, DevsActivity::class.java)
                    startActivity(intent)
                    true

                }
                R.id.usedLib -> {
                    mainDrawer.closeDrawers()
                    val intent = Intent(this,LibraryActivity::class.java)
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
                            Toast.makeText(this, "Logout Successfull!", Toast.LENGTH_SHORT).show()
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

        val edittext=searchView.findViewById<EditText>(R.id.search_src_text) as EditText

        edittext.setTextColor(getResources().getColor(R.color.lblue_200))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempMeetingList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    meetingList.forEach {
                        if (it.title.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            tempMeetingList.add(it)
                            mainNoData.visibility = View.GONE
                        } else {
                            mainNoData.visibility = View.VISIBLE
                        }
                    }
                    adapter.notifyDataSetChanged()
                } else if (meetingList.isEmpty()) {
                    mainNoData.visibility = View.VISIBLE
                } else {
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

    override fun onItemClicked(meeting: Meeting) {
        val meetingColRef = firestore.collection("meetings")
        CoroutineScope(Dispatchers.IO).launch {
            val meetingQuery = meetingColRef
                .whereEqualTo("content", meeting.content)
                .whereEqualTo("title", meeting.title)
                .whereEqualTo("meeting_link", meeting.meeting_link)
                .whereEqualTo("date", meeting.date)
                .whereEqualTo("time", meeting.time)
                .whereEqualTo("userId", meeting.userId)
                .whereEqualTo("color", meeting.color)
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
                            Toast.makeText(this@MainActivity, "Error!", Toast.LENGTH_LONG).show()
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
        CoroutineScope(Dispatchers.IO).launch {
            val meetingColRef = firestore.collection("meetings")
            val meetingQuery = meetingColRef
                .whereEqualTo("content", meeting.content)
                .whereEqualTo("title", meeting.title)
                .whereEqualTo("meeting_link", meeting.meeting_link)
                .whereEqualTo("date", meeting.date)
                .whereEqualTo("time", meeting.time)
                .whereEqualTo("userId", meeting.userId)
                .whereEqualTo("color", meeting.color)
                .get()
                .await()
            if (meetingQuery.documents.isNotEmpty()) {
                for (document in meetingQuery) {
                    withContext(Dispatchers.Main) {
//                        Toast.makeText(this@MainActivity, document.id, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, MeetingViewDetail::class.java)
                        intent.putExtra("meeting_document_id", document.id)
                        startActivityForResult(intent, MEETING_VIEW_DETAIL_CODE)
                    }
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
            ), ZoneId.systemDefault()
        )
        val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val intent = Intent(this, MeetingFilter::class.java)
        intent.putExtra("DATE", dateAsFormattedText)
        startActivityForResult(intent, MEETING_FILTER_REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setForDateAndTime(dateTimeStampInMillis: Long, view: View){
        currentSelectedDate = dateTimeStampInMillis
        val dateTime: LocalDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(
                currentSelectedDate!!
            ), ZoneId.systemDefault()
        )
        val dateAsFormattedText: String = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()

        picker.show(supportFragmentManager, "Meetly")

        var localTime : String = ""

        picker.addOnPositiveButtonClickListener {
            if (picker.hour > 12) {
                localTime =
                    String.format("%02d", picker.hour - 12) + ":" + String.format(
                        "%02d", picker.minute
                    ) + " PM"
            } else {
                localTime = String.format("%02d", picker.hour) + ":" + String.format(
                    "%02d", picker.minute
                ) + " AM"

            }

            val dateTimeV2 = view.findViewById<EditText>(R.id.date_time_new_meeting_v2) as EditText
            dateTimeV2.setText("${dateAsFormattedText}  ${localTime}")
            // Flag to check if date and time are selected
            dateTimeSelectorFlag = true
            dateSelected = dateAsFormattedText
            timeSelected = localTime
        }
    }

    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11

        const val MEETING_FILTER_REQUEST_CODE: Int = 22

        const val MEETING_VIEW_DETAIL_CODE: Int = 33
    }

    fun closeLayoutDrawer(view: View) {
        mainDrawer.closeDrawers()
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun DateTimeFunction(view: View) {
        dateTimeSelectorFlag = false
        val selectedDateInMillis = currentSelectedDate ?: System.currentTimeMillis()

        val datePicker =
            MaterialDatePicker.Builder.datePicker().setSelection(selectedDateInMillis).build()
        datePicker.show(supportFragmentManager, "DatePicker")
        datePicker.addOnPositiveButtonClickListener { dateInMillis ->
            setForDateAndTime(dateInMillis, view)
        }
        datePicker.addOnNegativeButtonClickListener {
//                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
        }
        datePicker.addOnCancelListener {
//                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
        }
    }
}