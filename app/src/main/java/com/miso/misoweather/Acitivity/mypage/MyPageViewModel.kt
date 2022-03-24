package com.miso.misoweather.Acitivity.mypage

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Acitivity.answerAnimationActivity.AnswerAnimationActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.selectRegion.RegionItem
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.Region
import com.miso.misoweather.model.DTO.RegionListResponse.RegionListResponseDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.MisoRepository
import retrofit2.Response
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MyPageViewModel(private val repository: MisoRepository) : ViewModel() {
    val unRegisterResponse: MutableLiveData<Any?> =
        MutableLiveData()

    fun unRegister(
        loginRequestDto: LoginRequestDto
    ) {
        repository.unregisterMember(
            repository.getPreference("misoToken")!!,
            loginRequestDto,
            { call, response ->
                try {
                    Log.i("결과", "성공")
                    repository.removePreference(
                        "misoToken",
                        "defaultRegionId",
                        "isSurveyed",
                        "LastSurveyedDate",
                        "bigScale",
                        "BigScaleRegion",
                        "MidScaleRegion",
                        "SmallScaleRegion",
                        "nickname",
                    )
                    repository.savePreferences()
                    unRegisterResponse.value = response
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                }
            },
            { call, response ->
                unRegisterResponse.value = response
            },
            { call, t ->
//                Log.i("결과", "실패 : $t")
            }
        )
    }

}