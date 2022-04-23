package com.miso.misoweather.Acitivity.getnickname

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectNickNameActivity : MisoActivity() {
    private val viewModel: SelectNicknameViewModel by viewModels()
    private lateinit var binding: ActivitySelectNicknameBinding
    private lateinit var txt_get_new_nick: TextView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private var nickName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    private fun initializeViews() {
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        txt_get_new_nick.setOnClickListener {
            getNickname()
        }
        btn_back.setOnClickListener()
        {
            doBack()
        }
        btn_next.setOnClickListener()
        {
            registerMember()
        }

        getNickname()
        viewModel.defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
    }

    override fun doBack() {
        val intent: Intent?
        if (viewModel.smallScaleRegion.isBlank())
            intent = Intent(this, SelectTownActivity::class.java)
        else
            intent = Intent(this, SelectAreaActivity::class.java)

        startActivity(intent)
        transferToBack()
        finish()
    }

    private fun registerMember() {
        fun inCaseFailedRegister() {
            Toast.makeText(this@SelectNickNameActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG)
                .show()
            goToLoginActivity()
        }

        viewModel.loginRequestDto = makeLoginRequestDto()
        lifecycleScope.launch {
            viewModel.registerMember(
                getSignUpInfo(),
                viewModel.accessToken,
                false
            )
            {
                if (it.isSuccessful) {
                    startActivity(Intent(this@SelectNickNameActivity, HomeActivity::class.java))
                } else {
                    inCaseFailedRegister()
                }
            }
        }
    }

    private fun goToLoginActivity() {
        startActivity(Intent(this@SelectNickNameActivity, LoginActivity::class.java))
        transferToBack()
        finish()
    }

    private fun makeLoginRequestDto(): LoginRequestDto {
        return LoginRequestDto(
            viewModel.socialId,
            viewModel.socialType
        )
    }

    private fun getSignUpInfo(): SignUpRequestDto =
        SignUpRequestDto().apply {
            defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
            emoji = binding.txtEmoji.text.toString()
            nickname = this@SelectNickNameActivity.nickName
            socialId = viewModel.socialId
            socialType = viewModel.socialType
        }

    private fun getNickname() {
        lifecycleScope.launch {
            viewModel.getNickname()
            {
                if (it.isSuccessful) {
                    Log.i("결과", "성공")
                    Log.i("결과", "닉네임 : ${it.body()?.data?.nickname}")
                    val nicknameResponseDto = it.body()!!
                    nickName = nicknameResponseDto.data.nickname
                    binding.txtGreetingBold.text =
                        "${getBigShortScale(viewModel.bigScaleRegion)}의 ${nickName}님!"
                    binding.txtEmoji.text = nicknameResponseDto.data.emoji
                } else {
                    Log.i("결과", "실패")
                    Toast.makeText(
                        this@SelectNickNameActivity,
                        "닉네임 받기에 실패하였습니다.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}