package com.miso.misoweather.model.DTO.Forecast.Brief

import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class ForecastBriefData(
    val forecastInfo: ForecastDetailInfo,
    val temperatureMax: String,
    val temperatureMin: String,
    val temperature: String,
    val weather: String,
    val region: Region,
    val windSpeed:String,
    val windSpeedIcon:String,
    val humidity:String,
    val humidityIcon:String
):Serializable