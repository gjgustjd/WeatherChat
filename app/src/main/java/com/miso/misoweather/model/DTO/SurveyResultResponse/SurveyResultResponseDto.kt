package com.miso.misoweather.model.DTO.SurveyResultResponse

data class SurveyResultResponseDto(
    val data: SurveyResultData,
    val message: String,
    val status: String
)