package com.example.myweather.model.remote.model

import com.example.myweather.BuildConfig
import com.example.myweather.model.remote.config.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiService : ApiService
) {
    fun getCurrentLocationWeather(lat : Double, lon : Double) : Flow<NetworkResult<CurrentLocationWeatherResponse>> = flow {
        emit(NetworkResult.Loading)
        try {
            val queries = mutableMapOf<String, Any>()
            queries["lat"] = lat
            queries["lon"] = lon
            queries["appid"] = BuildConfig.API_KEY
            queries["units"] = "metric"
            queries["lang"] = "id"

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