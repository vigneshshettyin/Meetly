package com.vs.meetly

import android.app.Dialog
import android.content.Context

object SupportClass {
    private lateinit var mProgressDialog: Dialog


    fun showProgressDialog(context : Context) {
        mProgressDialog = Dialog(context)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.dialog_progress)

//        mProgressDialog.tv_progress_text.text = text

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun progressBarDismiss(){
        mProgressDialog.dismiss()
    }


}