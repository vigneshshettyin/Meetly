package com.vs.meetly.retrofit

import com.vs.meetly.modals.SupportAPI
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val BASE_URL = "https://meetlyapiv2.herokuapp.com/"

interface SupportAPIInterface{

    @GET("api/v2/")
    fun getAllUpdates() : Call<SupportAPI>
}

object SupportAPIService{
    val SupportAPIInstance : SupportAPIInterface
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        SupportAPIInstance = retrofit.create(SupportAPIInterface::class.java)
    }
}