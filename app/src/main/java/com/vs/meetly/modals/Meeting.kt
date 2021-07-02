package com.vs.meetly.modals

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Meeting (
        val date : String = "",
        val title : String = "",
        val content : String = "",
        val meeting_link : String = "",
        val time : String = "",
        val userId : ArrayList<String> = ArrayList(),
        val color : String = "",
        ) : Parcelable