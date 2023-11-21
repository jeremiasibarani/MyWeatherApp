package com.example.myweather.ui.model.remote.config

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitConfig {

    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private val client = OkHttpClient.Builder()
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    private var INSTANCE :ApiService? = null
    fun getInstance() : ApiService{
        if(INSTANCE == null){
            INSTANCE = retrofit.create(ApiService::class.java)
        }
        return INSTANCE!!
    }

}