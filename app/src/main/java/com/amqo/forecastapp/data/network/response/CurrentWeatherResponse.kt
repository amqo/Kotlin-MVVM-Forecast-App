package com.amqo.forecastapp.data.network.response

import com.amqo.forecastapp.data.db.entity.CurrentWeatherEntry
import com.amqo.forecastapp.data.db.entity.Location
import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    val location: Location,
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry
)