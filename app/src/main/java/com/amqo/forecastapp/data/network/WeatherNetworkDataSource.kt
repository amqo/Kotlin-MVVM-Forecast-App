package com.amqo.forecastapp.data.network

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {

    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>

    suspend fun fetchCurrentWeather(
        location: String,
        languageCode: String = "en"
    )
}