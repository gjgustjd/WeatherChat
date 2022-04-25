package com.miso.misoweather.model.dto.forecast.currentAir

import java.io.Serializable

data class CurrentAirResponseDto(
    val data: CurrentAirData,
    val message: String,
    val status: String
) : Serializable