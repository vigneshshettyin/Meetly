@file:Suppress("DEPRECATION")

package com.vs.meetly


import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN=100
    private lateinit var auth: FirebaseAuth
    private lateinit var user:User
    private lateinit var googlesigninclient:GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //google sign-in
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googlesigninclient = GoogleSignIn.getClient(this,gso)

        val tvforgotpass: TextView = findViewById(R.id.etvForgotPassword)
        val tvreg: TextView = findViewById(R.id.redirectToRegister)
        tvforgotpass.underline()
        tvreg.underline()

        auth = FirebaseAuth.getInstance()

        googleLogin.setOnClickListener{
            signIn()
        }
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

        if (email.isEmpty()) {
//            Snackbar.make(
//                loginSnackbar,
//                "Enter all the fields!", Snackbar.LENGTH_LONG
//            ).show()
            textEmail.error = "Enter the email!"
            loginPreloader.visibility = View.GONE
        } else if(password.isEmpty()){
            textPassword.error = "Enter the password!"
            loginPreloader.visibility = View.GONE
        }
        else if (!email.matches(emailPattern.toRegex())) {
//            Snackbar.make(
//                loginSnackbar,
//                "Enter a valid email id!", Snackbar.LENGTH_LONG
//            )
//                .show()
            textEmail.error = "Enter a valid email id!"
            loginPreloader.visibility = View.GONE
        } else if (password.length < 6) {
//            Snackbar.make(
//                loginSnackbar,
//                "Password is too short!", Snackbar.LENGTH_LONG
//            )
//                .show()
            etvPassword.error = "Password is too short!"
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

    private fun hideKeyboard() { // Hides the keyboard
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun signIn() {
        loginPreloader.visibility=View.VISIBLE
        googlesigninclient.signOut()
        val signInIntent = googlesigninclient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                }catch (e:ApiException){
                    Log.d("ApiException","Google Sign in failed")
                }
            }else{
                loginPreloader.visibility=View.GONE
                Log.d("Error",exception.toString())
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken:String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                loginPreloader.visibility=View.GONE
                val userDao = UserDao()
                if (task.isSuccessful) {
                    if(task.result.additionalUserInfo!!.isNewUser){
                        user = User(
                            auth.currentUser!!.uid,
                            auth.currentUser!!.displayName,
                            auth.currentUser!!.email.toString(),
                            0,
                            auth.currentUser!!.photoUrl.toString()
                        )
                        userDao.addUser(user)
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }else{
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                } else {
                    loginPreloader.visibility=View.GONE
                    Log.d("CredentialFailure", "Sign in with credential failure"+ task.exception)
                }
            }
    }
}