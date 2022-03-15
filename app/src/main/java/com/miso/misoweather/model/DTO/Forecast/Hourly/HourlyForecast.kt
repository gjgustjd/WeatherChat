package com.miso.misoweather.model.DTO.Forecast.Hourly

import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class HourlyForecast(
    val createdAt: String,
    val forecastTime: String,
    val temperature: String,
    val weather: String
) : Serializable