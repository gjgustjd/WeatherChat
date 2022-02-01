package com.miso.misoweather.model.DTO.Forecast.Detail

data class ForecastDetailResponseDto(
    val data: ForecastDetailData,
    val message: String,
    val status: String
)