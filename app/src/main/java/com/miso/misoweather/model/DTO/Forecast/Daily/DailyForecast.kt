package com.miso.misoweather.model.DTO.Forecast.Daily

import java.io.Serializable

data class DailyForecast (
    val createdAt: String,
    val forecastTime: String,
    val maxTemperature: String,
    val minTemperature: String,
    val weather: String
):Serializable