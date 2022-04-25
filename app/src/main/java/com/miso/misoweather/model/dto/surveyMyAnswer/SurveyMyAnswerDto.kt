package com.miso.misoweather.model.dto.surveyMyAnswer

import java.io.Serializable

data class SurveyMyAnswerDto(
    val answered: Boolean,
    val memberAnswer: String,
    val surveyId: Int
):Serializable