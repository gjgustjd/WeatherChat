package com.miso.misoweather.model.DTO.Forecast

import com.miso.misoweather.model.DTO.RegionListResponse.Region

data class ForecastBriefData(
    val forecast: Forecast,
    val forecastInfo: ForecastInfo,
    val region: Region
)