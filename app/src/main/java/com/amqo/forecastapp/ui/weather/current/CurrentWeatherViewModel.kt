package com.amqo.forecastapp.ui.weather.current

import androidx.lifecycle.ViewModel;
import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.internal.UnitSystem
import com.amqo.forecastapp.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}
