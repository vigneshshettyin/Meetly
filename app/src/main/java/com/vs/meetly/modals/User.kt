package com.vs.meetly.modals

data class User (
    val uid : String ="",
    val displayName : String? = "",
    val email : String = "",
    val phone : Long = 0L,
    val imageUrl : String = "")