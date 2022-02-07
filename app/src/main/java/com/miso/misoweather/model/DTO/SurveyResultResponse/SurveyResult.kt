package com.miso.misoweather.model.DTO.SurveyResultResponse

data class SurveyResult(
    val keyList: List<Any>,
    val surveyId: Int,
    val valueList: List<Int>
)