package com.miso.misoweather.Module

import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerData
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultData
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
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