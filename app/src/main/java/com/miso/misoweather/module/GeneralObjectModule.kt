package com.miso.misoweather.module

import com.miso.misoweather.model.dto.surveyMyAnswer.SurveyMyAnswerData
import com.miso.misoweather.model.dto.surveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResultData
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResultResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
class GeneralObjectModule {

    @Provides
    fun getSurveyResultResponseDto(): SurveyResultResponseDto {
        return SurveyResultResponseDto(
            SurveyResultData(
                listOf()
            ), "", ""
        )
    }

    @Provides
    fun getSurveyMyAnswerResponseDto(): SurveyMyAnswerResponseDto {
        return SurveyMyAnswerResponseDto(SurveyMyAnswerData(listOf()), "", "")
    }

}