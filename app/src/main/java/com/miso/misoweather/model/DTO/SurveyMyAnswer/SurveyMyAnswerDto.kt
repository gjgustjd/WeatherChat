package com.miso.misoweather.model.DTO.SurveyMyAnswer

import java.io.Serializable

data class SurveyMyAnswerDto(
    val answered: Boolean,
    val memberAnswer: String,
    val surveyId: Int
):Serializable