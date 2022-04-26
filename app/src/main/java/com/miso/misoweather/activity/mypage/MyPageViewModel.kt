package com.miso.misoweather.activity.mypage

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miso.misoweather.R
import com.miso.misoweather.SocketApplication
import com.miso.misoweather.model.dto.GeneralResponseDto
import com.miso.misoweather.model.dto.LoginRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class MyPageViewModel @Inject constructor(
    application: Application,
    private val repository: MisoRepository
) : AndroidViewModel(application) {
    val misoToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)
    }
    val emoji by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.EMOJI)
    }
    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val nickname by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.NICKNAME)
    }

    val socialId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_ID)
    }
    val socialType by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_TYPE)
    }

    val fullNickName by lazy {
        flow {
            emit(
                "${getBigShortScale(bigScaleRegion)}의 ${nickname}"
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = "${getBigShortScale(bigScaleRegion)}의 ${nickname}"
        )
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

    fun getBigShortScale(bigScale: String): String {
        try {
            val resources = getApplication<SocketApplication>().resources
            val regionList = resources.getStringArray(R.array.regions_full)
            val index = regionList.indexOf(bigScale)
            val regionSmallList = resources.getStringArray(R.array.regions)

            return regionSmallList.get(index)
        } catch (e: Exception) {
            return ""
        }
    }
}