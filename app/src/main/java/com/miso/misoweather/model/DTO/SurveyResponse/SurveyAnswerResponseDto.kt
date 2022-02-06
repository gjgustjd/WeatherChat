package com.miso.misoweather.model.DTO.SurveyResponse

data class SurveyAnswerResponseDto(
    val data: SurveyResponseData,
    val message: String,
    val status: String
)