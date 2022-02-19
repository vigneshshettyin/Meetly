package com.vs.meetly.daos

import com.google.firebase.firestore.FirebaseFirestore


class SupportDao {
    private val db = FirebaseFirestore.getInstance()
    val supportCollection = db.collection("support")

}