package com.amqo.forecastapp.ui.weather.current

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.amqo.forecastapp.R
import com.amqo.forecastapp.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.amqo.forecastapp.internal.consume
import com.amqo.forecastapp.internal.glide.GlideApp
import com.amqo.forecastapp.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.current_weather_fragment.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class CurrentWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: CurrentWeatherViewModelFactory by instance()

    private lateinit var viewModel: CurrentWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.current_weather_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
        .get(CurrentWeatherViewModel::class.java)

        bindUI()
    }

    private fun bindUI() = launch {
        group_loading.visibility = View.VISIBLE
        consume(viewModel.weather, { weather ->
            group_loading.visibility = View.GONE
            bindUIWithWeather(weather)
        }, {
            Log.e(CurrentWeatherFragment::class.java.simpleName, "Error getting weather")
            // TODO show some visual feedback for this loading error
        })
        consume(viewModel.weatherLocation, { weatherLocation ->
            updateActionBar(weatherLocation.name)
        }, {
            updateActionBar("Weather")
        })
    }

    private fun bindUIWithWeather(weather: UnitSpecificCurrentWeatherEntry) {
        with(weather) {
            updateTemperatures(temperature, feelsLikeTemperature)
            updateCondition(conditionText)
            updatePrecipitation(precipitationVolume)
            updateWind(windDirection, windSpeed)
            updateVisibility(visibilityDistance)
            updateConditionIcon(conditionIconUrl)
        }
    }

    private fun updateConditionIcon(iconUrl: String) {
        GlideApp.with(this@CurrentWeatherFragment)
            .load("http:$iconUrl")
            .into(imageView_condition_icon)
    }

    private fun updateActionBar(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "Today"
    }

    private fun updateTemperatures(temperature: Double, feelsLike: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("ºC", "ºF")
        textView_temperature.text = "$temperature $unitAbbreviation"
        textView_feels_like_temperature.text = "Feels like: $feelsLike $unitAbbreviation"
    }

    private fun updateCondition(condition: String) {
        textView_condition.text = condition
    }

    private fun updatePrecipitation(precipitation: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("mm", "in")
        textView_precipitation.text = "Precipitation: $precipitation $unitAbbreviation"
    }

    private fun updateWind(windDirection: String, windSpeed: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("kph", "mph")
        textView_wind.text = "Wind: $windDirection, $windSpeed $unitAbbreviation"
    }

    private fun updateVisibility(visibilityDistance: Double) {
        val unitAbbreviation = chooseLocalizedUnitAbbreviation("km", "mi")
        textView_visibility.text = "Visibility: $visibilityDistance $unitAbbreviation"
    }

    private fun chooseLocalizedUnitAbbreviation(metric: String, imperial: String): String {
        return if (viewModel.isMetricUnit) metric else imperial
    }
}
