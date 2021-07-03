package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_pass.*
import kotlinx.android.synthetic.main.activity_forgot_pass.etvEmail

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        etvForgotPassword.setOnClickListener {
            forgotPassword()
        }
    }

    private fun forgotPassword() {
        firebaseAuth = FirebaseAuth.getInstance()

        val email = etvEmail.text.toString()

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z0-9.]+"

        if (email.isEmpty()) {
            Snackbar.make(
                ForgotPasswordSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            )
                .show()
        } else if (!email.matches(emailPattern.toRegex())) {
            Snackbar.make(
                ForgotPasswordSnackbar,
                "Enter a valid email id!", Snackbar.LENGTH_LONG
            )
                .show()
        }
        else{
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
                if (it.isComplete) {
                    Toast.makeText(this, "Rest email generated successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Something went wrong, ${it.toString()}!", Toast.LENGTH_SHORT)
                        .show()
                }
                finish()
            }
        }
    }
}