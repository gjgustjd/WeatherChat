package com.miso.misoweather.Acitivity.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.lang.Exception
import javax.inject.Inject

@ActivityRetainedScoped
class MyPageViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MisoRepository

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