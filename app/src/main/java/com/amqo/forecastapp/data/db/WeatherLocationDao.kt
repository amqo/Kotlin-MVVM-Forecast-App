package com.amqo.forecastapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amqo.forecastapp.data.db.entity.WEATHER_LOCATION_ID
import com.amqo.forecastapp.data.db.entity.WeatherLocation

private const val SELECT_QUERY = "select * from weather_location where id = $WEATHER_LOCATION_ID"

@Dao
interface WeatherLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(weatherLocation: WeatherLocation)

    @Query(SELECT_QUERY)
    fun getLocation(): LiveData<WeatherLocation>

    @Query(SELECT_QUERY)
    fun getLocationNonLive(): WeatherLocation?
}