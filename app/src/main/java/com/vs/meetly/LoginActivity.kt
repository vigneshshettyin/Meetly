@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mProgressDialog: Dialog


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
            Toast.makeText(this, "Already logged in!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        etvLogin.setOnClickListener {
            showProgressDialog()
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
        //Validation
        if (TextUtils.isEmpty(email)) {
            val context = findViewById<TextView>(R.id.etvEmail) as TextView
            Snackbar.make(context, "Email id is required!", Snackbar.LENGTH_SHORT).show()
//            etvEmail.error = "Email id required!"
            return

        }
        if (TextUtils.isEmpty(password)) {
            etvPassword.error = "Password required!"
            return

        }


        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    mProgressDialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Incorrect login credentials or account not found!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun validate(): Boolean {
        return true;
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

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}