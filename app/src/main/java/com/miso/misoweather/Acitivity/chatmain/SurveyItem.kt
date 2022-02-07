package com.miso.misoweather.Acitivity.chatmain

import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult

class SurveyItem(val surveyId:Int,
                 val surveyQuestion:String,
                 var surveyMyAnswer:List<SurveyMyAnswerResponseDto>,
                 var surveyAnswers:SurveyAnswerDto,
                 var surveyResult:SurveyResult){}