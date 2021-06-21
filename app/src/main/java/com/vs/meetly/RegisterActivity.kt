@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.daos.UserDao
import com.vs.meetly.miscellaneous.randAvatar
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.etvEmail
import kotlinx.android.synthetic.main.activity_register.etvPassword

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //For Underline
        val tvlogin: TextView = findViewById(R.id.redirectToLogin)
        tvlogin.underline()

        firebaseAuth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {
            showProgressDialog()
            registerUser()
        }
        redirectToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    TODO: Password Strong Progress Bar To Be Implemented

    private fun registerUser() {

        val userDao = UserDao()

        var user: User

        val name = etvName.text.toString()
        val email = etvEmail.text.toString()
        val password = etvPassword.text.toString()
        val imageUrl = randAvatar()

//        TODO: To Check If The Below Function Can Be Called Using A Coroutine

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    user = User(firebaseAuth.currentUser!!.uid, name, imageUrl)
                    userDao.addUser(user)
                    mProgressDialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error, while creating user!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //Function to put Underline
    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun hideDefaultUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun showProgressDialog() {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

//        mProgressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }
}