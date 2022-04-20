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
import androidx.annotation.RequiresApi
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.selectTown.SelectTownActivity
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class SelectNickNameActivity : MisoActivity() {
    @Inject
    lateinit var viewModel: SelectNicknameViewModel
    private lateinit var binding: ActivitySelectNicknameBinding
    private lateinit var txt_get_new_nick: TextView
    private lateinit var btn_back: ImageButton
    private lateinit var btn_next: Button
    private var nickName: String = ""
    private var accessToken: String = ""
    private var misoToken: String = ""
    private var smallScaleRegion: String = ""
    private var midScaleRegion: String = ""
    private var bigScaleRegion: String = ""
    private var socialId: String = ""
    private var socialType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        initializeProperties()
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
    }

    private fun initializeProperties() {
        viewModel.setupAccessToken()
        viewModel.accessToken.observe(this) {
            accessToken = it!!
        }
        viewModel.setupMisoToken()
        viewModel.misoToken.observe(this) {
            misoToken = it!!
        }
        viewModel.setupBigScaleRegion()
        viewModel.bigScaleRegion.observe(this) {
            bigScaleRegion = it!!
        }
        viewModel.setupMiddleScaleRegion()
        viewModel.middleScaleRegion.observe(this) {
            midScaleRegion = it!!
        }
        viewModel.setupSmallScaleRegion()
        viewModel.smallScaleRegion.observe(this) {
            smallScaleRegion = it!!
        }
        viewModel.setupSocialId()
        viewModel.socialId.observe(this) {
            socialId = it!!
        }
        viewModel.setupSocialType()
        viewModel.socialType.observe(this) {
            socialType = it!!
        }
        viewModel.defaultRegionId = intent.getStringExtra("RegionId")!!
    }

    override fun doBack() {
        val intent: Intent?
        if (smallScaleRegion.isBlank())
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
        viewModel.registerMember(
            getSignUpInfo(),
            accessToken,
            false
        )
        viewModel.registerResultString.observe(this) {
            if (it.equals("OK")) {
                val intent = Intent(this@SelectNickNameActivity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                inCaseFailedRegister()
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
            socialId,
            socialType
        )
    }

    private fun getSignUpInfo(): SignUpRequestDto {
        return SignUpRequestDto().apply {
            defaultRegionId = intent.getStringExtra("RegionId")!!.toString()
            emoji = binding.txtEmoji.text.toString()
            nickname = nickName
            socialId = socialId
            socialType = socialType
        }
    }

    private fun getNickname() {
        viewModel.getNickname()
        viewModel.nicknameResponseDto.observe(this) {
            val responseDto = it as Response<NicknameResponseDto>
            if (it == null) {
                Toast.makeText(
                    this@SelectNickNameActivity,
                    "닉네임 받기에 실패하였습니다.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (it.isSuccessful) {
                    Log.i("결과", "성공")
                    Log.i("결과", "닉네임 : ${responseDto.body()?.data?.nickname}")
                    val nicknameResponseDto = responseDto.body()!!
                    nickName = nicknameResponseDto.data.nickname
                    binding.txtGreetingBold.text =
                        "${getBigShortScale(bigScaleRegion)}의 ${nickName}님!"
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