package com.miso.misoweather.Acitivity.getnickname

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SelectNicknameViewModel @Inject constructor(private val repository: MisoRepository2) :
    ViewModel() {

    @ActivityContext
    lateinit var context: Context

    val smallScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SMALLSCALE_REGION)
    }

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    val accessToken by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.ACCESS_TOKEN)
    }

    val socialId by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_ID)
    }

    val socialType by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SOCIAL_TYPE)
    }

    val registerResultString by lazy { MutableLiveData<String?>() }

    lateinit var loginRequestDto: LoginRequestDto
    lateinit var signUpRequestDto: SignUpRequestDto
    lateinit var defaultRegionId: String

    suspend fun registerMember(
        signUpRequestDto: SignUpRequestDto,
        socialToken: String,
        isResetedToken: Boolean,
        action: ((response: Response<GeneralResponseDto>) -> Unit)? = null
    ) {
        this.signUpRequestDto = signUpRequestDto
        val response = repository.registerMember(
            signUpRequestDto,
            socialToken
        )
        if (response.isSuccessful)
            issueMisoToken(loginRequestDto, defaultRegionId)
        else {
            if (response.errorBody()!!.source().toString()
                    .contains("UNAUTHORIZED") && !isResetedToken
            ) {
                resetAccessToken()
            } else {
                registerResultString.value = "Failed"
            }
        }

        if (action != null) {
            action(response)
        }
    }

    suspend fun getNickname(action: (response: Response<NicknameResponseDto>) -> Unit) =
        action(repository.getNickname())

    private fun resetAccessToken() {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("resetAccessToken", "로그인 실패", error)
            } else if (token != null) {
                Log.i("resetAccessToken", "로그인 성공 ${token.accessToken}")
                repository.dataStoreManager.savePreference(
                    DataStoreManager.ACCESS_TOKEN,
                    token.accessToken
                )
                viewModelScope.launch {
                    registerMember(signUpRequestDto, socialType, true)
                }
            }
        }
    }

    suspend fun issueMisoToken(
        loginRequestDto: LoginRequestDto,
        defaultRegionId: String,
        action: ((response: Response<GeneralResponseDto>) -> Unit)? = null
    ) {
        val response = repository.issueMisoToken(
            loginRequestDto,
            accessToken
        )
        if (response.isSuccessful) {
            try {
                Log.i("결과", "성공")
                val headers = response.headers()
                val serverToken = headers.get("servertoken")
                repository.dataStoreManager.apply {
                    savePreference(DataStoreManager.MISO_TOKEN, serverToken!!)
                    savePreference(DataStoreManager.DEFAULT_REGION_ID, defaultRegionId)
                }
                if (!serverToken.isNullOrBlank()) {
                    registerResultString.value = "OK"
                } else
                    registerResultString.value = "Failed"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (action != null) {
            action(response)
        }
    }
}