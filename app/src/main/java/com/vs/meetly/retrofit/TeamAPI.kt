package com.vs.meetly.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface TeamAPI {
    @GET("get-meetly-data/")
    fun getData():Call<HashMap<String,Map<String,String>>>
}