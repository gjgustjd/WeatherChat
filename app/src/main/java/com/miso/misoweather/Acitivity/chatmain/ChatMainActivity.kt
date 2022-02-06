package com.miso.misoweather.Acitivity.chatmain

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameData
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.databinding.ActivityChatMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class ChatMainActivity : MisoActivity() {
    lateinit var binding: ActivityChatMainBinding
    lateinit var txt_get_new_nick: TextView
    lateinit var btn_back: ImageButton
    lateinit var btn_next: Button
    lateinit var previousActivity: String
    lateinit var goToPreviousActivity: () -> Unit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        previousActivity = intent.getStringExtra("previousActivity")!!
        when (previousActivity) {
            "Weather" -> goToPreviousActivity =
                { startActivity(Intent(this, WeatherDetailActivity::class.java)) }
            else -> goToPreviousActivity =
                { startActivity(Intent(this, HomeActivity::class.java)) }
        }
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            goToPreviousActivity()
            transferToBack()
            finish()
        }
    }

    fun registerMember() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(MISOWEATHER_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(MisoWeatherAPI::class.java)
//
//        val callRegisterMember = api.registerMember(getSignUpInfo(),getPreference("accessToken")!!)
//        callRegisterMember.enqueue(object : Callback<GeneralResponseDto> {
//            override fun onResponse(
//                call: Call<GeneralResponseDto>,
//                response: Response<GeneralResponseDto>
//            ) {
//                Log.i("결과", "성공")
//                issueMisoToken()
//            }
//
//            override fun onFailure(call: Call<GeneralResponseDto>, t: Throwable) {
//                Log.i("결과", "실패 : $t")
//            }
//        })
    }
}