package com.miso.misoweather.Acitivity.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityLoginBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import java.lang.Exception

class LoginActivity : MisoActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clBtnKakaoLogin.setOnClickListener {
            if (getPreference("accessToken").equals(""))
                kakaoLogin()
            else {
                lateinit var intent:Intent
                if (getPreference("misoToken").equals(""))
                    intent = Intent(this, SelectRegionActivity::class.java)
                else
                    intent = Intent(this, HomeActivity::class.java)

                startActivity(intent)
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
                                startActivity(Intent(this, SelectRegionActivity::class.java))
                                transferToNext()
                            }
                        }
                    }catch (e:Exception)
                    {
                        e.printStackTrace()
                        savePreferences()
                    }
                }
            }
        }
    }
}