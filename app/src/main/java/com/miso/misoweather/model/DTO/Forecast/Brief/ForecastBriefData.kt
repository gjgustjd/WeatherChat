package com.miso.misoweather.model.DTO.Forecast.Brief

import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Region

data class ForecastBriefData(
    val forecast: Forecast,
    val forecastInfo: ForecastDetailInfo,
    val region: Region
)