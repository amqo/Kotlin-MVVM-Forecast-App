package com.amqo.forecastapp.ui.weather.future.list

import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.internal.lazyDeferred
import com.amqo.forecastapp.ui.base.WeatherViewModel
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weatherEntries by lazyDeferred {
        forecastRepository.getFutureWeather(super.isMetricUnit, LocalDate.now())
    }
}
