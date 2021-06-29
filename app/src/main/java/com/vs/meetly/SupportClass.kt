package com.vs.meetly

import android.app.Activity
import android.content.Context
import androidx.core.content.res.ResourcesCompat
import www.sanju.motiontoast.MotionToast


object SupportClass {

    fun alertUserDanger(context: Context, message : String){
        MotionToast.createColorToast(
            context as Activity,
            "Failed ☹️",
            message,
            MotionToast.TOAST_ERROR,
            MotionToast.GRAVITY_TOP,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(context, R.font.catamaran)
        )
    }

    fun alertSuccess(context: Context, message : String){
        fun alertUserDanger(context: Context, message : String){
            MotionToast.createColorToast(
                context as Activity,
                "Hurray success 😍",
                message,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(context, R.font.catamaran)
            )
        }
    }



}