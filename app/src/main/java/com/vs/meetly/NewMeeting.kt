package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.vs.meetly.daos.MeetingDao
import com.vs.meetly.modals.Meeting
import kotlinx.android.synthetic.main.activity_new_meeting.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//<!--    This is only for Mad Lab Assignment -->
//
//<!--    Don't Change anything for now-->


class NewMeeting : AppCompatActivity() {

    private lateinit var date: String

    private lateinit var time: String

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

        testSelectTime.setOnClickListener {
            var picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build()

            picker.show(supportFragmentManager, "mediLite")

            picker.addOnPositiveButtonClickListener {
                if (picker.hour > 12) {
                    testSelectTime.text =
                        String.format("%02d", picker.hour - 12) + ":" + String.format(
                            "%02d", picker.minute
                        ) + " PM"
                } else {
                    testSelectTime.text = String.format("%02d", picker.hour) + ":" + String.format(
                        "%02d", picker.minute
                    ) + " AM"

                }

                time = testSelectTime.text.toString()
            }
        }

        testSubmit.setOnClickListener {
            var text = etvTestText.text.toString().trim()
//            Toast.makeText(this, "${text} & ${date}", Toast.LENGTH_SHORT).show()
            var newMeeting = Meeting(date, text, time)
            Toast.makeText(this, "New Meeting Added!", Toast.LENGTH_SHORT).show()
            GlobalScope.launch {
                val meetingDao = MeetingDao()
                meetingDao.addMeeting(newMeeting)
            }
            finish()
        }


    }
}