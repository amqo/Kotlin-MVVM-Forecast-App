package com.amqo.forecastapp.data.network

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import com.amqo.forecastapp.data.network.response.FutureWeatherResponse

interface WeatherNetworkDataSource {

    val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
    val downloadedFutureWeather: LiveData<FutureWeatherResponse>

    suspend fun fetchCurrentWeather(
        location: String,
        languageCode: String = "en"
    )

    suspend fun fetchFutureWeather(
        location: String,
        languageCode: String = "en"
    )
}