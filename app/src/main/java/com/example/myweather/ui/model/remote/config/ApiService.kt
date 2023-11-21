package com.example.myweather.ui.model.remote.config

import com.example.myweather.ui.model.remote.model.CurrentLocationWeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiService {

    @GET("weather/")
    suspend fun getCurrentLocationWeather(
        @QueryMap queries : MutableMap<String, Any>
    ) : Response<CurrentLocationWeatherResponse>

}