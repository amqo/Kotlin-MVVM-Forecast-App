package com.amqo.forecastapp.data.provider

import com.amqo.forecastapp.data.db.entity.WeatherLocation

class LocationProviderImpl : LocationProvider {

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        return true
    }

    override suspend fun getPreferredLocationString(): String {
        return "Barcelona"
    }
}