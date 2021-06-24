package com.vs.meetly

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialDialogs
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.adapters.UsersListAdapter
import kotlinx.android.synthetic.main.activity_meeting_view_detail.*
import kotlinx.android.synthetic.main.dialog_new_user_meeting.*

class MeetingViewDetail : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var meetingName : String

    private lateinit var usersList : MutableList<String>

    lateinit var adapter: UsersListAdapter

    private lateinit var mProgressDialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meeting_view_detail)

        topAppBar.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        auth = FirebaseAuth.getInstance()

        meetingName = intent.getStringExtra("meetingName").toString()

        usersList = intent.getStringArrayListExtra("usersList") as ArrayList<String>

        meeting_info_name.text = meetingName

        topAppBar.title = meetingName

        adapter = UsersListAdapter(this, usersList)
        meeting_info_recycle_view.layoutManager = LinearLayoutManager(this)
        meeting_info_recycle_view.adapter = adapter

        add_new_user.setOnClickListener {
           showEditTextDialog()
        }

    }

    private fun showEditTextDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater : LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_new_user_meeting, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.et_editText)
        with(builder){
            setTitle("Enter user email!")
            setPositiveButton("ADD USER"){dialog, which ->
                Toast.makeText(this@MeetingViewDetail, editText.text.toString(), Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("CANCEL"){dialog, which ->
                Log.d("MAIN-DIALOG", "Canceled")
            }
            setView(dialogLayout)
            show()
        }
    }
}