package com.amqo.forecastapp.data.network.response

import com.amqo.forecastapp.data.db.entity.CurrentWeatherEntry
import com.amqo.forecastapp.data.db.entity.WeatherLocation
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    val location: WeatherLocation,
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry

    // TODO add state enum
)