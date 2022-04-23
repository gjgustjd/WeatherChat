package com.miso.misoweather.model

import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MisoRepository @Inject constructor(
    private val api: MisoWeatherAPI,
    val dataStoreManager: DataStoreManager
) {

    suspend fun checkRegistered(socialId: String, socialType: String) =
        api.checkRegistered(socialId, socialType)

    suspend fun issueMisoToken(
        loginRequestDto: LoginRequestDto,
        socialType: String
    ) = api.reIssueMisoToken(loginRequestDto, socialType)

    suspend fun getNickname() = api.getNickname()

    suspend fun registerMember(
        signUpRequestDto: SignUpRequestDto,
        socialToken: String,
    ) =
        api.registerMember(signUpRequestDto, socialToken)

    suspend fun getCity(
        bigScaleRegion: String,
    ) =
        api.getCity(bigScaleRegion)

    suspend fun getArea(
        bigScaleRegion: String,
        midScaleRegion: String,
    ) =
        api.getArea(bigScaleRegion, midScaleRegion)

    suspend fun unregisterMember(
        serverToken: String,
        loginRequestDto: LoginRequestDto,
    ) =
        api.unregisterMember(serverToken, loginRequestDto)

    suspend fun getUserInfo(
        serverToken: String,
    ) =
        api.getUserInfo(serverToken)

    suspend fun getBriefForecast(
        regionId: Int,
    ) =
        api.getBriefForecast(regionId)

    suspend fun getDailyForecast(
        regionId: Int,
    ) =
        api.getDailyForecast(regionId)

    suspend fun getHourlyForecast(
        regionId: Int,
    ) =
        api.getHourlyForecast(regionId)

    suspend fun getCurrentAir(
        regionId: Int,
    ) =
        api.getCurrentAir(regionId)

    suspend fun getCommentList(
        commentId: Int?,
        size: Int,
    ) =
        api.getCommentList(commentId, size)

    suspend fun addComment(
        serverToken: String,
        commentRegisterRequestDto: CommentRegisterRequestDto,
    ) =
        api.addComment(serverToken, commentRegisterRequestDto)

    suspend fun getSurveyAnswers(
        surveyId: Int
    ) =
        api.getSurveyAnswers(surveyId)

    suspend fun getSurveyResults(
        shortBigScale: String?,
    ) =
        api.getSurveyResults(shortBigScale)

    suspend fun getSurveyMyAnswers(
        serverToken: String
    ) =
        api.getSurveyMyAnswers(serverToken)

    suspend fun putSurveyMyAnswer(
        serverToken: String,
        answerSurveyDto: SurveyAddMyAnswerRequestDto
    ) =
        api.putSurveyMyAnser(serverToken, answerSurveyDto)

    suspend fun updateRegion(
        serverToken: String,
        regionId: Int,
    ) =
        api.updateRegion(serverToken, regionId)

    suspend fun loadWeatherInfo(
        regionId: Int,
    ) =
        api.loadWeatherInfo(regionId)
}
