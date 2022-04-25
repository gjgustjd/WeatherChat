package com.miso.misoweather.model.dto.forecast.brief

import java.io.Serializable

data class ForecastBriefResponseDto(
    val data: ForecastBriefData,
    val message: String,
    val status: String
):Serializable