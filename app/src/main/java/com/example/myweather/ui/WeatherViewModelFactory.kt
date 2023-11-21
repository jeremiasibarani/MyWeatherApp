package com.example.myweather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweather.model.remote.config.ApiService
import com.example.myweather.model.remote.config.RetrofitConfig
import com.example.myweather.model.remote.model.WeatherRepository

@Suppress("UNCHECKED_CAST")
class WeatherViewModelFactory private constructor(private val repository: WeatherRepository) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(WeatherViewModel::class.java)){
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }

    companion object{
        private var INSTANCE : WeatherViewModelFactory? = null
        fun getInstance() : WeatherViewModelFactory{
            if(INSTANCE == null){
                val apiService = RetrofitConfig.getInstance()
                val repository = WeatherRepository(apiService)
                INSTANCE = WeatherViewModelFactory(repository)
            }
            return INSTANCE!!
        }
    }
}