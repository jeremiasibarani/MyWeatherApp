package com.example.myweather.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.example.myweather.ui.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweather.model.remote.model.NetworkResult
import com.example.myweather.ui.WeatherViewModelFactory

private lateinit var locationCallback : LocationCallback
private lateinit var locationProvider : FusedLocationProviderClient

data class LatLon(
    val lat : Double = 0.0,
    val lon : Double = 0.0
)

@Composable
fun WeatherScreen(
    modifier : Modifier,
    viewModel : WeatherViewModel = viewModel(factory = WeatherViewModelFactory.getInstance())
){
    val context = LocalContext.current
    val userLocation = getUserLocation(context)

    val currentLocation by viewModel.currentLocationWeather.collectAsState()

    // Todo(the api call should be run inside a side-effect)
    LaunchedEffect(key1 = Unit){
        viewModel.getCurrentLocationWeather(userLocation.lat, userLocation.lon)
    }

    when(val state = currentLocation){
        is NetworkResult.Loading -> {

        }
        is NetworkResult.Success -> {
            Text(
                modifier = modifier,
                textAlign = TextAlign.Center,
                text = "${state.data}"
            )
        }
        is NetworkResult.Error -> {

        }
        is NetworkResult.Exception -> {

        }
    }


}

@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(context : Context) : LatLon{
    locationProvider = LocationServices.getFusedLocationProviderClient(context)

    var currentUserLocation by remember {
        mutableStateOf(LatLon())
    }

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val permissionsGranted = permissions.values.reduce { acc, isPermissionGranted ->
                acc && isPermissionGranted
            }

            if(!permissionsGranted){
                Toast.makeText(context, "Permission is required", Toast.LENGTH_SHORT).show()
            }else{
                // Get current location here and launch weather api request
                locationUpdate()
            }
        }
    )

    DisposableEffect(key1 = locationProvider){
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                locationProvider.lastLocation.addOnSuccessListener { location ->
                    location?.let{
                        val lat = it.latitude
                        val lon = it.longitude
                        currentUserLocation = LatLon(lat, lon)
                    }
                }.addOnFailureListener {
                    Log.e("Location-ERROR", it.message.toString())
                }
            }
        }

        onDispose {
            stopLocationUpdate()
        }
    }

    SideEffect {
        if(locationPermissionGranted(context)){
            locationUpdate()
        }else{
            locationPermissionLauncher.launch(locationPermissions)
        }
    }

    return currentUserLocation
}

fun stopLocationUpdate(){
    try{
        val removeTask = locationProvider.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("LOCATION_TAG", "Location Callback removed.")
            } else {
                Log.d("LOCATION_TAG", "Failed to remove Location Callback.")
            }
        }
    }catch (ex : Exception){
        Log.e("LOCATION_TAG", "Failed to remove Location Callback.. $ex")
    }
}

@SuppressLint("MissingPermission")
fun locationUpdate(){
    locationCallback.let{
        val locationRequest : LocationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationProvider.requestLocationUpdates(
            locationRequest,
            it,
            Looper.getMainLooper()
        )
    }
}

private fun locationPermissionGranted(context : Context) = ContextCompat.checkSelfPermission(
    context,
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED