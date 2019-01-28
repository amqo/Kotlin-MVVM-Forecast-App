package com.amqo.forecastapp.ui.weather.current

import androidx.lifecycle.ViewModel;
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.internal.UnitSystem
import com.amqo.forecastapp.internal.lazyDeferred

class CurrentWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {

    private val unitSystem = UnitSystem.METRIC //TODO get from settings
    private val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }
}
