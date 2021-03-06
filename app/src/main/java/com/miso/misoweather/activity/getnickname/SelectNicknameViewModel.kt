package com.miso.misoweather.activity.getnickname

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.R
import com.miso.misoweather.model.dto.GeneralResponseDto
import com.miso.misoweather.model.dto.LoginRequestDto
import com.miso.misoweather.model.dto.nicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.dto.SignUpRequestDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class SelectNicknameViewModel @Inject constructor(
    application: Application,
    private val repository: MisoRepository
) :
    AndroidViewModel(application) {
    val context:Context = application

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
    val emoji by lazy { MutableLiveData("") }
    val nickname by lazy { MutableLiveData("") }
    val greetingText by lazy { MutableLiveData("") }

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

    suspend fun getNickname(action: (response: Response<NicknameResponseDto>) -> Unit) {
        val response = repository.getNickname()
        emoji.value = response.body()!!.data.emoji
        nickname.value = response.body()!!.data.nickname
        greetingText.value =
            "${getBigShortScale(bigScaleRegion)}??? ${nickname.value}???!"
        action(response)
    }

    private fun resetAccessToken() {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                Log.e("resetAccessToken", "????????? ??????", error)
            } else if (token != null) {
                Log.i("resetAccessToken", "????????? ?????? ${token.accessToken}")
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
                Log.i("??????", "??????")
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

        action?.let {
            it(response)
        }
    }

    private fun getBigShortScale(bigScale: String): String {
        return try {
            val regionList = context.resources.getStringArray(R.array.regions_full)
            val index = regionList.indexOf(bigScale)
            val regionSmallList = context.resources.getStringArray(R.array.regions)

            regionSmallList[index]
        } catch (e: Exception) {
            Log.e("getBigShortScale", e.toString())
            ""
        }
    }
}