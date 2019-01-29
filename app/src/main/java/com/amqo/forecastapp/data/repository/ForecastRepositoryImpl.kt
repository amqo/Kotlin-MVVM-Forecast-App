package com.amqo.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.db.CurrentWeatherDao
import com.amqo.forecastapp.data.db.WeatherLocationDao
import com.amqo.forecastapp.data.db.entity.WeatherLocation
import com.amqo.forecastapp.data.db.unitlocalized.UnitSpecificCurrentWeatherEntry
import com.amqo.forecastapp.data.network.WeatherNetworkDataSource
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import com.amqo.forecastapp.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

private const val MINUTES_CACHE: Long = 30

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
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

    override suspend fun getWeatherLocation(): LiveData<out WeatherLocation> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocation().value
        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation) ||
            isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
            fetchCurrentWeather()
        }
    }

    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val cacheMinutesAgo = ZonedDateTime.now().minusMinutes(MINUTES_CACHE)
        return lastFetchTime.isBefore(cacheMinutesAgo)
    }
}