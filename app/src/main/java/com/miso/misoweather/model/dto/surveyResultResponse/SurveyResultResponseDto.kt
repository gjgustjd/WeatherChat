package com.miso.misoweather.model.dto.surveyResultResponse

data class SurveyResultResponseDto(
    val data: SurveyResultData,
    val message: String,
    val status: String
)