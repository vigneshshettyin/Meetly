@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
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

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()


        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //For Underline
        val tvlogin: TextView = findViewById(R.id.redirectToLogin)
        tvlogin.underline()

        buttonRegister.setOnClickListener {
            registerPreloader.visibility = View.VISIBLE
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

        //Storing the field inputs and trimming them to ensure no extra whitespaces at the ends.
        val name = etvName.text.toString().trim()
        val email = etvEmail.text.toString().trim()
        val password = etvPassword.text.toString().trim()

        //Generating random avatar for the user.
        val imageUrl = randAvatar()

        //Valid email pattern.
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z0-9.]+"
        hideKeyboard()

        /* We check for the following things :
         * a) If email, name and password fields are empty, we show appropriate message.
         * b) If the email entered is not in a valid format, we show appropriate message.
         * c) If password length is less than 6 characters, we show appropriate message.
         * d) If all of the above checks are passed, we go on to register the user. If the
         *    user is registered successfully, we proceed to add the user to the firestore
         *    database, and we also send a verification email to the user. If the registration
         *    service fails somehow, we show appropriate message.
         */
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Snackbar.make(
                registerSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            )
                .show()
            registerPreloader.visibility = View.GONE
        } else if (!email.matches(emailPattern.toRegex())) {
            Snackbar.make(
                registerSnackbar,
                "Enter a valid email id!", Snackbar.LENGTH_LONG
            )
                .show()
            registerPreloader.visibility = View.GONE
        } else if (password.length < 6) {
            Snackbar.make(
                registerSnackbar,
                "Password should be atleast 6 digits long!", Snackbar.LENGTH_LONG
            )
                .show()
            registerPreloader.visibility = View.GONE
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        user = User(
                            auth.currentUser!!.uid,
                            name,
                            auth.currentUser!!.email.toString(),
                            0,
                            imageUrl)
                        Snackbar.make(registerSnackbar,
                            "Registration successfully complete!", Snackbar.LENGTH_LONG)
                            .show()
                        userDao.addUser(user)
                        auth.currentUser!!.sendEmailVerification().addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                registerPreloader.visibility = View.GONE
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Snackbar.make(
                                    registerSnackbar,
                                    "Error, while creating user!", Snackbar.LENGTH_LONG
                                )
                                    .show()
                                registerPreloader.visibility = View.GONE
                            }
                        }
                    } else {
                        Snackbar.make(
                            registerSnackbar,
                            "Error, while creating user!", Snackbar.LENGTH_LONG
                        )
                            .show()
                        registerPreloader.visibility = View.GONE
                    }
                }
        }
    }

    //Function to put Underline
    private fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun hideDefaultUI() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun hideKeyboard() { // Hides the keyboard
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}