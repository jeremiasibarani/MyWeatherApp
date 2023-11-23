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
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myweather.R
import com.example.myweather.model.remote.model.CurrentLocationWeatherResponse
import com.example.myweather.model.remote.model.NetworkResult
import com.example.myweather.ui.WeatherViewModel
import com.example.myweather.ui.WeatherViewModelFactory
import com.example.myweather.ui.theme.MyWeatherTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

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

    var shouldFecthedWeatherData by remember{
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .padding(vertical = 20.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(shouldFecthedWeatherData){
            val userLocation = getUserLocation(context)
            val currentLocation by viewModel.currentLocationWeather.collectAsState()

            Log.i("LOCATION-TAG", "$userLocation")
            LaunchedEffect(key1 = userLocation){
                if(userLocation.lat != 0.0 && userLocation.lon != 0.0){
                    viewModel.getCurrentLocationWeather(userLocation.lat, userLocation.lon)
                }
            }

            when(val state = currentLocation){
                is NetworkResult.Loading -> {
                    WeatherLoading()
                }
                is NetworkResult.Success -> {
                    WeatherSuccess(
                        data = state.data
                    )
                }
                is NetworkResult.Error -> {
                    WeatherErrorException()
                }
                is NetworkResult.Exception -> {
                    WeatherErrorException()
                }
            }
        }else{
            Button(onClick = { shouldFecthedWeatherData = true }) {
                Icon(painter = painterResource(id = R.drawable.ic_sunny_weather), contentDescription = null, modifier = Modifier.padding(end = 10.dp))
                Text(text = "Find my weather")
            }
        }
    }


}

//Todo( - get more animation for each weather condition code : see (https://openweathermap.org/weather-conditions) for reference
// - create a button to trigger the weather location request
// - display progressbar as response to loading state
// - toast or perhaps snackbar as response to error or exception state
// - implement dependency injection
// - make a short video out of it and post it on linkedin

@Composable
private fun WeatherSuccess(
    modifier: Modifier = Modifier,
    data : CurrentLocationWeatherResponse
){
    WeatherAnimation(
        modifier = modifier,
        rawResourceId = getWeatherAnimation(data.weather[0].main)
    )
    Text(
        text = stringResource(id = R.string.city_and_degree, data.name, data.main.temp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.displaySmall.copy(
            fontSize = 24.sp,
            fontWeight = FontWeight.W600
        )
    )
    Text(
        text = "${data.weather[0].main}, ${data.weather[0].description}",
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Normal,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun WeatherLoading(
    modifier : Modifier = Modifier
){
    WeatherAnimation(
        modifier = modifier
            .size(250.dp),
        rawResourceId = R.raw.loading
    )
}

@Composable
private fun WeatherErrorException(
    modifier : Modifier = Modifier,
    message : String = "Something went wrong"
){
    Text(
        text = message,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
private fun WeatherAnimation(
    modifier: Modifier = Modifier
        .size(400.dp),
    @RawRes rawResourceId : Int
){
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            rawResourceId
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        composition = preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier
    )
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

private fun getWeatherAnimation(weather : String) : Int{
    return when(weather){
        "Thunderstorm" -> R.raw.daylight_thunderstorm
        "Drizzle" -> R.raw.daylight_atmosphere
        "Rain" -> R.raw.daylight_rain
        "Snow" -> R.raw.daylight_snow
        "Atmosphere" -> R.raw.daylight_atmosphere
        "Clear" -> R.raw.daylight_clear
        "Clouds" -> R.raw.daylight_cloud
        else -> R.raw.daylight_clear
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherScreenPreview(){
    MyWeatherTheme {

    }
}