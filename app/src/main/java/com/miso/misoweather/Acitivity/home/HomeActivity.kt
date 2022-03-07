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
import com.kakao.sdk.user.UserApiClient
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.login.LoginActivity
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
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
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
    lateinit var imgbtnChangeLocaion: ImageButton
    lateinit var firstProgressLayout: ConstraintLayout
    lateinit var secondProgressLayout: ConstraintLayout
    lateinit var thirdProgressLayout: ConstraintLayout
    lateinit var chartLayout: ConstraintLayout
    lateinit var txtEmptyChart: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        getUserInfo()
        getBriefForecast()
        getCommentList()
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
        imgbtnChangeLocaion = binding.imgbtnChangeLocation
        firstProgressLayout = binding.itemFirstLayout
        secondProgressLayout = binding.itemSecondLayout
        thirdProgressLayout = binding.itemThirdLayout
        chartLayout = binding.chartLayout
        txtEmptyChart = binding.txtEmptyChart


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
        imgbtnChangeLocaion.setOnClickListener()
        {
            var intent = Intent(this, SelectRegionActivity::class.java)
            intent.putExtra("for", "change")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    override fun doBack() {
        GeneralConfirmDialog(
            this,
            View.OnClickListener {
                logout()
            },
            "로그아웃 하시겠습니까? \uD83D\uDD13",
            "로그아웃"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    fun logout() {
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e("kakaoLogout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
            } else {
                Log.i("kakaoLogout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                removePreference("accessToken", "socialId", "socialType", "misoToken")
                savePreferences()
                startActivity(Intent(this, LoginActivity::class.java))
                transferToBack()
                finish()
            }
        }
    }

    fun goToChatMainActivity() {
        var isSurveyed = getPreference("isSurveyed")
        var lastSurveyedDate = getPreference("LastSurveyedDate") ?: ""
        var currentDate =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
        if (!isSurveyed.equals("true") || !lastSurveyedDate.equals(currentDate)) {
            var intent = Intent(this, SelectSurveyAnswerActivity::class.java)
            intent.putExtra("isFirstSurvey", true)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        } else {
            var intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToNext()
            finish()
        }
    }

    fun getBriefForecast() {
        var repeatCount = 0
        fun forecastRequest() {
            fun repeatRequest() {
                if (repeatCount < 3) {
                    repeatCount++
                    forecastRequest()
                    Log.i("getBriefForecast", "repeated")
                }
            }

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
                    addPreferencePair(
                        "midScale",
                        if (region.midScale.equals("선택 안 함")) "전체" else region.midScale
                    )
                    addPreferencePair(
                        "smallScale",
                        if (region.smallScale.equals("선택 안 함")) "전체" else region.smallScale
                    )
                    savePreferences()
                    txtLocation.text =
                        region.bigScale + " " + getPreference("midScale") + " " +
                                if (getPreference("midScale").equals("전체")) "" else getPreference("smallScale")
                    txtWeatherEmoji.setText(forecast.sky)
                    txtWeatherDegree.setText(forecast.temperature + "˚")
                    setupSurveyResult()
                } catch (e: Exception) {
                    repeatRequest()
                    e.printStackTrace()
                    Log.i("getBriefForecast", "excepted")
                }
            }, { call, t ->
                Log.i("getBriefForecast", "실패 : $t")
                repeatRequest()
            })
        }
        try {
            forecastRequest()
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
        recyclerChatAdapter =
            RecyclerChatsAdapter(this, commentListResponseDto.data.commentList, false)
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
                if (todaySurveyResultDto.keyList[0] == null) {
                    imgIconCheckFirst.visibility = View.GONE
                    txtEmptyChart.visibility = View.VISIBLE
                } else {
                    txtFirstAnswer.text = todaySurveyResultDto.keyList[0].toString()
                    txtFirstRatio.text = todaySurveyResultDto.valueList[0].toString() + "%"
                    if (txtFirstRatio.text.equals("")) {
                        imgIconCheckFirst.visibility = View.GONE
                        txtEmptyChart.visibility = View.VISIBLE
                    } else
                        imgIconCheckFirst.visibility = View.VISIBLE

                    txtFirstRatio.text = todaySurveyResultDto.valueList[0].toString() + "%"
                    firstProgressLayout.visibility = View.VISIBLE

                    if (todaySurveyResultDto.keyList[1] == null)
                        return@requestApi
                    txtSecondAnswer.text = todaySurveyResultDto.keyList[1].toString()
                    txtSecondRatio.text = todaySurveyResultDto.valueList[1].toString() + "%"
                    secondProgressLayout.visibility = View.VISIBLE

                    if (todaySurveyResultDto.keyList[2] == null)
                        return@requestApi
                    txtThirdAnswer.text = todaySurveyResultDto.keyList[2].toString()
                    txtThirdRatio.text = todaySurveyResultDto.valueList[2].toString() + "%"
                    thirdProgressLayout.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                txtEmptyChart.visibility = View.VISIBLE
                e.printStackTrace()
            }
        }, { call, throwable ->

        })
    }
}