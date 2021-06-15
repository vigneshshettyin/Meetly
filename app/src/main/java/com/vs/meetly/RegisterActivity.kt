package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()

        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser(){
        val email = etvEmail.text.toString()
        val password = etvPassword.text.toString()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if(it.isSuccessful){
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Error, while creating user!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}