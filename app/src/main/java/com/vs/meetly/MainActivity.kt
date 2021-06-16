package com.vs.meetly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        testProgress.visibility = View.VISIBLE

            GlobalScope.launch(Dispatchers.IO) {
                val currentUserId = auth.currentUser!!.uid
                val userDao = UserDao()
                user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
                withContext(Dispatchers.Main){
                    demoTest.text = user.displayName
                    demoTest2.text  = user.uid
                    loadImage(user.imageUrl)
                }
            }
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(image)
        testProgress.visibility = View.GONE
    }
}