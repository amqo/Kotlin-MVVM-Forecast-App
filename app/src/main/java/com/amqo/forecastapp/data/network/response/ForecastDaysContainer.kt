package com.amqo.forecastapp.data.network.response

import com.amqo.forecastapp.data.db.entity.FutureWeatherEntry
import com.google.gson.annotations.SerializedName

data class ForecastDaysContainer(
    @SerializedName("forecastday")
    val entries: List<FutureWeatherEntry>
)