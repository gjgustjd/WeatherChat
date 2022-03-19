package com.miso.misoweather.model.DTO.Forecast.CurrentAir

import java.io.Serializable

data class CurrentAirResponseDto(
    val data: CurrentAirData,
    val message: String,
    val status: String
) : Serializable