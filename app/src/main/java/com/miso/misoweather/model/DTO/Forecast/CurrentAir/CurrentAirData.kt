package com.miso.misoweather.model.DTO.Forecast.CurrentAir

import com.miso.misoweather.model.DTO.Region
import java.io.Serializable

data class CurrentAirData(
    val bigScale: String,
    val fineDust: String,
    val fineDustGrade: String,
    val fineDustIcon: String,
    val ultraFineDust: String,
    val ultraFineDustGrade: String,
    val ultraFineDustIcon: String,
) : Serializable