package com.miso.misoweather.getnickname

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.model.KakaoSdkError
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.model.DTO.Data
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.NicknameResponseDto
import com.miso.misoweather.model.DTO.SignUpRequestDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.selectArea.SelectAreaActivity
import com.miso.misoweather.selectTown.SelectTownActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.sign

class SelectNickNameActivity : MisoActivity() {
    lateinit var binding: ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick: TextView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    var nicknameResponseDto: NicknameResponseDto = NicknameResponseDto(Data("", ""), "", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()

    }

    fun initializeViews() {
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        btn_back = binding.imgbtnBack
        btn_next = binding.btnAction
        txt_get_new_nick.setOnClickListener() {
            getNickname()
        }
        btn_back.setOnClickListener()
        {
            startActivity(Intent(this, SelectAreaActivity::class.java))
            transferToBack()
            finish()
        }
        btn_next.setOnClickListener()
        {
            registerMember()
        }
        getNickname()
    }

    fun registerMember(){
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        var signUpRequestDto = SignUpRequestDto()
        signUpRequestDto.defaultRegionId=prefs.getString("defaultRegionId","")!!
        signUpRequestDto.emoji=binding.txtEmoji.text.toString()
        signUpRequestDto.socialId=prefs.getString("socialId","")!!
        signUpRequestDto.socialType=prefs.getString("socialType","")!!

        val callRegisterMember = api.registerMember(signUpRequestDto,prefs.getString("access_token","")!!)

        callRegisterMember.enqueue(object : Callback<GeneralResponseDto> {
            override fun onResponse(
                call: Call<GeneralResponseDto>,
                response: Response<GeneralResponseDto>
            ) {
                Log.i("결과", "성공")
            }

            override fun onFailure(call: Call<GeneralResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
    fun getNickname() {
        val retrofit = Retrofit.Builder()
            .baseUrl(MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callGetNickName = api.getNickname()

        callGetNickName.enqueue(object : Callback<NicknameResponseDto> {
            override fun onResponse(
                call: Call<NicknameResponseDto>,
                response: Response<NicknameResponseDto>
            ) {
                Log.i("결과", "성공")
                Log.i("결과", "닉네임 : ${response.body()?.data?.nickname}")
                nicknameResponseDto = response.body()!!
                binding.txtGreetingBold.text = "${nicknameResponseDto.data.nickname}님!"
                binding.txtEmoji.text = "${nicknameResponseDto.data.emoji}"
            }

            override fun onFailure(call: Call<NicknameResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }
}