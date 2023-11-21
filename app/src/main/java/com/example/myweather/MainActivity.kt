    package com.example.myweather

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweather.ui.WeatherViewModel
import com.example.myweather.ui.WeatherViewModelFactory
import com.example.myweather.ui.screen.WeatherScreen
import com.example.myweather.ui.theme.MyWeatherTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyWeatherTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp(){
    //val viewModel : WeatherViewModel = viewModel(factory = WeatherViewModelFactory.getInstance())
    WeatherScreen(
        modifier = Modifier
            .fillMaxSize()
    )

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyWeatherTheme {

    }
}