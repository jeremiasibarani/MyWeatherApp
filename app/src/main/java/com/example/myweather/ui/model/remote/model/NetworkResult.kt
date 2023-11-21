package com.example.myweather.ui.model.remote.model

sealed class NetworkResult<out R> private constructor(){
    data class Success<out T>(val data : T) : NetworkResult<T>()
    data class Error(val code : Int, val message : String) : NetworkResult<Nothing>()
    data class Exception(val message : String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}