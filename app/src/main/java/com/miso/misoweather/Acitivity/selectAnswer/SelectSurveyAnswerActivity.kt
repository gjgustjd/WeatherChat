package com.miso.misoweather.Acitivity.selectAnswer

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.model.DTO.*
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Fragment.CommentsFragment
import com.miso.misoweather.Fragment.SurveyFragment
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ActivityChatMainBinding
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.TransportManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class SelectSurveyAnswerActivity : MisoActivity() {
    lateinit var binding: ActivitySurveyAnswerBinding
    lateinit var btn_back: ImageButton
    lateinit var btn_action: Button
    lateinit var surveyItem:SurveyItem
    lateinit var recycler_answers:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        surveyItem = intent.getSerializableExtra("SurveyItem") as SurveyItem
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            var intent = Intent(this,ChatMainActivity::class.java)
            intent.putExtra("previousActivity","Home")
            startActivity(intent)
            transferToBack()
            finish()
        }
        btn_action = binding.btnAction
        btn_action.setOnClickListener()
        {

        }
        recycler_answers = binding.recyclerAnswers
    }
}