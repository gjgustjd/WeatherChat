package com.miso.misoweather.model.dto.surveyResponse

data class SurveyAnswerResponseDto(
    val data: SurveyResponseData,
    val message: String,
    val status: String
)