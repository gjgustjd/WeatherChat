package com.miso.misoweather.Acitivity.chatmain

import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult

class SurveyItem( val surveyId:Int,
                  var surveyMyAnswer:List<SurveyMyAnswerDto>,
                  var surveyAnswers:SurveyAnswerDto,
                  var surveyResult:SurveyResult){}