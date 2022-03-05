@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

        auth = FirebaseAuth.getInstance()

        etvLogin.setOnClickListener {
            loginPreloader.visibility = View.VISIBLE
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
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z0-9.]+"
        hideKeyboard()

        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(
                loginSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            ).show()
            loginPreloader.visibility = View.GONE
        } else if (!email.matches(emailPattern.toRegex())) {
            Snackbar.make(
                loginSnackbar,
                "Enter a valid email id!", Snackbar.LENGTH_LONG
            )
                .show()
            loginPreloader.visibility = View.GONE
        } else if (password.length < 6) {
            Snackbar.make(
                loginSnackbar,
                "Password is too short!", Snackbar.LENGTH_LONG
            )
                .show()
            loginPreloader.visibility = View.GONE
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        if (auth.currentUser!!.isEmailVerified) {
                            loginPreloader.visibility = View.GONE
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            loginPreloader.visibility = View.GONE
                            Snackbar.make(
                                loginSnackbar,
                                "Email not verified yet!", Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    } else {
                        loginPreloader.visibility = View.GONE
                        Snackbar.make(
                            loginSnackbar,
                            "Incorrect login credentials or account not found!",
                            Snackbar.LENGTH_LONG
                        )
                            .show()
                    }
                }
        }
    }

    private fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}