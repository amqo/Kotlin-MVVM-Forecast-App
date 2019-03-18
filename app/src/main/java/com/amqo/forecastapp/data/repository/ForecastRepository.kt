package com.amqo.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.db.entity.WeatherLocation
import com.amqo.forecastapp.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import org.threeten.bp.LocalDate

interface ForecastRepository {

    suspend fun getCurrentWeather(
        metric: Boolean
    ): LiveData<out UnitSpecificCurrentWeatherEntry>

    suspend fun getFutureWeather(
        metric: Boolean, startDate: LocalDate
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>>

    suspend fun getFutureWeatherByDate(
        date: LocalDate, metric: Boolean
    ): LiveData<out UnitSpecificDetailFutureWeatherEntry>

    suspend fun getWeatherLocation(): LiveData<out WeatherLocation>
}