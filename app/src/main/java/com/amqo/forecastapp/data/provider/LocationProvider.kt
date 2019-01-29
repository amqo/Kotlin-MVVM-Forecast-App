package com.amqo.forecastapp.data.provider

import com.amqo.forecastapp.data.db.entity.WeatherLocation

interface LocationProvider {

    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean

    suspend fun getPreferredLocationString(): String
}