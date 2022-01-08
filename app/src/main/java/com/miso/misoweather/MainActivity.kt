package com.miso.misoweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clBtnKakaoLogin.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@MainActivity)) {
                UserApiClient.instance.loginWithKakaoTalk(this@MainActivity) { token, error ->
                    if (error != null) {
                        Log.e("miso", "로그인 실패", error)
                    } else if (token != null) {
                        Log.i("miso", "로그인 성공 ${token.accessToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this@MainActivity) { token, error ->
                    if (error != null) {
                        Log.e("miso", "로그인 실패", error)
                    } else if (token != null) {
                        Log.i("miso", "로그인 성공 ${token.accessToken}")
                    }
                }
            }
        }
    }
}