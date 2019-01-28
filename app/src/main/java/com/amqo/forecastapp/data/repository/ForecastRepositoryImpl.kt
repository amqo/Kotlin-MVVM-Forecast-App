package com.amqo.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.db.CurrentWeatherDao
import com.amqo.forecastapp.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry
import com.amqo.forecastapp.data.network.WeatherNetworkDataSource
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

private const val MINUTES_CACHE: Long = 30

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : ForecastRepository {

    init {
        weatherNetworkDataSource.downloadedCurrentWeather.observeForever { newCurrentWeather ->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }

    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) {
                currentWeatherDao.getMetric()
            } else {
                currentWeatherDao.getImperial()
            }
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
        }
    }

    private suspend fun initWeatherData() {
        if (isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1))) {
            fetchCurrentWeather()
        }
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            "Los Angeles",
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val cacheMinutesAgo = ZonedDateTime.now().minusMinutes(MINUTES_CACHE)
        return lastFetchTime.isBefore(cacheMinutesAgo)
    }
}