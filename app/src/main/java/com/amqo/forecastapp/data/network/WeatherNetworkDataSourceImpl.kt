package com.amqo.forecastapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import com.amqo.forecastapp.internal.NoConnectivityException

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
}