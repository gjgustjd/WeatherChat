package com.miso.misoweather.model.DTO.Forecast.Brief

import java.io.Serializable

data class ForecastBriefResponseDto(
    val data: ForecastBriefData,
    val message: String,
    val status: String
):Serializable