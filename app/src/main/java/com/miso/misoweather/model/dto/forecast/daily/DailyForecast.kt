package com.miso.misoweather.model.dto.forecast.daily

import java.io.Serializable

data class DailyForecast (
    val createdAt: String,
    val forecastTime: String,
    val maxTemperature: String,
    val minTemperature: String,
    val weather: String,
    val pop:String,
    val popIcon:String,
    val rain:String,
    val snow:String
):Serializable