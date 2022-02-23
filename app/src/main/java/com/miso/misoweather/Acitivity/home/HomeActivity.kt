package com.miso.misoweather.Acitivity.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityHomeBinding
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.Forecast.Brief.ForecastBriefResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoResponseDto
import com.miso.misoweather.model.DTO.MemberInfoResponse.MemberInfoDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Acitivity.mypage.MyPageActivity
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.TransportManager
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class HomeActivity : MisoActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var memberInfoResponseDto: MemberInfoResponseDto
    lateinit var forecastBriefResponseDto: ForecastBriefResponseDto
    lateinit var commentListResponseDto: CommentListResponseDto
    lateinit var weatherLayout: ConstraintLayout
    lateinit var txtNickName: TextView
    lateinit var txtEmoji: TextView
    lateinit var txtLocation: TextView
    lateinit var txtWeatherEmoji: TextView
    lateinit var txtWeatherDegree: TextView
    lateinit var btnShowWeatherDetail: ImageButton
    lateinit var btnProfile: ImageButton
    lateinit var btngoToSurvey: ImageButton
    lateinit var recyclerChat: RecyclerView
    lateinit var recyclerChatAdapter: RecyclerChatsAdapter
    lateinit var todaySurveyResultDto: SurveyResult
    lateinit var txtFirstAnswer: TextView
    lateinit var txtSecondAnswer: TextView
    lateinit var txtThirdAnswer: TextView
    lateinit var txtFirstRatio: TextView
    lateinit var txtSecondRatio: TextView
    lateinit var txtThirdRatio: TextView
    lateinit var imgIconCheckFirst: ImageView
    lateinit var firstProgressLayout:ConstraintLayout
    lateinit var secondProgressLayout:ConstraintLayout
    lateinit var thirdProgressLayout:ConstraintLayout
    lateinit var chartLayout:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getUserInfo()
    }

    fun initializeViews() {
        weatherLayout = binding.weatherLayout
        txtNickName = binding.txtNickname
        txtEmoji = binding.txtEmoji
        txtLocation = binding.txtLocation
        txtWeatherDegree = binding.txtDegree
        txtWeatherEmoji = binding.txtWeatherEmoji
        btnShowWeatherDetail = binding.imgbtnShowWeather
        btnProfile = binding.imgbtnProfile
        btngoToSurvey = binding.imgbtnSurvey
        txtFirstAnswer = binding.txtAnswerFirst
        txtSecondAnswer = binding.txtAnswerSecond
        txtThirdAnswer = binding.txtAnswerThird
        txtFirstRatio = binding.txtRatioFirst
        txtSecondRatio = binding.txtRatioSecond
        txtThirdRatio = binding.txtRatioThird
        imgIconCheckFirst = binding.imgIconFirst
        firstProgressLayout = binding.itemFirstLayout
        secondProgressLayout = binding.itemSecondLayout
        thirdProgressLayout = binding.itemThirdLayout
        chartLayout = binding.chartLayout

        chartLayout.setOnClickListener()
        {
            goToChatMainActivity()
        }

        btngoToSurvey.setOnClickListener()
        {
            goToChatMainActivity()
        }
        weatherLayout.setOnClickListener()
        {
            startActivity(Intent(this, WeatherDetailActivity::class.java))
            transferToNext()
            finish()
        }
        btnProfile.setOnClickListener()
        {
            startActivity(Intent(this, MyPageActivity::class.java))
            transferToNext()
            finish()
        }
        recyclerChat = binding.recyclerChats
    }
    fun goToChatMainActivity()
    {
        var isSurveyed = getPreference("isSurveyed")
        var lastSurveyedDate = getPreference("LastSurveyedDate")?:""
        var currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
        if(!isSurveyed.equals("true")|| !lastSurveyedDate.equals(currentDate))
        {
            var intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.putExtra("isFirstSurvey", true)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        }
        else {
            var intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    fun getBriefForecast() {
        try {
            val callBriefForecast =
                TransportManager.getRetrofitApiObject<ForecastBriefResponseDto>()
                    .getBriefForecast(getPreference("defaultRegionId")!!.toInt())

            TransportManager.requestApi(callBriefForecast, { call, response ->
                try {
                    Log.i("결과", "성공")
                    forecastBriefResponseDto = response.body()!!
                    var forecast = forecastBriefResponseDto.data.forecast
                    var region = forecastBriefResponseDto.data.region
                    addPreferencePair("bigScale", region.bigScale)
                    addPreferencePair("midScale", region.midScale)
                    addPreferencePair("smallScale", region.smallScale)
                    savePreferences()
                    txtLocation.text =
                        region.bigScale + " " + region.midScale + " " + region.smallScale
                    txtWeatherEmoji.setText(forecast.sky)
                    txtWeatherDegree.setText(forecast.temperature + "˚")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, { call, t ->
                Log.i("결과", "실패 : $t")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCommentList() {
        val callBriefForecast = TransportManager.getRetrofitApiObject<ForecastBriefResponseDto>()
            .getCommentList(null, 5)
        TransportManager.requestApi(callBriefForecast, { call, response ->
            try {
                Log.i("결과", "성공")
                commentListResponseDto = response.body()!!
                setRecyclerChats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { call, t ->
            Log.i("결과", "실패 : $t")
        })
    }

    fun setRecyclerChats() {
        recyclerChatAdapter = RecyclerChatsAdapter(this, commentListResponseDto.data.commentList,false)
        recyclerChat.adapter = recyclerChatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(this)
    }

    fun getUserInfo() {
        val callgetUserInfo = TransportManager.getRetrofitApiObject<MemberInfoResponseDto>()
            .getUserInfo(getPreference("misoToken")!!)
        TransportManager.requestApi(callgetUserInfo, { call, response ->
            try {
                Log.i("결과", "성공")
                memberInfoResponseDto = response.body()!!
                var memberInfo = memberInfoResponseDto.data
                txtNickName.setText(memberInfo.nickname + "님!")
                txtEmoji.setText(memberInfo.emoji)
                addPreferencePair(
                    "defaultRegionId",
                    this@HomeActivity.memberInfoResponseDto.data.regionId.toString()
                )
                addPreferencePair("emoji", memberInfo.emoji)
                addPreferencePair("nickname", memberInfo.nickname)
                savePreferences()
                getBriefForecast()
                setupSurveyResult()
                getCommentList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { call, t ->
            Log.i("결과", "실패 : $t")
        })
    }

    fun setupSurveyResult() {
        val callGetSurveyResult =
            TransportManager.getRetrofitApiObject<SurveyResultResponseDto>()
                .getSurveyResults(getBigShortScale(getPreference("bigScale")!!))

        TransportManager.requestApi(callGetSurveyResult, { call, reponse ->
            try {
                todaySurveyResultDto = reponse.body()!!.data.responseList.first { it.surveyId == 2 }
                txtFirstAnswer.text = todaySurveyResultDto.keyList.get(0).toString()
                txtFirstRatio.text = todaySurveyResultDto.valueList.get(0).toString() + "%"
                if(txtFirstRatio.text.equals(""))
                    imgIconCheckFirst.visibility = View.GONE
                else
                    imgIconCheckFirst.visibility = View.VISIBLE

                txtFirstRatio.text = todaySurveyResultDto.valueList.get(0).toString() + "%"
                firstProgressLayout.visibility= View.VISIBLE

                txtSecondAnswer.text = todaySurveyResultDto.keyList.get(1).toString()
                txtSecondRatio.text = todaySurveyResultDto.valueList.get(1).toString() + "%"
                secondProgressLayout.visibility= View.VISIBLE

                txtThirdAnswer.text = todaySurveyResultDto.keyList.get(2).toString()
                txtThirdRatio.text = todaySurveyResultDto.valueList.get(2).toString() + "%"
                thirdProgressLayout.visibility= View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { call, throwable ->

        })
    }
}