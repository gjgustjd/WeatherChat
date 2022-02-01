package com.miso.misoweather.model.DTO.Forecast.Brief

data class ForecastBriefResponseDto(
    val data: ForecastBriefData,
    val message: String,
    val status: String
)