package com.miso.misoweather.model

import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.Detail.ForecastDetailResponseDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

object MisoRepository {
    fun checkRegistered(
        socialId: String,
        socialType: String,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callCheckRegistered = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .checkRegistered(socialId, socialType)

        TransportManager.requestApi(callCheckRegistered,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun issueMisoToken(
        loginRequestDto: LoginRequestDto,
        socialType: String,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {

        val callIssueMisoToken = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .reIssueMisoToken(loginRequestDto, socialType)

        TransportManager.requestApi(callIssueMisoToken,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getNickname(
        onSuccessful: (
            Call<NicknameResponseDto>,
            Response<NicknameResponseDto>
        ) -> Unit,
        onFail: (
            Call<NicknameResponseDto>,
            Response<NicknameResponseDto>
        ) -> Unit,
        onError: (
            Call<NicknameResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetNickname = TransportManager.getRetrofitApiObject<NicknameResponseDto>()
            .getNickname()

        TransportManager.requestApi(callGetNickname,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })

    }

    fun registerMember(
        signUpRequestDto: SignUpRequestDto,
        socialToken: String,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callRegisterMember = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .registerMember(signUpRequestDto, socialToken)

        TransportManager.requestApi(callRegisterMember,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })

    }

    fun getCity(
        bigScaleRegion: String,
        onSuccessful: (
            Call<RegionListResponseDto>,
            Response<RegionListResponseDto>
        ) -> Unit,
        onFail: (
            Call<RegionListResponseDto>,
            Response<RegionListResponseDto>
        ) -> Unit,
        onError: (
            Call<RegionListResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetCity = TransportManager.getRetrofitApiObject<RegionListResponseDto>()
            .getCity(bigScaleRegion)

        TransportManager.requestApi(callGetCity,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })

    }

    fun getArea(
        bigScaleRegion: String,
        midScaleRegion: String,
        onSuccessful: (
            Call<RegionListResponseDto>,
            Response<RegionListResponseDto>
        ) -> Unit,
        onFail: (
            Call<RegionListResponseDto>,
            Response<RegionListResponseDto>
        ) -> Unit,
        onError: (
            Call<RegionListResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetArea = TransportManager.getRetrofitApiObject<RegionListResponseDto>()
            .getArea(bigScaleRegion, midScaleRegion)

        TransportManager.requestApi(callGetArea,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun unregisterMember(
        serverToken: String,
        loginRequestDto: LoginRequestDto,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callUnregisterMember = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .unregisterMember(serverToken, loginRequestDto)

        TransportManager.requestApi(callUnregisterMember,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getUserInfo(
        serverToken: String,
        onSuccessful: (
            Call<MemberInfoResponseDto>,
            Response<MemberInfoResponseDto>
        ) -> Unit,
        onFail: (
            Call<MemberInfoResponseDto>,
            Response<MemberInfoResponseDto>
        ) -> Unit,
        onError: (
            Call<MemberInfoResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetUserInfo = TransportManager.getRetrofitApiObject<MemberInfoResponseDto>()
            .getUserInfo(serverToken)

        TransportManager.requestApi(callGetUserInfo,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getBriefForecast(
        regionId: Int,
        onSuccessful: (
            Call<ForecastBriefResponseDto>,
            Response<ForecastBriefResponseDto>
        ) -> Unit,
        onFail: (
            Call<ForecastBriefResponseDto>,
            Response<ForecastBriefResponseDto>
        ) -> Unit,
        onError: (
            Call<ForecastBriefResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetBriefForecast = TransportManager.getRetrofitApiObject<ForecastBriefResponseDto>()
            .getBriefForecast(regionId)

        TransportManager.requestApi(callGetBriefForecast,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getDetailForecast(
        regionId: Int,
        onSuccessful: (
            Call<ForecastDetailResponseDto>,
            Response<ForecastDetailResponseDto>
        ) -> Unit,
        onFail: (
            Call<ForecastDetailResponseDto>,
            Response<ForecastDetailResponseDto>
        ) -> Unit,
        onError: (
            Call<ForecastDetailResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetDetailForecast =
            TransportManager.getRetrofitApiObject<ForecastDetailResponseDto>()
                .getDetailForecast(regionId)

        TransportManager.requestApi(callGetDetailForecast,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getCommentList(
        commentId: Int?,
        size: Int,
        onSuccessful: (
            Call<CommentListResponseDto>,
            Response<CommentListResponseDto>
        ) -> Unit,
        onFail: (
            Call<CommentListResponseDto>,
            Response<CommentListResponseDto>
        ) -> Unit,
        onError: (
            Call<CommentListResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetCommentList = TransportManager.getRetrofitApiObject<CommentListResponseDto>()
            .getCommentList(commentId, size)

        TransportManager.requestApi(callGetCommentList,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun addComment(
        serverToken: String,
        commentRegisterRequestDto: CommentRegisterRequestDto,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callAddComment = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .addComment(serverToken, commentRegisterRequestDto)

        TransportManager.requestApi(callAddComment,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getSurveyAnswers(
        surveyId: Int,
        onSuccessful: (
            Call<SurveyAnswerResponseDto>,
            Response<SurveyAnswerResponseDto>
        ) -> Unit,
        onFail: (
            Call<SurveyAnswerResponseDto>,
            Response<SurveyAnswerResponseDto>
        ) -> Unit,
        onError: (
            Call<SurveyAnswerResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetSurveyAnswers = TransportManager.getRetrofitApiObject<SurveyAnswerResponseDto>()
            .getSurveyAnswers(surveyId)

        TransportManager.requestApi(callGetSurveyAnswers,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getSurveyResults(
        shortBigScale: String,
        onSuccessful: (
            Call<SurveyResultResponseDto>,
            Response<SurveyResultResponseDto>
        ) -> Unit,
        onFail: (
            Call<SurveyResultResponseDto>,
            Response<SurveyResultResponseDto>
        ) -> Unit,
        onError: (
            Call<SurveyResultResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetSurveyResults = TransportManager.getRetrofitApiObject<SurveyResultResponseDto>()
            .getSurveyResults(shortBigScale)

        TransportManager.requestApi(callGetSurveyResults,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun getSurveyMyAnswers(
        serverToken: String,
        onSuccessful: (
            Call<SurveyMyAnswerResponseDto>,
            Response<SurveyMyAnswerResponseDto>
        ) -> Unit,
        onFail: (
            Call<SurveyMyAnswerResponseDto>,
            Response<SurveyMyAnswerResponseDto>
        ) -> Unit,
        onError: (
            Call<SurveyMyAnswerResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callGetSurveyMyAnswers =
            TransportManager.getRetrofitApiObject<SurveyMyAnswerResponseDto>()
                .getSurveyMyAnswers(serverToken)

        TransportManager.requestApi(callGetSurveyMyAnswers,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun putSurveyMyAnswer(
        serverToken: String,
        answerSurveyDto: SurveyAddMyAnswerRequestDto,
        onSuccessful: (
            Call<SurveyAddMyAnswerResponseDto>,
            Response<SurveyAddMyAnswerResponseDto>
        ) -> Unit,
        onFail: (
            Call<SurveyAddMyAnswerResponseDto>,
            Response<SurveyAddMyAnswerResponseDto>
        ) -> Unit,
        onError: (
            Call<SurveyAddMyAnswerResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callPutSurveyMyAnswer =
            TransportManager.getRetrofitApiObject<SurveyAddMyAnswerResponseDto>()
                .putSurveyMyAnser(serverToken, answerSurveyDto)

        TransportManager.requestApi(callPutSurveyMyAnswer,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }

    fun updateRegion(
        serverToken: String,
        regionId: Int,
        onSuccessful: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onFail: (
            Call<GeneralResponseDto>,
            Response<GeneralResponseDto>
        ) -> Unit,
        onError: (
            Call<GeneralResponseDto>,
            Throwable
        ) -> Unit?,
    ) {
        val callUpdateRegion = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .updateRegion(serverToken, regionId)

        TransportManager.requestApi(callUpdateRegion,
            { call, response ->
                if (response.isSuccessful)
                    onSuccessful(call, response)
                else
                    onFail(call, response)
            },
            { call, throwable ->
                onError(call, throwable)
            })
    }
}