@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.User
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.activity_user_profile.topAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

class UserProfile : AppCompatActivity() {

    // A global variable for a user profile image URL
    private var mProfileImageURL: String = ""

    private var currentProfileImage: String = ""


    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        auth = FirebaseAuth.getInstance()

        val verifytbtn: TextView = findViewById(R.id.verifyEmailtBtn)

        verifytbtn.underline()

        getUserInfo(auth.currentUser!!.uid)

        topAppBar.setNavigationOnClickListener {
            finish()
        }
        profileImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }

        }

        buttonProfileUpdate.setOnClickListener {
            updateUserProfileData()
        }
        verifyEmailtBtn.setOnClickListener {
            if (!auth.currentUser!!.isEmailVerified) {
                auth.currentUser!!.sendEmailVerification()
                Snackbar.make(
                    profileSnackbar,
                    "Verification email sent successfully!", Snackbar.LENGTH_LONG
                )
                    .show()
            } else {
                Snackbar.make(
                    profileSnackbar,
                    "Email already verified!", Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun updateUserProfileData() {

        val name = profileName.text.toString()
        val email = profileEmail.text.toString()
        val phone = profilePhone.text.toString()

        if (name.isEmpty() || phone.isEmpty()) {
            Snackbar.make(
                profileSnackbar,
                "Enter all the fields!", Snackbar.LENGTH_LONG
            )
                .show()
        } else {

            var userAvatar = ""
            if (mProfileImageURL.isEmpty()) {
                userAvatar = currentProfileImage
            } else {
                userAvatar = mProfileImageURL
            }
            profilePreloader.visibility = View.GONE
            val user = User(auth.currentUser!!.uid, name, email, phone.toLong(), userAvatar)

            GlobalScope.launch {
                val userdao = UserDao()
                userdao.addUser(user)
            }
            setResult(Activity.RESULT_OK)
            Snackbar.make(
                profileSnackbar,
                "Profile updated successfully!", Snackbar.LENGTH_LONG
            )
                .show()
        }
    }

    private fun getUserInfo(userId: String) {

        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(userId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                loadImage(user.imageUrl)
                currentProfileImage = user.imageUrl
                profileName.setText(user.displayName)
                profileEmail.setText(user.email)
                profilePhone.setText(user.phone.toString())
                if (auth.currentUser!!.isEmailVerified) {
                    verifiedstatus.setVisibility(View.VISIBLE);


                }
            }
        }
    }

    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, 2)
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).circleCrop().into(profileImage)
    }

    private fun uploadUserImage() {

        profilePreloader.visibility = View.VISIBLE

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + getFileExtension(this, mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
//                    Log.e(
//                        "Firebase Image URL",
//                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
//                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
//                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            profilePreloader.visibility = View.GONE
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    profilePreloader.visibility = View.GONE

                }
        } else {
            profilePreloader.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == 2
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .circleCrop() // Scale type of the image.
                    .into(profileImage) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        uploadUserImage()
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun TextView.underline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}