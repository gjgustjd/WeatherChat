package com.miso.misoweather.model.DTO.Forecast

data class ForecastDetailInfo(
    val humidity: String,
    val humidityValue: String,
    val rainSnow: String,
    val rainSnowPossibility: String,
    val rainSnowValue: String,
    val temperatureMax: String,
    val temperatureMin: String,
    val windSpeed: String,
    val windSpeedValue: String
)