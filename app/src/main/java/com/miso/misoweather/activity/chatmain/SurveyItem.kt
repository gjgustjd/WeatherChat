package com.miso.misoweather.activity.chatmain

import com.miso.misoweather.model.dto.surveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.dto.surveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResult
import java.io.Serializable

data class SurveyItem(
    val surveyId: Int,
    val surveyQuestion: String,
    var surveyMyAnswer: SurveyMyAnswerDto,
    var surveyAnswers: List<SurveyAnswerDto>,
    var surveyResult: SurveyResult
) : Serializable