package com.miso.misoweather.model.DTO.Forecast.Hourly

import java.io.Serializable

data class HourlyForecastResponseDto(
    val data: HourlyForecastData,
    val message: String,
    val status: String
):Serializable