package com.miso.misoweather.model.DTO.Forecast.Detail

import com.miso.misoweather.model.DTO.Forecast.Forecast
import com.miso.misoweather.model.DTO.Forecast.ForecastDetailInfo
import com.miso.misoweather.model.DTO.Region

data class ForecastDetailData(
    val forecastByTime: List<Forecast>,
    val forecastInfo: ForecastDetailInfo,
    val region: Region
)