package com.miso.misoweather.model.DTO.Forecast.Hourly

import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class HourlyForecastData(
    val hourlyForecastList:List<HourlyForecast>,
    val region: Region,
):Serializable