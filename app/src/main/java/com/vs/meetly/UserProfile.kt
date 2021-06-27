@file:Suppress("DEPRECATION")

package com.vs.meetly

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
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
import www.sanju.motiontoast.MotionToast
import java.io.IOException

class UserProfile : AppCompatActivity() {

    // A global variable for a user profile image URL
    private var mProfileImageURL: String = ""

    private var currentProfileImage : String = ""

    private lateinit var mProgressDialog: Dialog

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        auth = FirebaseAuth.getInstance()

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
                /*Requests permissions to be granted to this application. These permissions
                     must be requested in your manifest, they should not be granted to your app,
                     and they should have protection level*/
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
    }

    private fun updateUserProfileData() {

        val name=profileName.text.toString()
        val email=profileEmail.text.toString()
        val phone=profilePhone.text.toString()
//        val userAvatar = mProfileImageURL
        var userAvatar = ""
        if (mProfileImageURL.isEmpty()){
            userAvatar = currentProfileImage
        }
        else{
            userAvatar = mProfileImageURL
        }

//        mProgressDialog.dismiss()

//        Toast.makeText(this, "${name}, ${email}, ${phone}, ${userAvatar}", Toast.LENGTH_SHORT).show()

        val user = User(auth.currentUser!!.uid, name, email, phone.toLong(), userAvatar)

        GlobalScope.launch {
            val userdao = UserDao()
            userdao.addUser(user)
        }

        MotionToast.createToast(this,
            "Success!",
            "Profile updated!",
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this,R.font.helvetica_regular))

        setResult(Activity.RESULT_OK)

    }

    private fun getUserInfo(userId : String) {

        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(userId).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                loadImage(user.imageUrl)
                currentProfileImage = user.imageUrl
                profileName.setText(user.displayName)
                profileEmail.setText(user.email)
                profilePhone.setText(user.phone.toString())
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

        progressPlsWait()

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
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            mProgressDialog.dismiss()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()

                    mProgressDialog.dismiss()

                }
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
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
    fun progressPlsWait(){
        //Progress Bar
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

        //        mProgressDialog.tv_progress_text.text = text
        //Start the dialog and display it on screen.
        mProgressDialog.show()
        // Call a function to update user details in the database.
    }
}