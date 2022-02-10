package com.miso.misoweather.Acitivity.chatmain

import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerData
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult

class SurveyItem(val surveyId:Int,
                 val surveyQuestion:String,
                 var surveyMyAnswer: SurveyMyAnswerDto,
                 var surveyAnswers: List<SurveyAnswerDto>,
                 var surveyResult:SurveyResult){}