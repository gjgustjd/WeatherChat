package com.miso.misoweather.model.DTO.Forecast.Brief

import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Region

data class ForecastBriefData(
    val forecastInfo: ForecastDetailInfo,
    val temperatureMax: String,
    val temperatureMin: String,
    val weather: String,
    val region: Region
)