package com.vs.meetly

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.daos.MeetingDao
import com.vs.meetly.miscellaneous.ColorPicker
import com.vs.meetly.miscellaneous.LinkPicker
import com.vs.meetly.modals.Meeting
import kotlinx.android.synthetic.main.activity_new_meeting.*
import kotlinx.android.synthetic.main.activity_new_meeting.topAppBar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class NewMeeting : AppCompatActivity() {

    private lateinit var date: String

    private lateinit var time: String

    var timeFlag: Boolean = false

    var dateFlag: Boolean = false

    private var currentSelectedDate: Long? = null

    private lateinit var auth: FirebaseAuth

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meeting)


        auth = FirebaseAuth.getInstance()

        topAppBar.setNavigationOnClickListener {
            finish()
        }

        setTimeToNewMeeting()

        val setdate = Calendar.getInstance().time

        nm_date.text = setDateToNewMeeting(setdate)

        nm_for_date.setOnClickListener {
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

        nm_for_time.setOnClickListener {
            var picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build()

            picker.show(supportFragmentManager, "Meetly")

            picker.addOnPositiveButtonClickListener {
                timeFlag = true
                if (picker.hour > 12) {
                    nm_time.text =
                        String.format("%02d", picker.hour - 12) + ":" + String.format(
                            "%02d", picker.minute
                        ) + " PM"
                } else {
                    nm_time.text = String.format("%02d", picker.hour) + ":" + String.format(
                        "%02d", picker.minute
                    ) + " AM"

                }
                time = nm_time.text.toString()
            }

        }

        testSubmit.setOnClickListener {

            val userId: ArrayList<String> = ArrayList()

            userId.add(auth.currentUser!!.uid)

            val title = etvTestText.text.toString().trim()

            val meetingLink = meetingLink.text.toString().trim()

            val finalMeetingLink = LinkPicker.getLink() + "-r-" + meetingLink.lowercase()


            val detail = detail.text.toString().trim()

            if (title.isEmpty() || detail.isEmpty() || meetingLink.isEmpty() || !dateFlag || !timeFlag) {
                Snackbar.make(
                    newMeetingSnackbar,
                    "Enter all the fields!", Snackbar.LENGTH_LONG
                )
                    .show()
            } else {
                val newMeeting = Meeting(
                    date,
                    title,
                    detail,
                    finalMeetingLink,
                    time,
                    userId,
                    ColorPicker.getColor()
                )
                Snackbar.make(
                    newMeetingSnackbar,
                    "Meeting added successfully!", Snackbar.LENGTH_LONG
                )
                    .show()
                GlobalScope.launch {
                    val meetingDao = MeetingDao()
                    meetingDao.addMeeting(newMeeting)
                }
                finish()
            }
        }
    }

    fun setDateToNewMeeting(setDateNewMeeting: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val formatedDate = sdf.format(setDateNewMeeting)
        return formatedDate
    }

    fun setTimeToNewMeeting() {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        if (hour > 12) {
            nm_time.text =
                String.format("%02d", hour - 12) + ":" + String.format(
                    "%02d", minute
                ) + " PM"
        } else {
            nm_time.text = String.format("%02d", hour) + ":" + String.format(
                "%02d", minute
            ) + " AM"

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
        date = dateAsFormattedText
        nm_date.text = date
        dateFlag = true
    }
}