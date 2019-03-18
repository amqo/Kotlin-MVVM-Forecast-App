package com.amqo.forecastapp.ui.base

import androidx.lifecycle.ViewModel
import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.internal.UnitSystem
import com.amqo.forecastapp.internal.lazyDeferred

abstract class WeatherViewModel(
    private val forecastRepository: ForecastRepository,
    unitProvider: UnitProvider
) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem()

    val isMetricUnit: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weatherLocation by lazyDeferred {
        forecastRepository.getWeatherLocation()
    }
}