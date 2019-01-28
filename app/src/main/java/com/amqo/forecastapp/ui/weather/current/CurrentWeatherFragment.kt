package com.amqo.forecastapp.ui.weather.current

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amqo.forecastapp.R
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

//        val apiService = ApixuWeatherApiService(ConnectivityInterceptorImpl(context!!))
//        val weatherNetworkDataSource = WeatherNetworkDataSourceImpl(apiService)
//        weatherNetworkDataSource.downloadedCurrentWeather.observe(this, Observer {
//            textView.text = it.currentWeatherEntry.toString()
//            Log.e("DATA", it.currentWeatherEntry.toString())
//        })
//
//        GlobalScope.launch(Dispatchers.Default) {
//            weatherNetworkDataSource.fetchCurrentWeather("Barcelona")
//        }
    }

    private fun bindUI() = launch {
        val currentWeather = viewModel.weather.await()
        currentWeather.observe(this@CurrentWeatherFragment, Observer {
            textView.text = it.toString()
        })
    }
}
