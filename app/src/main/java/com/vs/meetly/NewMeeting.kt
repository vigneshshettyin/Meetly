package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.vs.meetly.daos.MeetingDao
import com.vs.meetly.modals.Meeting
import kotlinx.android.synthetic.main.activity_new_meeting.*
import kotlinx.android.synthetic.main.activity_splash.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//<!--    This is only for Mad Lab Assignment -->
//
//<!--    Don't Change anything for now-->


class NewMeeting : AppCompatActivity() {

    private lateinit var date : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_meeting)

        testSelectDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                Toast.makeText(this, "+ ${datePicker.headerText}", Toast.LENGTH_SHORT).show()
                testSelectDate.text = datePicker.headerText
                date = datePicker.headerText
            }
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "-", Toast.LENGTH_SHORT).show()
            }
            datePicker.addOnCancelListener {
                Toast.makeText(this, "<- Back", Toast.LENGTH_SHORT).show()
            }
        }

        testSubmit.setOnClickListener {
            var text = etvTestText.text.toString().trim()
//            Toast.makeText(this, "${text} & ${date}", Toast.LENGTH_SHORT).show()
            var newMeeting = Meeting(date, text)
            Toast.makeText(this, "New Meeting Added!", Toast.LENGTH_SHORT).show()
            GlobalScope.launch {
                val meetingDao = MeetingDao()
                meetingDao.addMeeting(newMeeting)
            }
            finish()
        }



    }
}