package com.example.myweather.ui

import androidx.lifecycle.ViewModel
import com.example.myweather.ui.model.remote.model.CurrentLocationWeatherResponse
import com.example.myweather.ui.model.remote.model.NetworkResult
import com.example.myweather.ui.model.remote.model.WeatherRepository
import kotlinx.coroutines.flow.Flow

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun getCurrentLocationWeather(lat : Double, lon : Double) : Flow<NetworkResult<CurrentLocationWeatherResponse>> = repository.getCurrentLocationWeather(lat, lon)

}