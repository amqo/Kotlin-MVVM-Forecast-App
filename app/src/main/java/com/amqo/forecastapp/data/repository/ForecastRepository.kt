package com.amqo.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.db.entity.WeatherLocation
import com.amqo.forecastapp.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {

    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>

    suspend fun getWeatherLocation(): LiveData<out WeatherLocation>
}