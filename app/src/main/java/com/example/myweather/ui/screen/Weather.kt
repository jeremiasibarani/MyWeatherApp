package com.example.myweather.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.myweather.R
import com.example.myweather.ui.model.remote.model.CurrentLocationWeatherResponse

@Composable
fun WeatherScreen(
    modifier : Modifier,
    data : CurrentLocationWeatherResponse
){
    // Todo(last checkpoint)
    Column(
        modifier = Modifier
    ) {
        // Display the city name and temperature
        Text(
            text = stringResource(id = R.string.city_and_degree, data.name, data.main.temp),
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        // Display the weather condition (its name)
        Text(
            text = "${data.weather[0].main}, ${data.weather[0].description}",
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}