package com.miso.misoweather.model.DTO.SurveyResponse

import java.io.Serializable

data class SurveyAnswerDto(
    val answer: String,
    val answerDescription: String,
    val answerId: Int,
    val surveyId: Int,
    val surveyDescription: String,
    val surveyTitle: String,
) : Serializable