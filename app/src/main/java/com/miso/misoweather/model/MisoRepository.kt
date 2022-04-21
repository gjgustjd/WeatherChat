package com.miso.misoweather.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.Preferences
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.Forecast.CurrentAir.CurrentAirResponseDto
import com.miso.misoweather.model.DTO.Forecast.Daily.DailyForecastResponseDto
import com.miso.misoweather.model.DTO.Forecast.Hourly.HourlyForecastResponseDto
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
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MisoRepository {
    var prefs: SharedPreferences
    var pairList: ArrayList<Pair<String, String>>
    var context: Context

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    constructor(@ApplicationContext aContext: Context) {
        context = aContext
        prefs = context.getSharedPreferences("misoweather", Context.MODE_PRIVATE)
        pairList = ArrayList()
    }

    fun addPreferencePair(first: String, second: String) {
        val pair = Pair(first, second)
        pairList.add(pair)
    }

    fun removePreference(pref: String) {
        val pair = Pair(pref, "")
        pairList.add(pair)
    }

    fun removePreference(vararg pref: String) {
        for (element in pref) {
            val pair = Pair(element, "")
            pairList.add(pair)
        }
    }

    fun getPreference(pref: String): String? {
        return prefs!!.getString(pref, "")
    }

    fun savePreferences() {
        var edit = prefs!!.edit()
        for (i in 0..pairList.size - 1) {
            val pair = pairList.get(i)
            edit.putString(pair.first, pair.second)
        }
        edit.apply()
        pairList.clear()
    }

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
        ) -> Unit,
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
        ) -> Unit,
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
        ) -> Unit,
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
        ) -> Unit,
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

    fun getDailyForecast(
        regionId: Int,
        onSuccessful: (
            Call<DailyForecastResponseDto>,
            Response<DailyForecastResponseDto>
        ) -> Unit,
        onFail: (
            Call<DailyForecastResponseDto>,
            Response<DailyForecastResponseDto>
        ) -> Unit,
        onError: (
            Call<DailyForecastResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetDetailForecast =
            TransportManager.getRetrofitApiObject<DailyForecastResponseDto>()
                .getDailyForecast(regionId)

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

    fun getHourlyForecast(
        regionId: Int,
        onSuccessful: (
            Call<HourlyForecastResponseDto>,
            Response<HourlyForecastResponseDto>
        ) -> Unit,
        onFail: (
            Call<HourlyForecastResponseDto>,
            Response<HourlyForecastResponseDto>
        ) -> Unit,
        onError: (
            Call<HourlyForecastResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetHourlyForecast =
            TransportManager.getRetrofitApiObject<HourlyForecastResponseDto>()
                .getHourlyForecast(regionId)

        TransportManager.requestApi(callGetHourlyForecast,
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

    fun getCurrentAir(
        regionId: Int,
        onSuccessful: (
            Call<CurrentAirResponseDto>,
            Response<CurrentAirResponseDto>
        ) -> Unit,
        onFail: (
            Call<CurrentAirResponseDto>,
            Response<CurrentAirResponseDto>
        ) -> Unit,
        onError: (
            Call<CurrentAirResponseDto>,
            Throwable
        ) -> Unit,
    ) {
        val callGetCurrentAir = TransportManager.getRetrofitApiObject<CurrentAirResponseDto>()
            .getCurrentAir(regionId)

        TransportManager.requestApi(callGetCurrentAir,
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
        shortBigScale: String?,
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
        ) -> Unit,
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

    fun loadWeatherInfo(
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
        ) -> Unit,
    ) {
        val callLoadWeatherInfo = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .loadWeatherInfo(regionId)

        TransportManager.requestApi(callLoadWeatherInfo,
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
