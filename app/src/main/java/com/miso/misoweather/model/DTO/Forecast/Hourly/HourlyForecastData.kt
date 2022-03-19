package com.miso.misoweather.model.DTO.Forecast.Hourly

import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class HourlyForecastData(
    val hourlyForecastList:List<HourlyForecast>,
    val region: Region,
    val humidity:String,
    val humidityIcon:String,
    val pop:String,
    val popIcon:String,
    val rain:String,
    val snow:String,
    val windSpeed:String,
    val windSpeedIcon:String
):Serializable