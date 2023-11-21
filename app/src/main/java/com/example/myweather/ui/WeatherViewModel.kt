package com.example.myweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweather.model.remote.model.CurrentLocationWeatherResponse
import com.example.myweather.model.remote.model.NetworkResult
import com.example.myweather.model.remote.model.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _currentLocationWeather = MutableStateFlow<NetworkResult<CurrentLocationWeatherResponse>>(NetworkResult.Loading)
    val currentLocationWeather : StateFlow<NetworkResult<CurrentLocationWeatherResponse>> = _currentLocationWeather

    fun getCurrentLocationWeather(lat : Double, lon : Double) = viewModelScope.launch {
        repository.getCurrentLocationWeather(lat, lon).collectLatest { networkResult ->
            _currentLocationWeather.value = networkResult
        }
    }

}