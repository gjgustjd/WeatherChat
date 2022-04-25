package com.miso.misoweather.model.dto.forecast.hourly

import com.miso.misoweather.model.dto.Region
import java.io.Serializable

data class HourlyForecastData(
    val hourlyForecastList:List<HourlyForecast>,
    val region: Region,
):Serializable