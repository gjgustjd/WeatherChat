package com.miso.misoweather.model.dto.forecast.brief

import com.miso.misoweather.model.dto.forecast.ForecastDetailInfo
import com.miso.misoweather.model.dto.Region
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
    val windSpeedComment:String,
    val humidity:String,
    val humidityIcon:String
):Serializable