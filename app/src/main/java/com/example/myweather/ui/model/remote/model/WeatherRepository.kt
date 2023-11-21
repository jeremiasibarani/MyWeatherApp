package com.example.myweather.ui.model.remote.model

import com.example.myweather.ui.model.remote.config.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val apiService : ApiService
) {

    fun getCurrentLocationWeather(lat : Double, lon : Double) : Flow<NetworkResult<CurrentLocationWeatherResponse>> = flow {
        emit(NetworkResult.Loading)
        try {
            val queries = mutableMapOf<String, Any>()
            queries["lat"] = lat
            queries["lon"] = lon
            queries["appid"] = ""
            queries["units"] = "metric"

            val response = apiService.getCurrentLocationWeather(queries)
            val responseBody = response.body()

            if(response.isSuccessful && responseBody != null){
                emit(NetworkResult.Success(responseBody))
            }else{
                emit(NetworkResult.Error(response.code(), response.message()))
            }

        }catch (ex : Exception){
            emit(NetworkResult.Exception("Something went wrong"))
        }
    }

}