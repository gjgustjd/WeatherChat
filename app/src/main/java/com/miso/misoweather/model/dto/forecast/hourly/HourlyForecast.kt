package com.miso.misoweather.model.dto.forecast.hourly

import java.io.Serializable

data class HourlyForecast(
    val createdAt: String,
    val forecastTime: String,
    val temperature: String,
    val weather: String
) : Serializable