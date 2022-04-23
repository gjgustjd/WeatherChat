package com.miso.misoweather.Acitivity.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(private val repository: MisoRepository2) : ViewModel() {
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

    val nickname by lazy {
        repository.dataStoreManager.getPreferenceAsFlow(DataStoreManager.NICKNAME).asLiveData()
    }

    val socialId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_ID)
    }
    val socialType by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_TYPE)
    }

    suspend fun unRegister(
        loginRequestDto: LoginRequestDto,
        action: (response: Response<GeneralResponseDto>) -> Unit
    ) {
        val response = repository.unregisterMember(
            misoToken,
            loginRequestDto
        )
        if (response.isSuccessful
        ) {
            try {
                Log.i("결과", "성공")
                repository.dataStoreManager.removePreferences(
                    DataStoreManager.MISO_TOKEN,
                    DataStoreManager.DEFAULT_REGION_ID,
                    DataStoreManager.IS_SURVEYED,
                    DataStoreManager.LAST_SURVEYED_DATE,
                    DataStoreManager.BIGSCALE_REGION,
                    DataStoreManager.MIDSCALE_REGION,
                    DataStoreManager.SMALLSCALE_REGION,
                    DataStoreManager.NICKNAME
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        action(response)
    }
}