package com.miso.misoweather.getnickname

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.model.DTO.NicknameResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SelectNickNameActivity :AppCompatActivity(){
    lateinit var binding:ActivitySelectNicknameBinding
    lateinit var txt_get_new_nick:TextView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState);
        binding = ActivitySelectNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()

    }
    fun initializeViews()
    {
        txt_get_new_nick = binding.txtGetNewNickname
        txt_get_new_nick.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        getNickname()
    }

    fun getNickname()
    {
        val BASE_URL_API = "http://3.35.55.100/"

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callGetNickName = api.getNickname()

        callGetNickName.enqueue(object : Callback<NicknameResponseDto>{
            override fun onResponse(
                call: Call<NicknameResponseDto>,
                response: Response<NicknameResponseDto>
            ) {
                Log.i("결과","성공")
                Log.i("결과","닉네임 : ${response.body()?.data?.nickname}")
            }

            override fun onFailure(call: Call<NicknameResponseDto>, t: Throwable) {
                Log.i("결과","실패 : $t")
            }
        })
    }
}