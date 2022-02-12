package com.miso.misoweather.Acitivity.answerAnimationActivity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySelectNicknameBinding
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameData
import com.miso.misoweather.model.DTO.NicknameResponse.NicknameResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.selectArea.SelectAreaActivity
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ActivityAnimationMyanswerBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class AnswerAnimationActivity : MisoActivity() {
    lateinit var binding: ActivityAnimationMyanswerBinding
    lateinit var txt_answer:TextView
    lateinit var img_animation:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimationMyanswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        Handler().postDelayed({
           goToChatMainActivity()
        },2000)
    }

    fun initializeViews() {
        txt_answer = binding.txtAnswer
        img_animation = binding.imgCheckAnimation
    }

    fun goToChatMainActivity()
    {
        startActivity(Intent(this,ChatMainActivity::class.java))
        transferToBack()
        finish()
    }
}