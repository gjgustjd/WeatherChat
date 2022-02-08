package com.miso.misoweather.Acitivity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityLoginBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.TransportManager
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class LoginActivity : MisoActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clBtnKakaoLogin.setOnClickListener {
            if (!AuthApiClient.instance.hasToken() ||
                getPreference("socialId").equals("") ||
                getPreference("socialType").equals("")
            )
                kakaoLogin()
            else {
                issueMisoToken()

                if (getPreference("misoToken").equals("")) {
                    startActivity(Intent(this, SelectRegionActivity::class.java))
                    transferToNext()
                } else
                    startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }

    fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
                if (error != null) {
                    Log.e("miso", "로그인 실패", error)
                } else if (token != null) {
                    Log.i("miso", "로그인 성공 ${token.accessToken}")
                    try {
                        addPreferencePair("accessToken", token.accessToken)
                        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                            if (error != null)
                                Log.i("token", "토큰 정보 보기 실패", error)
                            else if (tokenInfo != null) {
                                Log.i(
                                    "token", "토큰 정보 보기 성공" +
                                            "\n회원번호:${tokenInfo.id}"
                                )
                                addPreferencePair("socialId", tokenInfo.id.toString())
                                addPreferencePair("socialType", "kakao")
                                savePreferences()
                                issueMisoToken()

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        savePreferences()
                    }
                }
            }
        }
    }

    fun startRegionActivity() {
        startActivity(Intent(this, SelectRegionActivity::class.java))
        transferToNext()
        finish()
    }

    fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun issueMisoToken() {
        val callReIssueMisoToken = TransportManager.getRetrofitApiObject<GeneralResponseDto>()
            .reIssueMisoToken(makeLoginRequestDto(), getPreference("accessToken")!!)

        TransportManager.requestApi(callReIssueMisoToken, { call, response ->
            try {
                Log.i("결과", "성공")
                var headers = response.headers()
                var serverToken: String? = headers.get("servertoken")!!
                addPreferencePair("misoToken", serverToken!!)
                savePreferences()
                startHomeActivity()
            } catch (e: Exception) {
                e.printStackTrace()
                addPreferencePair("misoToken", "")
                savePreferences()
                startRegionActivity()
            }
        }, { call, t ->
            Log.i("결과", "실패 : $t")
            addPreferencePair("misoToken", "")
            startRegionActivity()
        })
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        var loginRequestDto = LoginRequestDto(
            getPreference("socialId"),
            getPreference("socialType")
        )
        return loginRequestDto
    }
}