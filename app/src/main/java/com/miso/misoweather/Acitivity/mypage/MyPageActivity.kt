package com.miso.misoweather.Acitivity.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityMypageBinding
import com.miso.misoweather.databinding.DialogConfirmGeneralBinding

class MyPageActivity : MisoActivity() {
    lateinit var binding: ActivityMypageBinding
    lateinit var btn_back: ImageButton
    lateinit var btn_logout: Button
    lateinit var btn_unregister: Button
    lateinit var btn_version: Button
    lateinit var txt_emoji: TextView
    lateinit var txt_nickname: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
    }

    fun initializeView() {
        btn_back = binding.imgbtnBack
        btn_logout = binding.btnLogout
        btn_unregister = binding.btnUnregister
        btn_version = binding.btnVersion
        txt_emoji = binding.txtEmoji
        txt_nickname = binding.txtNickname

        txt_emoji.text = getPreference("emoji")
        txt_nickname.text = getPreference("nickname")
        btn_back.setOnClickListener()
        {
            startActivity(Intent(this, HomeActivity::class.java))
            transferToBack()
            finish()
        }
        btn_unregister.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                View.OnClickListener {},
                "정말로 계정을 삭제할까요? \uD83D\uDE22",
                "삭제"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
        btn_logout.setOnClickListener()
        {
            val dialog = GeneralConfirmDialog(
                this,
                View.OnClickListener {
                    logout()
                },
                "로그아웃 하시겠습니까? \uD83D\uDD13",
                "로그아웃"
            )
            dialog.show(supportFragmentManager, "generalConfirmDialog")
        }
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.e("", "토큰 정보 보기 실패", error)
                    }
                    else if (tokenInfo != null) {
                        Log.i("", "토큰 정보 보기 성공" +
                                "\n회원번호: ${tokenInfo.id}" +
                                "\n만료시간: ${tokenInfo.expiresIn} 초")
                    }
                }
            } else {
                Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                removePreference("accessToken", "socialId", "socialType","misoToken")
                savePreferences()
                startActivity(Intent(this, LoginActivity::class.java))
                transferToBack()
                finish()
            }
        }
    }

}