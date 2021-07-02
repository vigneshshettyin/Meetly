@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.daos.UserDao
import com.vs.meetly.miscellaneous.randAvatar
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.etvEmail
import kotlinx.android.synthetic.main.activity_register.etvPassword

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

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

            registerUser()
        }
        redirectToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser() {

        val userDao = UserDao()

        var user: User

        val name = etvName.text.toString()
        val email = etvEmail.text.toString()
        val password = etvPassword.text.toString()
        val imageUrl = randAvatar()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Snackbar.make(
                registerSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            )
                .show()
        } else if (!email.matches(emailPattern.toRegex())) {
            Snackbar.make(
                registerSnackbar,
                "Enter a valid email id!", Snackbar.LENGTH_LONG
            )
                .show()
        } else if (password.length < 6) {
            Snackbar.make(
                registerSnackbar,
                "Password should be atleast 6 digits long!", Snackbar.LENGTH_LONG
            )
                .show()
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        user = User(
                            firebaseAuth.currentUser!!.uid,
                            name,
                            firebaseAuth.currentUser!!.email.toString(),
                            0,
                            imageUrl
                        )
                        Snackbar.make(
                            registerSnackbar,
                            "Registration successfully complete!", Snackbar.LENGTH_LONG
                        )
                            .show()
                        userDao.addUser(user)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(
                            registerSnackbar,
                            "Error, while creating user!", Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
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

}