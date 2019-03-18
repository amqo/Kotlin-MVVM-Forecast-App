package com.amqo.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amqo.forecastapp.data.db.entity.CURRENT_WEATHER_ID
import com.amqo.forecastapp.data.db.entity.CurrentWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.current.ImperialCurrentWeatherEntry
import com.amqo.forecastapp.data.db.unitlocalized.current.MetricCurrentWeatherEntry

private const val SELECT_QUERY = "select * from current_weather where id = $CURRENT_WEATHER_ID"

@Dao
interface CurrentWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherEntry: CurrentWeatherEntry)

    @Query(SELECT_QUERY)
    fun getMetric(): LiveData<MetricCurrentWeatherEntry>

    @Query(SELECT_QUERY)
    fun getImperial(): LiveData<ImperialCurrentWeatherEntry>
}