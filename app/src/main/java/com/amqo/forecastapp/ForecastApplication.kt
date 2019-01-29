package com.amqo.forecastapp

import android.app.Application
import android.preference.PreferenceManager
import com.amqo.forecastapp.data.db.ForecastDatabase
import com.amqo.forecastapp.data.network.*
import com.amqo.forecastapp.data.provider.LocationProviderImpl
import com.amqo.forecastapp.data.provider.UnitProvider
import com.amqo.forecastapp.data.provider.UnitProviderImpl
import com.amqo.forecastapp.data.repository.ForecastRepository
import com.amqo.forecastapp.data.repository.ForecastRepositoryImpl
import com.amqo.forecastapp.ui.weather.current.CurrentWeatherViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class ForecastApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind() from singleton { LocationProviderImpl() }
        bind<ForecastRepository>() with singleton {
            ForecastRepositoryImpl(instance(), instance(), instance(), instance())
        }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind() from singleton { CurrentWeatherViewModelFactory(instance(), instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
    }
}