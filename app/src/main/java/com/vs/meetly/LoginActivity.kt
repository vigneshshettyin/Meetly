package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hideDefaultUI()

        firebaseAuth = FirebaseAuth.getInstance()

        etvLogin.setOnClickListener {
            login()
        }

    }

    private fun login() {
        val email = etvEmail.text.toString()
        val password =etvPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    Toast.makeText(this, "Login Successfull!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Incorrect login credentials or account not found!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun hideDefaultUI(){
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}