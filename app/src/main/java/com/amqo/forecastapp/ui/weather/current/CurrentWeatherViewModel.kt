package com.amqo.forecastapp.ui.weather.current

import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.internal.lazyDeferred
import com.amqo.forecastapp.ui.base.WeatherViewModel

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : WeatherViewModel(forecastRepository, unitProvider) {

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(super.isMetricUnit)
    }
}
