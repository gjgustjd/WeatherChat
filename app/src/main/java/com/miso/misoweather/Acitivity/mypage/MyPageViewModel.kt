package com.miso.misoweather.Acitivity.mypage

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

    @MutableNullableAnyLiveData
    @Inject
    lateinit var unRegisterResponse: MutableLiveData<Any?>

    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }
    val emoji by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.EMOJI).asLiveData()
    }
    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.BIGSCALE_REGION)
            .asLiveData()
    }

    val nickname by lazy{
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.NICKNAME).asLiveData()}

    val socialId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_ID)
    }
    val socialType by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_TYPE)
    }

    fun unRegister(loginRequestDto: LoginRequestDto) {
        repository.unregisterMember(
            misoToken,
            loginRequestDto,
            { call, response ->
                try {
                    Log.i("결과", "성공")
                    repository.dataStoreManager.removePreferences(
                        DataStoreManager.MISO_TOKEN,
                        DataStoreManager.DEFAULT_REGION_ID,
                        DataStoreManager.IS_SURVEYED,
                        DataStoreManager.LAST_SURVEYED_DATE,
//                            "bigScale",
                        DataStoreManager.BIGSCALE_REGION,
                        DataStoreManager.MIDSCALE_REGION,
                        DataStoreManager.SMALLSCALE_REGION,
                        DataStoreManager.NICKNAME
                    )
                    unRegisterResponse.value = response
                } catch (e: Exception) {
                    e.printStackTrace()
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