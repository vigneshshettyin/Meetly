package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_pass.*

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

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(this) {
            if(it.isComplete){
                Toast.makeText(this, "Rest Email Generated!", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Something went wrong, ${it.toString()}!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}