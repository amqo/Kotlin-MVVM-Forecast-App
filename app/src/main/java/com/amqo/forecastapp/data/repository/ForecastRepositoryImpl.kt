package com.amqo.forecastapp.data.repository

import androidx.lifecycle.LiveData
import com.amqo.forecastapp.data.db.CurrentWeatherDao
import com.amqo.forecastapp.data.db.FutureWeatherDao
import com.amqo.forecastapp.data.db.WeatherLocationDao
import com.amqo.forecastapp.data.db.entity.WeatherLocation
import com.amqo.forecastapp.data.db.unitlocalized.current.UnitSpecificCurrentWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.future.detail.UnitSpecificDetailFutureWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.future.list.UnitSpecificSimpleFutureWeatherEntry
import com.amqo.forecastapp.data.network.FORECAST_DAYS_COUNT
import com.amqo.forecastapp.data.network.WeatherNetworkDataSource
import com.amqo.forecastapp.data.network.response.CurrentWeatherResponse
import com.amqo.forecastapp.data.network.response.FutureWeatherResponse
import com.amqo.forecastapp.data.provider.LocationProvider
import com.amqo.forecastapp.internal.observeForever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime
import java.util.*

private const val MINUTES_CACHE: Long = 30

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherLocationDao: WeatherLocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val locationProvider: LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            observeForever(downloadedCurrentWeather) { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            observeForever(downloadedFutureWeather) { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }

    override suspend fun getCurrentWeather(
        metric: Boolean
    ): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) {
                currentWeatherDao.getMetric()
            } else {
                currentWeatherDao.getImperial()
            }
        }
    }

    override suspend fun getFutureWeatherByDate(
        date: LocalDate,
        metric: Boolean
    ): LiveData<out UnitSpecificDetailFutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) {
                futureWeatherDao.getDetailedWeatherByDateMetric(date)
            } else {
                futureWeatherDao.getDetailedWeatherByDateImperial(date)
            }
        }
    }

    override suspend fun getFutureWeather(
        metric: Boolean,
        startDate: LocalDate
    ): LiveData<out List<UnitSpecificSimpleFutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext if (metric) {
                futureWeatherDao.getSimpleWeatherForecastsMetric(startDate)
            } else {
                futureWeatherDao.getSimpleWeatherForecastsImperial(startDate)
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

    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO) {
            futureWeatherDao.deleteOldEntries(LocalDate.now())
            futureWeatherDao.insert(fetchedWeather.futureWeatherEntries.entries)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()
        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }
        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)) {
            fetchCurrentWeather()
        }
        if (isFetchFutureNeeded()) {
            fetchFutureWeather()
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

    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString(),
            Locale.getDefault().language
        )
    }

    private fun isFetchFutureNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}