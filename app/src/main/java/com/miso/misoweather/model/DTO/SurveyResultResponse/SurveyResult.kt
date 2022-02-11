package com.miso.misoweather.model.DTO.SurveyResultResponse

import java.io.Serializable

data class SurveyResult(
    val keyList: List<Any>,
    val surveyId: Int,
    val valueList: List<Int>
):Serializable