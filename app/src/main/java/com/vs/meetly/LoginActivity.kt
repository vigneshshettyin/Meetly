@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.etvEmail
import kotlinx.android.synthetic.main.activity_login.etvPassword

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        val tvforgotpass: TextView = findViewById(R.id.etvForgotPassword)
        val tvreg: TextView = findViewById(R.id.redirectToRegister)
        tvforgotpass.underline()
        tvreg.underline()

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Toast.makeText(this, "logged in!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        etvLogin.setOnClickListener {

            login()
        }

        etvForgotPassword.setOnClickListener {
            forgotPassword()
        }
        redirectToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun forgotPassword() {
        val intent = Intent(this, ForgotPassActivity::class.java)
        startActivity(intent)
    }

    private fun login() {
        val email = etvEmail.text.toString().trim()
        val password = etvPassword.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        if (email.isEmpty() || password.isEmpty()){
            Snackbar.make(
                loginSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            )
                .show()
        }
        else if (!email.matches(emailPattern.toRegex())) {
            Snackbar.make(
                loginSnackbar,
                "Enter a valid email id!", Snackbar.LENGTH_LONG
            )
                .show()
        } else if (password.length < 6) {
            Snackbar.make(
                loginSnackbar,
                "Password is too short!", Snackbar.LENGTH_LONG
            )
                .show()
        }
        else{
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Snackbar.make(
                            loginSnackbar,
                            "Incorrect login credentials or account not found!", Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
        }
    }

    fun validate(): Boolean {
        return true;
    }


    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}