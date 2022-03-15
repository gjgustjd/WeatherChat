package com.miso.misoweather.model.DTO.Forecast.Daily

import java.io.Serializable

data class DailyForecastResponseDto(
    val data: DailyForecastData,
    val message: String,
    val status: String
):Serializable