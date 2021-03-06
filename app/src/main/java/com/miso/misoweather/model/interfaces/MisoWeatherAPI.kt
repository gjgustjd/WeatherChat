package com.miso.misoweather.model.interfaces

import com.miso.misoweather.model.dto.*
import com.miso.misoweather.model.dto.commentList.CommentListResponseDto
import com.miso.misoweather.model.dto.forecast.brief.ForecastBriefResponseDto
import com.miso.misoweather.model.dto.forecast.currentAir.CurrentAirResponseDto
import com.miso.misoweather.model.dto.forecast.daily.DailyForecastResponseDto
import com.miso.misoweather.model.dto.forecast.hourly.HourlyForecastResponseDto
import com.miso.misoweather.model.dto.memberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.dto.nicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.dto.regionListResponse.RegionListResponseDto
import com.miso.misoweather.model.dto.surveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.dto.surveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.dto.surveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.dto.surveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResultResponseDto
import retrofit2.Response
import retrofit2.http.*

interface MisoWeatherAPI {
    @GET("api/member/nickname")
    suspend fun getNickname(): Response<NicknameResponseDto>

    @POST("api/member")
    suspend fun registerMember(
        @Body body: SignUpRequestDto,
        @Query("socialToken") socialToken: String
    ): Response<GeneralResponseDto>

    @GET("api/region/{bigScaleRegion}")
    suspend fun getCity(@Path("bigScaleRegion") bigScaleRegion: String): Response<RegionListResponseDto>

    @GET("api/region/{bigScaleRegion}/{midScaleRegion}")
    suspend fun getArea(
        @Path("bigScaleRegion") bigScaleRegion: String,
        @Path("midScaleRegion") midScaleRegion: String
    ): Response<RegionListResponseDto>

    @POST("api/member/token")
    suspend fun reIssueMisoToken(
        @Body body: LoginRequestDto,
        @Query("socialToken") socialToken: String
    ): Response<GeneralResponseDto>

    @HTTP(method = "DELETE", path = "api/member/", hasBody = true)
    suspend fun unregisterMember(
        @Header("serverToken") serverToken: String,
        @Body body: LoginRequestDto
    ): Response<GeneralResponseDto>

    @GET("api/member")
    suspend fun getUserInfo(@Header("serverToken") serverToken: String): Response<MemberInfoResponseDto>

    @GET("api/new-forecast/{regionId}")
    suspend fun getBriefForecast(@Path("regionId") regionId: Int): Response<ForecastBriefResponseDto>

    @GET("api/forecast/{regionId}/detail")
    suspend fun getDetailForecast(@Path("regionId") regionId: Int): Response<DailyForecastResponseDto>

    @GET("api/comment")
    suspend fun getCommentList(
        @Query("commentId") commentId: Int?,
        @Query("size") size: Int
    ): Response<CommentListResponseDto>

    @POST("api/comment")
    suspend fun addComment(
        @Header("serverToken") serverToken: String,
        @Body body: CommentRegisterRequestDto
    ): Response<GeneralResponseDto>

    @GET("api/survey/answers/{surveyId}")
    suspend fun getSurveyAnswers(@Path("surveyId") surveyId: Int): Response<SurveyAnswerResponseDto>

    @GET("api/survey")
    suspend fun getSurveyResults(@Query("shortBigScale") shortBigScale: String? = null): Response<SurveyResultResponseDto>

    @GET("api/survey/member")
    suspend fun getSurveyMyAnswers(@Header("serverToken") serverToken: String): Response<SurveyMyAnswerResponseDto>

    @POST("api/survey")
    suspend fun putSurveyMyAnser(
        @Header("serverToken") serverToken: String,
        @Body answerSurveyDto: SurveyAddMyAnswerRequestDto
    ): Response<SurveyAddMyAnswerResponseDto>

    @PUT("api/member-region-mapping/default")
    suspend fun updateRegion(
        @Header("serverToken") serverToken: String,
        @Query("regionId") regionId: Int
    ): Response<GeneralResponseDto>

    @GET("api/member/existence")
    suspend fun checkRegistered(
        @Query("socialId") socialId: String,
        @Query("socialType") socialType: String
    ): Response<GeneralResponseDto>

    @GET("/api/new-forecast/update/{regionId}")
    suspend fun loadWeatherInfo(@Path("regionId") regionId: Int): Response<GeneralResponseDto>

    @GET("/api/new-forecast/daily/{regionId}")
    suspend fun getDailyForecast(@Path("regionId") regionId: Int): Response<DailyForecastResponseDto>

    @GET("/api/new-forecast/hourly/{regionId}")
    suspend fun getHourlyForecast(@Path("regionId") regionId: Int): Response<HourlyForecastResponseDto>

    @GET("/api/new-forecast/airdust/{regionId}")
    suspend fun getCurrentAir(@Path("regionId") regionId: Int): Response<CurrentAirResponseDto>
}