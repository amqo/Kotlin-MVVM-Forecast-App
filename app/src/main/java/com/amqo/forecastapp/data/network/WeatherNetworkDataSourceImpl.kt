package com.amqo.forecastapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import com.amqo.forecastapp.data.network.response.FutureWeatherResponse
import com.amqo.forecastapp.internal.NoConnectivityException

const val FORECAST_DAYS_COUNT = 7

class WeatherNetworkDataSourceImpl(
    private val apixuWeatherApiService: ApixuWeatherApiService
) : WeatherNetworkDataSource {

    private val downloadedCurrentWeatherMutable = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeather: LiveData<CurrentWeatherResponse>
        get() = downloadedCurrentWeatherMutable

    override suspend fun fetchCurrentWeather(location: String, languageCode: String) {
        try {
            apixuWeatherApiService.getCurrentWeatherAsync(location, languageCode)
                .await().also {
                    downloadedCurrentWeatherMutable.postValue(it)
                }
        } catch (exception: NoConnectivityException) {
            Log.e(NoConnectivityException::class.java.simpleName, "No internet connection", exception)
            // TODO post response with state ERROR
        }
    }

    private val downloadedFutureWeatherMutable = MutableLiveData<FutureWeatherResponse>()
    override val downloadedFutureWeather: LiveData<FutureWeatherResponse>
        get() = downloadedFutureWeatherMutable

    override suspend fun fetchFutureWeather(location: String, languageCode: String) {
        try {
            apixuWeatherApiService.getFutureWeather(location, FORECAST_DAYS_COUNT, languageCode)
                .await().also {
                    downloadedFutureWeatherMutable.postValue(it)
                }
        } catch (exception: NoConnectivityException) {
            Log.e(NoConnectivityException::class.java.simpleName, "No internet connection", exception)
            // TODO post response with state ERROR
        }
    }
}