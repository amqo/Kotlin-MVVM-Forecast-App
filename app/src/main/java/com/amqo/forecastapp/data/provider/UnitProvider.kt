package com.amqo.forecastapp.data.provider

import com.amqo.forecastapp.internal.UnitSystem

interface UnitProvider {

    fun getUnitSystem(): UnitSystem
}