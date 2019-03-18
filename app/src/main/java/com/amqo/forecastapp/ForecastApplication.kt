package com.amqo.forecastapp

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.amqo.forecastapp.data.db.ForecastDatabase
import com.amqo.forecastapp.data.network.*
import com.amqo.forecastapp.data.provider.LocationProviderImpl
import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.provider.UnitProviderImpl
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.data.repository.ForecastRepositoryImpl
import com.amqo.forecastapp.ui.weather.current.CurrentWeatherViewModelFactory
import com.amqo.forecastapp.ui.weather.future.detail.FutureDetailWeatherViewModelFactory
import com.amqo.forecastapp.ui.weather.future.list.FutureListWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

class ForecastApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().futureWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind() from singleton { LocationProviderImpl(instance(), instance()) }
        bind<ForecastRepository>() with singleton {
            ForecastRepositoryImpl(instance(), instance(), instance(), instance(), instance())
        }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind() from singleton { CurrentWeatherViewModelFactory(instance(), instance()) }
        bind() from singleton { FutureListWeatherViewModelFactory(instance(), instance()) }
        bind() from factory { detailDate: LocalDate ->
            FutureDetailWeatherViewModelFactory(detailDate, instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}