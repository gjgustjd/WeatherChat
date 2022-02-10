package com.miso.misoweather.model.DTO.SurveyMyAnswer

data class SurveyMyAnswerDto(
    val answered: Boolean,
    val memberAnswer: String,
    val surveyId: Int
)