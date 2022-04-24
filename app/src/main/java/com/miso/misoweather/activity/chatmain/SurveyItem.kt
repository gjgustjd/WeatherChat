package com.miso.misoweather.activity.chatmain

import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import java.io.Serializable

data class SurveyItem(
    val surveyId: Int,
    val surveyQuestion: String,
    var surveyMyAnswer: SurveyMyAnswerDto,
    var surveyAnswers: List<SurveyAnswerDto>,
    var surveyResult: SurveyResult
) : Serializable