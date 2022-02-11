package com.miso.misoweather.model.DTO.SurveyResponse

import java.io.Serializable

data class SurveyAnswerDto(
    val answer: String,
    val description: String,
    val id: Int
):Serializable