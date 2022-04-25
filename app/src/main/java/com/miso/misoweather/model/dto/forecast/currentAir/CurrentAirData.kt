package com.miso.misoweather.model.dto.forecast.currentAir

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