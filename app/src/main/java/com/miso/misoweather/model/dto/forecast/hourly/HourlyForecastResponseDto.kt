package com.miso.misoweather.model.dto.forecast.hourly

import java.io.Serializable

data class HourlyForecastResponseDto(
    val data: HourlyForecastData,
    val message: String,
    val status: String
):Serializable